package unibz.it.PatternChatbot.state;

import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import unibz.it.PatternChatbot.model.Response;
import unibz.it.PatternChatbot.utility.ChatHelperUtility;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

public class IntentDiscoverAskingForHelpState extends State{

    public IntentDiscoverAskingForHelpState(ChatHelperUtility chatHelper){
        super(chatHelper);
        this.InitializationMessage ="How can I help you?";
        this.createInitMessage();
    }
    @Override
    public void handleError() {

    }

    @Override
    public Optional<State> handleInput(String chatInput, MessageList chat, IFrame webpageIFrame) {
        for (Map.Entry<Pattern, Response> set :
                this.Rules.entrySet()) {
            //Try to match a Rule
            if(set.getKey().matcher(chatInput).find()) {
                return Optional.ofNullable(set.getValue().responseAction(chatInput, chat, webpageIFrame));
            }
        }
        return null;
    }

    @Override
    public void setupResponses() {
        //1. Request for a Guided Search
        //old regex "(?i)(guide|assist|help).*search|(find|discover).*pattern"
        this.Rules.put(Pattern.compile("(?i)\\b(1|request|need|want|ask|help)\\b.*\\b(guide|guided|assisted)?\\b.*\\b(search)\\b|(?i)\\b(request|need|want|ask|help)\\b.*\\b(guide|guided|assisted)\\b.*\\b(search)\\b|1.*|1\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                return new GuidedSearchState(chatHelper);
            }
        });
        //2. Request for the Nearest Pattern to a Given Pattern
        //old regex (?i)(nearest|closest|similar|related).*pattern.*(to|like).*
        this.Rules.put(Pattern.compile("(?i)\\b(2|request|find|need|want|ask)\\b.*\\b(nearest|closest|similar)\\b.*\\b(pattern)\\b.*\\b(to)?\\b.*\\b(given|specific|mentioned)?\\b.*\\b(pattern)\\b|" +
                        "(?i)\\b(request|find|need|want|ask)\\b.*\\b(nearest|closest|similar)\\b.*\\b(pattern)\\b.*\\b(to)\\b.*\\b(given|specific|mentioned)\\b.*\\b(pattern)\\b|2.*|2\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chatHelper.createPatteraChatMessage("2. Request for the Nearest Pattern to a Given Pattern");
                //TODO go into correct state
                return new GuidedSearchState(chatHelper);
            }
        });
        //3. Request for a List of all available Pattern
        //old regex (?i)(list|show|display|all).*pattern(s)?
        this.Rules.put(Pattern.compile("(?i)\\b(3|request|need|want|ask|show|give)?\\b.*\\b(list)?\\b.*\\b(all|available)?\\b.*\\b(patterns|pattern)\\b|" +
                        "(?i)\\b(request|need|want|ask|show|give)\\b.*\\b(list)\\b.*\\b(all|available)\\b.*\\b(patterns)\\b|3.*|3\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chatHelper.createPatteraChatMessage("3. Request for a List of All Patterns");
                //TODO go into correct state
                return new GuidedSearchState(chatHelper);
            }
        });
        //4. Request for Information on a Specific Pattern
        //old regex (?i)(info|information|details|describe|tell\\sme).*pattern.*(\\b[A-Za-z]+\\b)
        this.Rules.put(Pattern.compile("(?i)\\b(4|request|need|want|ask|show|give)\\b.*\\b(info|information)?\\b.*\\b(on|about)?\\b.*\\b(specific|particular)?\\b.*\\b(pattern)\\b|" +
                        "(?i)\\b(request|need|want|ask|show|give)\\b.*\\b(info|information)\\b.*\\b(on|about)\\b.*\\b(specific|particular)\\b.*\\b(pattern)\\b|4.*|4\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chatHelper.createPatteraChatMessage("4. Request for Information on a Specific Pattern");
                //TODO go into correct state
                return new GuidedSearchState(chatHelper);
            }
        });
        //Fallback
        //TODO redo fallback
        this.Rules.put(Pattern.compile(".*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chatHelper.createPatteraChatMessage("5. Request for Help Without Specific Intent (Fallback to General Help)");
                //TODO go into correct state
                return new GuidedSearchState(chatHelper);
            }
        });
    }

    @Override
    public void setupOptions() {
        this.Options.add("1. Request for a Guided Search");
        this.Options.add("2. Request for the Nearest Pattern to a Given Pattern");
        this.Options.add("3. Request for a List of all available Pattern");
        this.Options.add("4. Request for Information on a Specific Pattern");
    }

    @Override
    public void createInitMessage() {
        StringBuilder startPhrase = new StringBuilder(this.InitializationMessage);
        startPhrase.append("\nOptions:");
        for(String option : this.Options) {
            startPhrase.append("\n").append(option);
        }
        chatHelper.createChatMessage(startPhrase.toString());
    }
}

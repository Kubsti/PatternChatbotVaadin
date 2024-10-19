package unibz.it.PatternChatbot.state;

import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.server.VaadinSession;
import unibz.it.PatternChatbot.model.PatternQuestion;
import unibz.it.PatternChatbot.model.Response;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

public class IntentDiscoverAskingForHelpState extends State{

    public IntentDiscoverAskingForHelpState(){
        this.Rules = new LinkedHashMap<Pattern, Response>();
        this.Options = new ArrayList<String>();
        this.InitializationMessage ="How can I help you?";
        this.setupResponses();
        this.setupOptions();
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
        //Todo catch error and return correct state
        return null;
    }

    @Override
    public void setupResponses() {
        //1. Request for a Guided Search
        //old regex "(?i)(guide|assist|help).*search|(find|discover).*pattern"
        this.Rules.put(Pattern.compile("(?i)\\b(1|request|need|want|ask|help)?\\b.*\\b(guide|guided|assisted)?\\b.*\\b(search)\\b|(?i)\\b(request|need|want|ask|help)\\b.*\\b(guide|guided|assisted)\\b.*\\b(search)\\b|1.*|1\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
//                PatternQuestion question = (PatternQuestion) VaadinSession.getCurrent().getAttribute("nextQuestion");
//                List<MessageListItem> messages = new ArrayList<MessageListItem>();
//                messages.addAll(chat.getItems());
//                messages.add(new MessageListItem(
//                        question.getQuestion(),
//                        Instant.now(), "Pattera"));
//                chat.setItems(messages);
                return new GuidedSearchState();
            }
        });
        //2. Request for the Nearest Pattern to a Given Pattern
        //old regex (?i)(nearest|closest|similar|related).*pattern.*(to|like).*
        this.Rules.put(Pattern.compile("(?i)\\b(2|request|find|need|want|ask)?\\b.*\\b(nearest|closest|similar)?\\b.*\\b(pattern)\\b.*\\b(to)?\\b.*\\b(given|specific|mentioned)?\\b.*\\b(pattern)?\\b|" +
                        "(?i)\\b(request|find|need|want|ask)\\b.*\\b(nearest|closest|similar)\\b.*\\b(pattern)\\b.*\\b(to)\\b.*\\b(given|specific|mentioned)\\b.*\\b(pattern)\\b|2.*|2\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chat.getItems().add(new MessageListItem(
                        "2. Request for the Nearest Pattern to a Given Pattern",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new GuidedSearchState();
            }
        });
        //3. Request for a List of all available Pattern
        //old regex (?i)(list|show|display|all).*pattern(s)?
        this.Rules.put(Pattern.compile("(?i)\\b(3|request|need|want|ask|show|give)?\\b.*\\b(list)?\\b.*\\b(all|available)?\\b.*\\b(patterns|pattern)?\\b|" +
                        "(?i)\\b(request|need|want|ask|show|give)\\b.*\\b(list)\\b.*\\b(all|available)\\b.*\\b(patterns)\\b|3.*|3\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chat.getItems().add(new MessageListItem(
                        "3. Request for a List of All Patterns",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new GuidedSearchState();
            }
        });
        //4. Request for Information on a Specific Pattern
        //old regex (?i)(info|information|details|describe|tell\\sme).*pattern.*(\\b[A-Za-z]+\\b)
        this.Rules.put(Pattern.compile("(?i)\\b(4|request|need|want|ask|show|give)?\\b.*\\b(info|information)?\\b.*\\b(on|about)?\\b.*\\b(specific|particular)?\\b.*\\b(pattern)?\\b|" +
                        "(?i)\\b(request|need|want|ask|show|give)\\b.*\\b(info|information)\\b.*\\b(on|about)\\b.*\\b(specific|particular)\\b.*\\b(pattern)\\b|4.*|4\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chat.getItems().add(new MessageListItem(
                        "4. Request for Information on a Specific Pattern",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new GuidedSearchState();
            }
        });
        //Fallback
        //TODO redo fallback
        this.Rules.put(Pattern.compile("(?i)(help|assist|support).*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chat.getItems().add(new MessageListItem(
                        "5. Request for Help Without Specific Intent (Fallback to General Help)",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new GuidedSearchState();
            }
        });
    }

    @Override
    public void setupOptions() {
    //TODO to  be implemented
    }
}

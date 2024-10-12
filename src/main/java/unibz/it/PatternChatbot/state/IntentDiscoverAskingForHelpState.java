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
        this.setupResponses();
        this.setupOptions();
    }
    @Override
    public void handleError() {

    }

    @Override
    public State handleInput(String chatInput,  MessageList chat, IFrame webpageIFrame) {
        for (Map.Entry<Pattern, Response> set :
                this.Rules.entrySet()) {
            //Try to match a Rule
            if(set.getKey().matcher(chatInput).find()) {
                return set.getValue().responseAction(chatInput,chat,webpageIFrame);
            }
        }
        //Todo catch error and return correct state
        return null;
    }

    @Override
    public void setupResponses() {
        //1. Request for a Guided Search
        this.Rules.put(Pattern.compile("(?i)(guide|assist|help).*search|(find|discover).*pattern"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                PatternQuestion question = (PatternQuestion) VaadinSession.getCurrent().getAttribute("nextQuestion");
                List<MessageListItem> messages = new ArrayList<MessageListItem>();
                messages.addAll(chat.getItems());
                messages.add(new MessageListItem(
                        question.getQuestion(),
                        Instant.now(), "Pattera"));
                chat.setItems(messages);
                return new SearchState();
            }
        });
        //2. Request for the Nearest Pattern to a Given Pattern
        this.Rules.put(Pattern.compile("(?i)(nearest|closest|similar|related).*pattern.*(to|like).*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chat.getItems().add(new MessageListItem(
                        "2. Request for the Nearest Pattern to a Given Pattern",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new SearchState();
            }
        });
        //3. Request for a List of All Patterns
        this.Rules.put(Pattern.compile("(?i)(list|show|display|all).*pattern(s)?"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chat.getItems().add(new MessageListItem(
                        "3. Request for a List of All Patterns",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new SearchState();
            }
        });
        //4. Request for Information on a Specific Pattern
        this.Rules.put(Pattern.compile("(?i)(info|information|details|describe|tell\\\\sme).*pattern.*(\\\\b[A-Za-z]+\\\\b)"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chat.getItems().add(new MessageListItem(
                        "4. Request for Information on a Specific Pattern",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new SearchState();
            }
        });
        //5. Request for Help Without Specific Intent (Fallback to General Help)
        this.Rules.put(Pattern.compile("(?i)(help|assist|support).*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chat.getItems().add(new MessageListItem(
                        "5. Request for Help Without Specific Intent (Fallback to General Help)",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new SearchState();
            }
        });
    }

    @Override
    public void setupOptions() {
    //TODO to  be implemented
    }
}

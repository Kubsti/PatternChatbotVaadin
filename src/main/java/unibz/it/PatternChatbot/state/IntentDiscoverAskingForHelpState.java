package unibz.it.PatternChatbot.state;

import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.server.VaadinSession;
import unibz.it.PatternChatbot.model.PatternQuestion;
import unibz.it.PatternChatbot.model.Response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class IntentDiscoverAskingForHelpState extends State{

    public IntentDiscoverAskingForHelpState(){
        this.Rules = new HashMap<Pattern, Response>();
        this.Options = new ArrayList<String>();
        this.setupResponses();
        this.setupOptions();
    }
    @Override
    public void handleError() {

    }

    @Override
    public void handleInput(String chatInput, State currState, MessageList chat, IFrame webpageIFrame) {
        for (Map.Entry<Pattern, Response> set :
                this.Rules.entrySet()) {
            //Try to match a Rule
            if(set.getKey().matcher(chatInput).find()) {
                set.getValue().responseAction(chatInput,currState,chat,webpageIFrame);
                break;
            }
        }
    }

    @Override
    public void setupResponses() {
        //1. Request for a Guided Search
        this.Rules.put(Pattern.compile("(?i)(guide|assist|help).*search|(find|discover).*pattern"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public void responseAction(String input, State currState, MessageList chat, IFrame webpageIFrame) {
                currState = new SearchState();
                PatternQuestion question = (PatternQuestion) VaadinSession.getCurrent().getAttribute("nextQuestion");
                VaadinSession.getCurrent().getAttribute("nextQuestion");
                chat.setItems(new MessageListItem(
                        question.getQuestion(),
                        Instant.now(), "Pattera"));
                currState = new SearchState();
            }
        });
        //2. Request for the Nearest Pattern to a Given Pattern
        this.Rules.put(Pattern.compile("(?i)(nearest|closest|similar|related).*pattern.*(to|like).*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public void responseAction(String input, State currState, MessageList chat, IFrame webpageIFrame) {
                chat.setItems(new MessageListItem(
                        "2. Request for the Nearest Pattern to a Given Pattern",
                        Instant.now(), "Pattera"));
            }
        });
        //3. Request for a List of All Patterns
        this.Rules.put(Pattern.compile("(?i)(list|show|display|all).*pattern(s)?"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public void responseAction(String input, State currState, MessageList chat, IFrame webpageIFrame) {
                chat.setItems(new MessageListItem(
                        "3. Request for a List of All Patterns",
                        Instant.now(), "Pattera"));
            }
        });
        //4. Request for Information on a Specific Pattern
        this.Rules.put(Pattern.compile("(?i)(info|information|details|describe|tell\\\\sme).*pattern.*(\\\\b[A-Za-z]+\\\\b)"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public void responseAction(String input, State currState, MessageList chat, IFrame webpageIFrame) {
                chat.setItems(new MessageListItem(
                        "4. Request for Information on a Specific Pattern",
                        Instant.now(), "Pattera"));
            }
        });
        //5. Request for Help Without Specific Intent (Fallback to General Help)
        this.Rules.put(Pattern.compile("(?i)(help|assist|support).*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public void responseAction(String input, State currState, MessageList chat, IFrame webpageIFrame) {
                chat.setItems(new MessageListItem(
                        "5. Request for Help Without Specific Intent (Fallback to General Help)",
                        Instant.now(), "Pattera"));
            }
        });
    }

    @Override
    public void setupOptions() {
    //TODO to  be implemented
    }
}

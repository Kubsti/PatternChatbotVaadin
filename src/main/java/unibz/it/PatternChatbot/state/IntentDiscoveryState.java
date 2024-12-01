package unibz.it.PatternChatbot.state;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.vaadin.flow.component.messages.MessageListItem;
import unibz.it.PatternChatbot.model.*;
import unibz.it.PatternChatbot.ui.ErrorDialog;
import unibz.it.PatternChatbot.utility.UiHelperUtility;


import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Pattern;
public class IntentDiscoveryState extends State {

    public IntentDiscoveryState(UiHelperUtility chatHelper, boolean showInitMesssge){
        super(chatHelper,"Hello I'm Pattera here to help you find the right pattern for your problem. How can I help you today?",showInitMesssge);
    }

    @Override
    public void setupResponses() {
        //1. Asking for help
        this.Rules.put(Pattern.compile("1.*|1\\."
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) throws StateException {
                //TODO check for what user wants help for
                List<MessageListItem> messages = new ArrayList<MessageListItem>();
                //chatHelper.createPatteraChatMessage("1. Asking for help entered");
                return new IntentDiscoverAskingForHelpState(chatHelper, true);
            }
        });
        //2. List all available patterns
        this.Rules.put(Pattern.compile("2.*|2\\."
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                return new ListAllPatternState(chatHelper, true);
            }
        });
        //3. Request for the Nearest Pattern to a Given Pattern
        this.Rules.put(Pattern.compile("3.*|3\\.|3"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input,ArrayList<String> stateOptions) {
                return new NearestPatternState(chatHelper, true);
            }
        });
        //Fallback
        this.Rules.put(Pattern.compile(".*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                chatHelper.createPatteraChatMessage("Sorry i could not understand what your intent is could you please try again.");
                return new IntentDiscoveryState(chatHelper, false);
            }
        });
        //        //3. Explaining a problem
//        this.Rules.put(Pattern.compile("(?i)(i\\shave\\s[a-z\\s]*problem|problem\\swith|issue\\swith|facing\\s[a-z\\s]*problem|difficulty|challenge)"
//                , Pattern.CASE_INSENSITIVE), new Response() {
//            @Override
//            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
//                List<MessageListItem> messages = new ArrayList<MessageListItem>();
//                messages.addAll(chat.getItems());
//                messages.add(new MessageListItem(
//                        "3. Explaining a problem",
//                        Instant.now(), "Pattera"));
//                chat.setItems(messages);
//
////                List<MessageListItem> test = chat.getItems();
////               test.add(new MessageListItem(
////                       "3. Explaining a problem",
////                       Instant.now(), "Pattera"));
////               chat.setItems(test);
////                chat.setItems(chat.getItems().add(new MessageListItem(
////                        "3. Explaining a problem",
////                        Instant.now(), "Pattera")));
//                //TODO go into correct state
//                return new IntentDiscoveryState();
//            }
//        });
        //4. General Greeting/Intro
//        this.Rules.put(Pattern.compile("(?i)(hello|hi|hey|good\\s(morning|evening|day))"
//                , Pattern.CASE_INSENSITIVE), new Response() {
//            @Override
//            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
//                chat.getItems().add(new MessageListItem(
//                        "Hi, how can I help you?",
//                        Instant.now(), "Pattera"));
//                //TODO go into correct state
//                return new IntentDiscoveryState();
//            }
//        });
    }

    @Override
    public void setupOptions() {
        this.Options.add("1. Help to find a pattern");
        this.Options.add("2. List all available patterns");
        this.Options.add("3. Request for the Nearest Pattern to a Given Pattern");
        //this.Options.add("3. Explaining a problem");
        //this.Options.add("3. Asking what Pattera can do");
        //this.Options.add("4. Requesting infos about a specific pattern type (e.g., design pattern, behavior pattern)");
        //this.Options.add("5. Requesting a specific pattern");
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

    @Override
    public void setupExceptions() {
        //TODO setup exceptions for state
//        this.Exceptions.put("TestException",
//                (String input) -> {
//            chatHelper.createPatteraChatMessage("Error occurred");
//            ErrorDialog.showError("Error occurred");
//            return null;
//        }
//        );
    }
}

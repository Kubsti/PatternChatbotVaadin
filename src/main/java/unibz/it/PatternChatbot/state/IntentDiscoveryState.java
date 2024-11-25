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
        //Old regex "(?i)(help|assist|support|need.*help|find.*solution)"
        this.Rules.put(Pattern.compile("(?i)(1 help to find a pattern|Help to find a pattern|help me find a pattern|1. aHelp to find a pattern)|(?i)\\b(help|assist|find|search)\\b.*\\b(pattern|model|structure)\\b|1.*|1\\."
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
        //old regex "(?i)(suggest.*pattern|recommend.*pattern|find.*pattern|pattern.*(help|needed|solution))"
        this.Rules.put(Pattern.compile("(?i)\\b(2|list|show|display)?\\b.*\\b(all|available|every)?\\b.*\\b(patterns|pattern)?\\b|(?i)\\b(list|show|display)\\b.*\\b(all|available|every)\\b.*\\b(patterns|pattern)\\b|2.*|2\\."
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                DesignPatterns response = httpHelper.getAllPattern();
                if (response != null) {
                    if(!response.getPatterns().isEmpty()){
                        StringBuilder startPhrase = new StringBuilder();
                        startPhrase.append("Pattern:");
                        response.getPatterns().forEach((pattern -> startPhrase.append("\n").append(pattern.name)));
                        chatHelper.createPatteraChatMessage(startPhrase.toString());
                    }else{
                        chatHelper.createPatteraChatMessage("Sorry it seems I have no patterns stored at the moment, but maybe you could give me some ;).");
                    }
                }
                return new IntentDiscoveryState(chatHelper, false);
            }
        });
        //3. Asking what Pattera can do
        //old regex (?i)(what\scan\syou\sdo|how\scan\syou\shelp|what\sdo\syou\soffer|what\sservices)
        this.Rules.put(Pattern.compile("(?i)\\b(3|what)\\b.*\\b(can)\\b.*\\b(pattera)\\b.*\\b(do)\\b|(?i)\\b(what)\\b.*\\b(can)\\b.*\\b(pattera)\\b.*\\b(do)\\b|3.*|3\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                chatHelper.createPatteraChatMessage("5. Asking what Pattera can do");
                //TODO go into correct state
                return new IntentDiscoveryState(chatHelper,false);
            }
        });
        //4. Requesting infos about a specific pattern type (e.g., design pattern, behavior pattern)
        //old regex "(?i)(design\\s(pattern|solution)|behavioral\\s(pattern|solution)|structural\\s(pattern|solution)|creational\\s(pattern|solution))"
        this.Rules.put(Pattern.compile("(?i)\b(4|request|ask|need|info|information|details|about)\b.*\b(design|behavior|creational|structural|behavioral)?\b.*\b(pattern)\b" +
                        "|(?i)\\b(request|ask|need|tell me|give me|show)\\b.*\\b(info|information|details|about)\\b.*\\b(design|behavior|creational|structural|behavioral)?\\b.*\\b(pattern)\\b\n|4.*|4\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                chatHelper.createPatteraChatMessage("6. Requesting specific pattern type (e.g., design pattern, behavior pattern)");
                //TODO go into correct state
                return new IntentDiscoveryState(chatHelper, false);
            }
        });
        //7. Fallback
        //old regex (?i)(design\s(pattern|solution)|behavioral\s(pattern|solution)|structural\s(pattern|solution)|creational\s(pattern|solution))
        //TODO redo fallback or make it better if needed
        this.Rules.put(Pattern.compile(".*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                //Todo implement fallback
                //TODO go into correct state
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
        //this.Options.add("3. Explaining a problem");
        this.Options.add("3. Asking what Pattera can do");
        this.Options.add("4. Requesting infos about a specific pattern type (e.g., design pattern, behavior pattern)");
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

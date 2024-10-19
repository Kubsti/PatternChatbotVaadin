package unibz.it.PatternChatbot.state;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import unibz.it.PatternChatbot.model.Response;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
public class IntentDiscoveryState extends State{

    public IntentDiscoveryState(){
        this.Rules = new LinkedHashMap<Pattern, Response>();
        this.Options = new ArrayList<String>();
        this.InitializationMessage ="Hello I'm Pattera here to help you find the right pattern for your problem. How can I help you today?";
        this.setupResponses();
        this.setupOptions();
    }
    @Override
    public void handleError() {

    }

    @Override
    public Optional<State> handleInput(String chatInput, MessageList chat, IFrame webpageIFrame) {
        //TODO implement case/logic for no match or create a fallback
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
        //1. Asking for help
        //Old regex "(?i)(help|assist|support|need.*help|find.*solution)"
        this.Rules.put(Pattern.compile("(?i)(1 help to find a pattern|Help to find a pattern|help me find a pattern|1. aHelp to find a pattern)|(?i)\\b(help|assist|find|search)\\b.*\\b(pattern|model|structure)\\b|1.*|1\\."
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                //TODO check for what user wants help for
                List<MessageListItem> messages = new ArrayList<MessageListItem>();
                messages.addAll(chat.getItems());
                messages.add(new MessageListItem(
                        "1. Asking for help entered",
                        Instant.now(), "Pattera"));
                chat.setItems(messages);
//                chat.getItems().add(new MessageListItem(
//                        "1. Asking for help entered",
//                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new IntentDiscoverAskingForHelpState();
            }
        });
        //2. List all available patterns
        //old regex "(?i)(suggest.*pattern|recommend.*pattern|find.*pattern|pattern.*(help|needed|solution))"
        this.Rules.put(Pattern.compile("(?i)\\b(2|list|show|display)?\\b.*\\b(all|available|every)?\\b.*\\b(patterns|pattern)?\\b|(?i)\\b(list|show|display)\\b.*\\b(all|available|every)\\b.*\\b(patterns|pattern)\\b|2.*|2\\."
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                List<MessageListItem> messages = new ArrayList<MessageListItem>();
                messages.addAll(chat.getItems());
                messages.add(new MessageListItem(
                        "2. Asking for patterns",
                        Instant.now(), "Pattera"));
                chat.setItems(messages);
//                chat.getItems().add(new MessageListItem(
//                        "2. Asking for patterns",
//                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new IntentDiscoveryState();
            }
        });
        //3. Asking what Pattera can do
        //old regex (?i)(what\scan\syou\sdo|how\scan\syou\shelp|what\sdo\syou\soffer|what\sservices)
        this.Rules.put(Pattern.compile("(?i)\\b(3|what)?\\b.*\\b(can)?\\b.*\\b(pattera)\\b.*\\b(do)?\\b|(?i)\\b(what)\\b.*\\b(can)\\b.*\\b(pattera)\\b.*\\b(do)\\b|3.*|3\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input,  MessageList chat, IFrame webpageIFrame) {
                chat.getItems().add(new MessageListItem(
                        "5. Asking what Pattera can do",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new IntentDiscoveryState();
            }
        });
        //4. Requesting infos about a specific pattern type (e.g., design pattern, behavior pattern)
        //old regex "(?i)(design\\s(pattern|solution)|behavioral\\s(pattern|solution)|structural\\s(pattern|solution)|creational\\s(pattern|solution))"
        this.Rules.put(Pattern.compile("(?i)\b(4|request|ask|need|info|information|details|about)?\b.*\b(design|behavior|creational|structural|behavioral)?\b.*\b(pattern)\b" +
                        "|(?i)\\b(request|ask|need|tell me|give me|show)\\b.*\\b(info|information|details|about)\\b.*\\b(design|behavior|creational|structural|behavioral)?\\b.*\\b(pattern)\\b\n|4.*|4\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chat.getItems().add(new MessageListItem(
                        "6. Requesting specific pattern type (e.g., design pattern, behavior pattern)",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new IntentDiscoveryState();
            }
        });
        //7. Fallback
        //old regex (?i)(design\s(pattern|solution)|behavioral\s(pattern|solution)|structural\s(pattern|solution)|creational\s(pattern|solution))
        //TODO redo fallback
        this.Rules.put(Pattern.compile(".*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                //Todo implement fallback
                //TODO go into correct state
                return new IntentDiscoveryState();
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
}

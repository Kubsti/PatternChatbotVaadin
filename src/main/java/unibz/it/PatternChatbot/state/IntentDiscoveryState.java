package unibz.it.PatternChatbot.state;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import unibz.it.PatternChatbot.model.Response;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
public class IntentDiscoveryState extends State{

    public IntentDiscoveryState(){
        this.Rules = new LinkedHashMap<Pattern, Response>();
        this.Options = new ArrayList<String>();
        this.setupResponses();
        this.setupOptions();
    }
    @Override
    public void handleError() {

    }

    @Override
    public State handleInput(String chatInput, MessageList chat, IFrame webpageIFrame) {
        //TODO implement case/logic for no match or create a fallback
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
        //1. Asking for help
        this.Rules.put(Pattern.compile("(?i)(help|assist|support|need.*help|find.*solution)"
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
        //2. Asking for patterns
        this.Rules.put(Pattern.compile("(?i)(suggest.*pattern|recommend.*pattern|find.*pattern|pattern.*(help|needed|solution))"
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
        //3. Explaining a problem
        this.Rules.put(Pattern.compile("(?i)(i\\shave\\s[a-z\\s]*problem|problem\\swith|issue\\swith|facing\\s[a-z\\s]*problem|difficulty|challenge)"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                List<MessageListItem> messages = new ArrayList<MessageListItem>();
                messages.addAll(chat.getItems());
                messages.add(new MessageListItem(
                        "3. Explaining a problem",
                        Instant.now(), "Pattera"));
                chat.setItems(messages);

//                List<MessageListItem> test = chat.getItems();
//               test.add(new MessageListItem(
//                       "3. Explaining a problem",
//                       Instant.now(), "Pattera"));
//               chat.setItems(test);
//                chat.setItems(chat.getItems().add(new MessageListItem(
//                        "3. Explaining a problem",
//                        Instant.now(), "Pattera")));
                //TODO go into correct state
                return new IntentDiscoveryState();
            }
        });
        //4. General Greeting/Intro
        this.Rules.put(Pattern.compile("(?i)(hello|hi|hey|good\\s(morning|evening|day))"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chat.getItems().add(new MessageListItem(
                        "Hi, how can I help you?",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new IntentDiscoveryState();
            }
        });
        //5. Asking what Pattera can do
        this.Rules.put(Pattern.compile("(?i)(what\\scan\\syou\\sdo|how\\scan\\syou\\shelp|what\\sdo\\syou\\soffer|what\\sservices)"
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
        //6. Requesting specific pattern type (e.g., design pattern, behavior pattern)
        this.Rules.put(Pattern.compile("(?i)(design\\s(pattern|solution)|behavioral\\s(pattern|solution)|structural\\s(pattern|solution)|creational\\s(pattern|solution))"
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
        this.Rules.put(Pattern.compile("(?i)(design\\s(pattern|solution)|behavioral\\s(pattern|solution)|structural\\s(pattern|solution)|creational\\s(pattern|solution))"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                //Todo implement fallback
                //TODO go into correct state
                return new IntentDiscoveryState();
            }
        });
    }

    @Override
    public void setupOptions() {
        this.Options.add("1. do a guided search to find a fitting pattern for your problem");
        this.Options.add("2. recommend similar pattern to a given pattern(not implemented concurrently)");
        this.Options.add("3. try to find a pattern for a problem you gave  me(not implemented concurrently");
        this.Options.add("4. get the infos for a pattern you provided");
        //this.Options.add("5. Requesting a specific pattern");
    }
}

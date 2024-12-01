package unibz.it.PatternChatbot.state;

import unibz.it.PatternChatbot.model.DesignPatterns;
import unibz.it.PatternChatbot.model.Response;
import unibz.it.PatternChatbot.model.State;
import unibz.it.PatternChatbot.utility.UiHelperUtility;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class FoundPatternState extends State {
    public FoundPatternState(UiHelperUtility chatHelper, boolean showInitMessage) {
        super(chatHelper,"", showInitMessage);
    }

    @Override
    public void setupResponses() {
        //1. Restart search
        this.Rules.put(Pattern.compile("1.*|1\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                httpHelper.intializeChatbot();
                return new IntentDiscoveryState(chatHelper, true);
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

        //Fallback
        this.Rules.put(Pattern.compile(".*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                chatHelper.createPatteraChatMessage("Sorry i could not understand what your intent is could you please try again.");
                return new FoundPatternState(chatHelper, false);
            }
        });
    }

    @Override
    public void setupOptions() {
        this.Options.add("1. Restart search");
        this.Options.add("2. List all available patterns");
    }

    @Override
    public void createInitMessage() {
        StringBuilder startPhrase = new StringBuilder();
        startPhrase.append("\nOptions:");
        for(String option : this.Options) {
            startPhrase.append("\n").append(option);
        }
        chatHelper.createPatteraChatMessage(startPhrase.toString());
    }

    @Override
    public void setupExceptions() {
    }
}

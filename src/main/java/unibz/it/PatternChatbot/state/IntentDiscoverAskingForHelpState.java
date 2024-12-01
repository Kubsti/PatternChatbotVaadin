package unibz.it.PatternChatbot.state;

import unibz.it.PatternChatbot.model.DesignPatterns;
import unibz.it.PatternChatbot.model.Response;
import unibz.it.PatternChatbot.model.State;
import unibz.it.PatternChatbot.utility.UiHelperUtility;

import java.util.*;
import java.util.regex.Pattern;

public class IntentDiscoverAskingForHelpState extends State {

    public IntentDiscoverAskingForHelpState(UiHelperUtility chatHelper, boolean showInitMesssge){
        super(chatHelper,"How can I help you?", showInitMesssge);
    }

    @Override
    public void setupResponses() {
        //1. Request for a Guided Search
        this.Rules.put(Pattern.compile("1.*|1\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                return new GuidedSearchState(chatHelper, true);
            }
        });
        //2. Request for the Nearest Pattern to a Given Pattern
        this.Rules.put(Pattern.compile("2.*|2\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input,ArrayList<String> stateOptions) {
                return new NearestPatternState(chatHelper, true);
            }
        });
        //3. Request for a List of all available Pattern
        this.Rules.put(Pattern.compile("3.*|3\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input,ArrayList<String> stateOptions) {
                DesignPatterns response = httpHelper.getAllPattern();
                if (response != null) {
                    if(!response.getPatterns().isEmpty()){
                        StringBuilder startPhrase = new StringBuilder();
                        startPhrase.append("Pattern:");
                        response.getPatterns().forEach((pattern -> startPhrase.append("\"").append(pattern.name).append("\",")));
                        chatHelper.createPatteraChatMessage(startPhrase.toString());
                    }else{
                        chatHelper.createPatteraChatMessage("Sorry it seems I have no patterns stored at the moment, but maybe you could give me some ;).");
                    }
                }
                return new IntentDiscoveryState(chatHelper, false);
            }
        });
        //Fallback
        this.Rules.put(Pattern.compile(".*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                chatHelper.createPatteraChatMessage("Sorry i could not understand what your intent is could you please try again.");
                return new IntentDiscoverAskingForHelpState(chatHelper, false);
            }
        });
    }

    @Override
    public void setupOptions() {
        this.Options.add("1. Request for a Guided Search");
        this.Options.add("2. Request for the Nearest Pattern to a Given Pattern");
        this.Options.add("3. Request for a List of all available Pattern");
//        this.Options.add("4. Request for Information on a Specific Pattern");
    }

    @Override
    public void createInitMessage() {
        StringBuilder startPhrase = new StringBuilder(this.InitializationMessage);
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

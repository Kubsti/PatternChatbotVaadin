package unibz.it.PatternChatbot.state;

import unibz.it.PatternChatbot.model.*;
import unibz.it.PatternChatbot.utility.UiHelperUtility;

import java.util.ArrayList;

public class NearestPatternState  extends State {
    private ArrayList<unibz.it.PatternChatbot.model.Pattern> allAvailablePattern;
    public NearestPatternState(UiHelperUtility chatHelper, boolean showInitMessage) {
        super(chatHelper, "For which of the following pattern would you like to get the nearest?", showInitMessage);
    }
    @Override
    public void setupResponses() {
        //1. Request for a Guided Search
        this.Rules.put(java.util.regex.Pattern.compile("1.*|1\\."
                , java.util.regex.Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) throws StateException {
                return new GuidedSearchState(chatHelper, true);
            }
        });
        //2. List all available patterns
        this.Rules.put(java.util.regex.Pattern.compile("2.*|2\\."
                , java.util.regex.Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) throws StateException {
                StringBuilder startPhrase = new StringBuilder("To get the nearest pattern available for a pattern pleas input the name of an available pattern.\n");
                DesignPatterns response = httpHelper.getAllPattern();
                if (response != null) {
                    if (!response.getPatterns().isEmpty()) {
                        startPhrase.append("Pattern:");
                        response.getPatterns().forEach((pattern -> startPhrase.append("\n").append(pattern.name)));
                        setAllAvailablePattern(response.getPatterns());
                        chatHelper.createPatteraChatMessage(startPhrase.toString());
                    } else {
                        chatHelper.createPatteraChatMessage("Sorry it seems I have no patterns stored at the moment, but maybe you could give me some ;).");
                    }
                }
                return null;
            }
        });
        //Fallback assume search for nearest pattern
        this.Rules.put(java.util.regex.Pattern.compile(".*"
                , java.util.regex.Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                for(unibz.it.PatternChatbot.model.Pattern pattern : getAllAvailablePattern()){
                    if(pattern.name.equalsIgnoreCase(input)){
                        NearestPatternWeightedResponseDto foundPattern = httpHelper.getNearestPatternWeigthed(pattern,-1);
                        if(null == foundPattern || foundPattern.getNearestPattern().isEmpty()){
                            chatHelper.createPatteraChatMessage("Sorry there seems to have happened an error could you please try again?");
                            return null;
                        }
                        StringBuilder response = new StringBuilder(foundPattern.getNearestPattern().get(0).name + " is the nearest Pattern to " + input);
                        response.append("\nOptions:");
                        for (String option : stateOptions) {
                            response.append("\n").append(option);
                        }
                        response.append("\nIf you want to find another nearest pattern please input the name of an available pattern");
                        chatHelper.createPatteraChatMessage(response.toString());
                        return null;
                    }
                }
                chatHelper.createPatteraChatMessage("Sorry i could not find any Pattern which matches \"" + input + "\". If your Pattern is in the list of all pattern please check if the spelling is correct, and try again.");
                return null;
            }
        });
    }

    @Override
    public void setupOptions() {
        this.Options.add("1. Request for a Guided Search");
        this.Options.add("2. List all available patterns");
        //this.Options.add("3. Make another nearest pattern search");
    }

    @Override
    public void createInitMessage() {
        allAvailablePattern = new ArrayList<unibz.it.PatternChatbot.model.Pattern>();
        StringBuilder startPhrase = new StringBuilder();
        startPhrase.append("Options:");
        for (String option : this.Options) {
            startPhrase.append("\n").append(option);
        }
        startPhrase.append("\n").append(this.InitializationMessage).append("\n");
        DesignPatterns response = httpHelper.getAllPattern();
        if (response != null) {
            getAllAvailablePattern().addAll(response.getPatterns());
            if (!response.getPatterns().isEmpty()) {
                startPhrase.append("Pattern:");
                response.getPatterns().forEach((pattern -> startPhrase.append("\"").append(pattern.name).append("\",")));
                chatHelper.createPatteraChatMessage(startPhrase.toString());
            } else {
                chatHelper.createPatteraChatMessage("Sorry it seems I have no patterns stored at the moment, but maybe you could give me some ;).");
            }
        }
    }

    @Override
    public void setupExceptions() {

    }

    public ArrayList<unibz.it.PatternChatbot.model.Pattern> getAllAvailablePattern() {
        return allAvailablePattern;
    }

    public void setAllAvailablePattern(ArrayList<unibz.it.PatternChatbot.model.Pattern> allAvailablePattern) {
        this.allAvailablePattern = allAvailablePattern;
    }
}

package unibz.it.PatternChatbot.state;

import com.vaadin.flow.server.VaadinSession;
import unibz.it.PatternChatbot.model.*;
import unibz.it.PatternChatbot.utility.UiHelperUtility;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class MultipeFuzzyScoreMatchesState extends State {
    private TreeMap<Double, String> matches;
    private String currentAmswer;
    public MultipeFuzzyScoreMatchesState(UiHelperUtility chatHelper, boolean showInitMesssge) {
        super(chatHelper, "", showInitMesssge);
    }
    @Override
    public void setupResponses() {
        //Yes to fuzzy score match
        this.Rules.put(Pattern.compile("Yes.*|yes.*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) throws StateException {
                SearchResponseDto searchResponse = handleSearch(currentAmswer);
                //If we have not found any pattern assume that the user might want to retry last response
                if (searchResponse.getDesignPatterns().getPatterns().size() == 0) {
                    chatHelper.createPatteraChatMessage("Sorry i have not found any pattern ");
                }
                VaadinSession.getCurrent().setAttribute("excludedTags", searchResponse.getExcludedTags());
                VaadinSession.getCurrent().setAttribute("nextSearchTag", searchResponse.getNextSearchTag());
                VaadinSession.getCurrent().setAttribute("nextQuestion", searchResponse.getPatternQuestion());
                VaadinSession.getCurrent().setAttribute("designPattern", searchResponse.getDesignPatterns());
                VaadinSession.getCurrent().setAttribute("possibleAnswers", searchResponse.getCurrPossibleAnswersToQuestion());
                if (searchResponse.getDesignPatterns().getPatterns().size() == 1) {
                    chatHelper.createChatMessage("Found the following pattern: " + searchResponse.getDesignPatterns().getPatterns().get(0).name + "\n Website of pattern is: " + searchResponse.getDesignPatterns().getPatterns().get(0).url);
                    chatHelper.updatePdfViewer(searchResponse.getDesignPatterns().getPatterns().get(0).url);
                    return new FoundPatternState(chatHelper, true);
                } else {
                    chatHelper.createPatteraSearchAnswer("Found " + searchResponse.getDesignPatterns().getPatterns().size() + " Pattern\n", stateOptions,
                            searchResponse.getCurrPossibleAnswersToQuestion());
                    return new GuidedSearchState(chatHelper, false);
                }
            }
        });
        //No to fuzzy score match
        this.Rules.put(Pattern.compile("No.*|no.*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) throws StateException {
                TreeMap<Double, String> matches = (TreeMap<Double, String>) VaadinSession.getCurrent().getAttribute("fuzzyScoreMatches");
                if(matches.isEmpty()){
                    currentAmswer="";
                    chatHelper.createPatteraChatMessage("Sorry it seems there are no more matches left.");
                    return new GuidedSearchErrorState(chatHelper, true);
                }else{
                    StringBuilder startPhrase = new StringBuilder();
                    startPhrase.append("Did you mean ").append(matches.firstEntry().getValue()).append("\n");
                    currentAmswer=matches.firstEntry().getValue();
                    matches.remove(matches.firstEntry().getKey());
                    VaadinSession.getCurrent().setAttribute("fuzzyScoreMatches", matches);
                    startPhrase.append("\n").append("Possible answers are:\n").append("Yes,\nNo");
                    chatHelper.createPatteraChatMessage(startPhrase.toString());
                    return null;
                }
            }
        });
        this.Rules.put(Pattern.compile(".*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                chatHelper.createPatteraChatMessage("Sorry i could not understand what your intent is could you please try again.");
                return new MultipeFuzzyScoreMatchesState(chatHelper, false);
            }
        });
    }

    @Override
    public void setupOptions() {

    }

    @Override
    public void createInitMessage() {
        StringBuilder startPhrase = new StringBuilder(this.InitializationMessage);
//        startPhrase.append("\nOptions:");
//        for (String option : this.Options) {
//            startPhrase.append("\n").append(option);
//        }
        TreeMap<Double, String> matches = (TreeMap<Double, String>) VaadinSession.getCurrent().getAttribute("fuzzyScoreMatches");
        startPhrase.append("Did you mean ").append(matches.firstEntry().getValue()).append("\n");
        matches.remove(matches.firstEntry().getKey());
        VaadinSession.getCurrent().setAttribute("fuzzyScoreMatches", matches);
        startPhrase.append("\n").append("Possible answers are:\n").append("Yes,\nNo");
        chatHelper.createPatteraChatMessage(startPhrase.toString());
    }

    @Override
    public void setupExceptions() {

    }

    public SearchResponseDto handleSearch(String searchInput) throws StateException {
        return this.httpHelper.searchForPattern(searchInput);
    }
}

package unibz.it.PatternChatbot.state;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.vaadin.flow.server.VaadinSession;
import unibz.it.PatternChatbot.model.*;
import unibz.it.PatternChatbot.utility.UiHelperUtility;

import java.lang.reflect.Array;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Pattern;

public class GuidedSearchState extends State {

    public GuidedSearchState(UiHelperUtility chatHelper, boolean showInitMesssge) {
        super(chatHelper, "Hi, I will help you find your desired pattern.", showInitMesssge);
    }
//    public SearchResponseDto handleSearch(String searchInput) throws StateException {
//        //Is one as the first try is accounted for
//        int retries = 1;
//        ArrayList<Pair<String,Double>> extractedKeywords = extractKeywords(searchInput);
//        if(extractedKeywords.isEmpty()){
//            //TODO handle error
//            throw new StateException("NoKeywordsFound", searchInput);
//            //chatHelper.createPatteraChatMessage("Sorry i have difficulties extracting keywords from your input, please contact my creator.");
//            //VaadinSession.getCurrent().setAttribute("state","errorstate");
//        }
//        SearchResponseDto searchResult = this.httpHelper.searchForPattern(extractedKeywords, 0);
//        //print message for user
//        //TODO rework does not work correctly, prob not needed if user can only input given answers
//        while(searchResult.getDesignPatterns().getPatterns().isEmpty() && retries < 3){
//            if(extractedKeywords.size() > retries){
//                retries++;
//                searchResult = this.httpHelper.searchForPattern(extractedKeywords, retries);
//            }else{
//                retries = 3;
//                if(searchResult.getDesignPatterns().getPatterns().isEmpty()){
//                    throw  new StateException("NoPatternFound", searchInput);
//                }
//            }
//
//        }
//        return searchResult;
//    }

    public SearchResponseDto handleSearch(String searchInput) throws StateException {
        ArrayList<String> possibleAnswers = (ArrayList<String>) VaadinSession.getCurrent().getAttribute("possibleAnswers");
        boolean isPossibleAnswer = false;
        if (possibleAnswers.isEmpty()) {
            throw new StateException("PossibleSearchAnswersEmpty", "The in the session stored search Answers List is empty.");
        }
        //TODO on later development change do parsing option with number
        if (searchInput.matches("^None$|^none$")) {
            //TODO better Exception handling
            return this.httpHelper.excludePattern((String) VaadinSession.getCurrent().getAttribute("nextSearchTag"));
        }

        for (String possibleAnswer : possibleAnswers) {
            if (possibleAnswer.equalsIgnoreCase(searchInput)) {
                isPossibleAnswer = true;
                break;
            }
        }

        if (!isPossibleAnswer) {
            throw new StateException("InvalidSearchInput", "Given input string did not match one of the available search Options");
        }
        SearchResponseDto searchResult = this.httpHelper.searchForPattern(searchInput);

        return searchResult;
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

        //2. Print all found pattern
        this.Rules.put(Pattern.compile("2.*|2\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                DesignPatterns designPatterns = (DesignPatterns) VaadinSession.getCurrent().getAttribute("designPattern");
                if (designPatterns.getPatterns().isEmpty()) {
                    chatHelper.createPatteraChatMessage("I found no pattern. Maybe the last search for a question did not return anything.");
                } else {
                    StringBuilder startPhrase = new StringBuilder();
                    startPhrase.append("Options:\n");
                    stateOptions.forEach(option -> startPhrase.append(option).append("\n"));
                    startPhrase.append("Pattern:");
                    designPatterns.getPatterns().forEach((pattern -> startPhrase.append("\"").append(pattern.name).append("\",")));
                    chatHelper.createPatteraChatMessage(startPhrase.toString());
                }
                return new GuidedSearchState(chatHelper, false);
            }
        });

        //3. Get another question
        this.Rules.put(Pattern.compile("3.*|3\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) throws StateException {
                if (getNewQuestion(input)) {
                    return new GuidedSearchState(chatHelper, false);
                }
                return new GuidedSearchErrorState(chatHelper, false);
            }
        });

        //4. Fallback (Assume Search Input for Keywords)
        this.Rules.put(Pattern.compile(".*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) throws StateException {
                SearchResponseDto searchResponse = handleSearch(input);
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
                }
                return null;
            }
        });
    }


    @Override
    public void setupOptions() {
//        this.Options.add("1. Stop search");
        this.Options.add("1. Restart search");
        this.Options.add("2. Print all found pattern");
        this.Options.add("3. Get another question");
        this.Options.add("4. Search (continue to answer the questions given by Pattera)");
    }

    @Override
    public void createInitMessage() {
        StringBuilder startPhrase = new StringBuilder(this.InitializationMessage);
        startPhrase.append("\nOptions:");
        for (String option : this.Options) {
            startPhrase.append("\n").append(option);
        }
        PatternQuestion question = (PatternQuestion) VaadinSession.getCurrent().getAttribute("nextQuestion");
        startPhrase.append("\n").append(question.getQuestion());
        ArrayList<String> possibleAnswers = (ArrayList<String>) VaadinSession.getCurrent().getAttribute("possibleAnswers");
        startPhrase.append("\n").append("Possible answers are:\n");
        for (String possibleAnswer : possibleAnswers) {
            startPhrase.append("\"").append(possibleAnswer).append("\",");
        }
        chatHelper.createPatteraChatMessage(startPhrase.toString());
    }

    @Override
    public void setupExceptions() {
        this.Exceptions.put("NoKeywordsFound",
                (String input) -> {
                    chatHelper.createPatteraChatMessage("I could not extract any keywords from your response, could you give me your answer again?");
                    return null;
                }
        );
        this.Exceptions.put("NoPatternFound",
                (String input) -> {
                    ;
                    return new GuidedSearchErrorState(chatHelper, true);
                }
        );
        this.Exceptions.put("NoMoreQuestionsAvailable",
                (String input) -> {
                    ;
                    StringBuilder startPhrase = new StringBuilder("Sorry there are no more questions available. What would you like to do?");
                    startPhrase.append("\nOptions:");
                    for (String option : this.Options) {
                        if (!option.equalsIgnoreCase("4. Get another question")) {
                            startPhrase.append("\n").append(option);
                        }
                    }
                    chatHelper.createPatteraChatMessage(startPhrase.toString());
                    return new GuidedSearchErrorState(chatHelper, true);
                }
        );
        this.Exceptions.put("InvalidSearchInput",
                (String input) -> {
                    chatHelper.createPatteraChatMessage("Sorry this is now valid search input, please select one from the possible search options.");
                    return new GuidedSearchState(chatHelper, false);
                }
        );
        this.Exceptions.put("PossibleSearchAnswersEmpty",
                (String input) -> {
                    chatHelper.createPatteraChatMessage("Sorry it seems like i have no possible answers please contact my creator. I will restart the search.");
                    httpHelper.intializeChatbot();
                    return new GuidedSearchState(chatHelper, true);
                }
        );
        this.Exceptions.put("QuestionResultDeserializationError",
                (String input) -> {
                    chatHelper.createPatteraChatMessage("Sorry it seems seems like an error occurred when trying to deserialize the result of getAnotherQuestion rest request.");
                    return new GuidedSearchErrorState(chatHelper, true);
                }
        );
        this.Exceptions.put("NoQuestionResult",
                (String input) -> {
                    chatHelper.createPatteraChatMessage("Sorry it seems seems like an error occurred when in the getAnotherQuestion rest request");
                    return new GuidedSearchErrorState(chatHelper, true);
                }
        );
        this.Exceptions.put("GetNewQuestionException",
                (String input) -> {
                    chatHelper.createPatteraChatMessage("Sorry it seems seems like an error occurred when in the getAnotherQuestion rest request");
                    return new GuidedSearchErrorState(chatHelper, true);
                }
        );
    }

    public boolean getNewQuestion(String chatInput) throws StateException {
        NewQuestionResponseDto questionResult = null;
        HttpResponse<String> response = httpHelper.getAnotherQuestion();

        if (response != null) {
            Gson gson = new GsonBuilder().create();
            try {
                questionResult = gson.fromJson(response.body(), NewQuestionResponseDto.class);
            } catch (Exception e) {
                e.printStackTrace();
                throw new StateException("QuestionResultDeserializationError", "An error occurred when trying to deserialize the result of getAnotherQuestion rest request");
            }
            if (questionResult.getNextSearchTag().equalsIgnoreCase("No more Tags available")) {
                throw new StateException("NoMoreQuestionsAvailable", chatInput);
            }
            VaadinSession.getCurrent().setAttribute("excludedTags", questionResult.getExcludedTags());
            VaadinSession.getCurrent().setAttribute("nextSearchTag", questionResult.getNextSearchTag());
            VaadinSession.getCurrent().setAttribute("nextQuestion", questionResult.getPatternQuestion());
            VaadinSession.getCurrent().setAttribute("possibleAnswers", questionResult.getPossibleAnswers());
            StringBuilder startPhrase = new StringBuilder(questionResult.getPatternQuestion().getQuestion());
            startPhrase.append("\n").append("Possible answers are:\n");
            for (String possibleAnswer : questionResult.getPossibleAnswers()) {
                startPhrase.append("\"").append(possibleAnswer).append("\",");
            }
            chatHelper.createChatMessage(startPhrase.toString());
        } else {
            throw new StateException("NoQuestionResult", "An error occurred when in the getAnotherQuestion rest request");
        }
        return true;
    }
}

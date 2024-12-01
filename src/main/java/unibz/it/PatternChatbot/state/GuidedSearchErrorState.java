package unibz.it.PatternChatbot.state;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.vaadin.flow.server.VaadinSession;
import unibz.it.PatternChatbot.model.*;
import unibz.it.PatternChatbot.utility.UiHelperUtility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class GuidedSearchErrorState extends State {
    public GuidedSearchErrorState(UiHelperUtility chatHelper, boolean showInitMesssge) {
        super(chatHelper,"Sorry I could not find any pattern with your response. What would you like to do?", showInitMesssge);
    }

    @Override
    public void setupResponses() {
        //1. Restart search
        //TODO must be tested
        this.Rules.put(Pattern.compile("1.*|1\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input,ArrayList<String> stateOptions) {
                httpHelper.intializeChatbot();
                return new GuidedSearchState(chatHelper, true);
            }
        });

        //2. Print currently filtered pattern
        this.Rules.put(Pattern.compile("2.*|2\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                DesignPatterns designPatterns = (DesignPatterns)VaadinSession.getCurrent().getAttribute("designPattern");
                if(designPatterns.getPatterns().isEmpty()){
                    chatHelper.createPatteraChatMessage("I found no pattern. Maybe the last search for a question did not return anything.");
                }else{
                    StringBuilder startPhrase = new StringBuilder();
                    startPhrase.append("Pattern:");
                    designPatterns.getPatterns().forEach((pattern -> startPhrase.append("\"").append(pattern.name).append("\",")));
                    chatHelper.createPatteraChatMessage(startPhrase.toString());
                }
                return new GuidedSearchErrorState(chatHelper, false);
            }
        });

        //3. Get another question
        this.Rules.put(Pattern.compile("3.*|3\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                if(getNewQuestion(input)){
                    return new GuidedSearchState(chatHelper, false);
                }
                return new GuidedSearchErrorState(chatHelper, false);
            }
        });

        //4. Retry to answer last question
        this.Rules.put(Pattern.compile("4.*|4\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                PatternQuestion question = (PatternQuestion) VaadinSession.getCurrent().getAttribute("nextQuestion");
                chatHelper.createPatteraChatMessage(question.getQuestion());
                return new GuidedSearchState(chatHelper, false);
            }
        });

        this.Rules.put(Pattern.compile(".*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                chatHelper.createPatteraChatMessage("Sorry i could not understand what your intent is could you please try again.");
                return new GuidedSearchErrorState(chatHelper, false);
            }
        });
    }

    @Override
    public void setupOptions() {
        //this.Options.add("1. Stop search");
        this.Options.add("1. Restart search");
        this.Options.add("2. Print currently filtered pattern");
        this.Options.add("3. Get another question");
        this.Options.add("4. Retry to answer last question");
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
        this.Exceptions.put("NoKeywordsFound",
                (String input) -> {
                    chatHelper.createPatteraChatMessage("I could not extract any keywords from your response, could you give me your answer again?");
                    return null;
                }
        );
        this.Exceptions.put("NoMoreQuestionsAvailable",
                (String input) -> {;
                    StringBuilder startPhrase = new StringBuilder("Sorry there are no more questions available. What would you like to do?");
                    startPhrase.append("\nOptions:");
                    for(String option : this.Options) {
                        if(!option.equalsIgnoreCase("4. Get another question")){
                            startPhrase.append("\n").append(option);
                        }
                    }
                    chatHelper.createPatteraChatMessage(startPhrase.toString());
                    return new GuidedSearchErrorState(chatHelper, true);
                }
        );
    }

    public boolean getNewQuestion(String chatInput){
        NewQuestionResponseDto questionResult = null;
        try{
            HttpResponse<String> response = httpHelper.getAnotherQuestion();

            if (response != null){
                Gson gson = new GsonBuilder().create();
                questionResult= gson.fromJson(response.body(), NewQuestionResponseDto.class);
                if(questionResult.getNextSearchTag().equalsIgnoreCase("No more Tags available")){
                    throw new StateException("NoMoreQuestionsAvailable",chatInput);
                }
                VaadinSession.getCurrent().setAttribute("excludedTags",questionResult.getExcludedTags());
                VaadinSession.getCurrent().setAttribute("nextSearchTag",questionResult.getNextSearchTag());
                VaadinSession.getCurrent().setAttribute("nextQuestion", questionResult.getPatternQuestion());
                StringBuilder startPhrase = new StringBuilder(questionResult.getPatternQuestion().getQuestion());
                startPhrase.append("\n").append("Possible answers are:\n");
                for(String possibleAnswer: questionResult.getPossibleAnswers()){
                    startPhrase.append("\"").append(possibleAnswer).append("\",");
                }
                chatHelper.createChatMessage(startPhrase.toString());
            }else{
                //TODO recheck if better exception handing is needed
                chatHelper.createPatteraChatMessage("Sorry a error occurred please try again");
                return false;
            }
        }catch(Exception e){
            //TODO recheck if better exception handing is needed
            e.printStackTrace();
            chatHelper.createPatteraChatMessage("Sorry a error occurred please try again");
            return false;
        }
        return true;
    }
}

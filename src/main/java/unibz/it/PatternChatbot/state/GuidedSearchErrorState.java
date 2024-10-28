package unibz.it.PatternChatbot.state;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.server.VaadinSession;
import unibz.it.PatternChatbot.model.*;
import unibz.it.PatternChatbot.utility.ChatHelperUtility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class GuidedSearchErrorState extends State {
    public GuidedSearchErrorState(ChatHelperUtility chatHelper) {
        super(chatHelper);
        this.InitializationMessage ="Sorry I could not find any pattern with your response. What would you like to do?";
        this.createInitMessage();
    }

    public void handleError(){

    }

    @Override
    public Optional<State> handleInput(String chatInput, MessageList chat, IFrame webpageIFrame) {
        for (Map.Entry<Pattern, Response> set :
                this.Rules.entrySet()) {
            //Try to match a Rule
            if(set.getKey().matcher(chatInput).find()) {
                return Optional.ofNullable(set.getValue().responseAction(chatInput, chat, webpageIFrame));
            }
        }
        return Optional.empty();
    }

    @Override
    public void setupResponses() {
        //1. Stop search
        this.Rules.put(Pattern.compile("(?i)\\b(1|stop|cancel|end|terminate)\\b.*\\b(search)\\b|(?i)\\b(stop|cancel|end|terminate)\\b.*\\b(search)\\b|1.*|1\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chatHelper.createPatteraChatMessage("Search stoppend");
                //TODO go into correct state
                return new IntentDiscoveryState(chatHelper);
            }
        });

        //2. Restart search
        this.Rules.put(Pattern.compile("(?i)\\b(2|restart|redo|start over|begin again)\\b.*\\b(search)\\b|(?i)\\b(restart|redo|start over|begin again)\\b.*\\b(search)\\b|2.*|2\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chatHelper.createPatteraChatMessage("To be implemented");
                //TODO reset and start new search
                return new GuidedSearchState(chatHelper);
            }
        });

        //3. Print currently filtered pattern
        this.Rules.put(Pattern.compile("(?i)\\b(3|print|show|display|list)\\b.*\\b(all|found)\\b.*\\b(patterns|pattern)\\b|(?i)\\b(print|show|display|list)\\b.*\\b(all|found)\\b.*\\b(patterns)\\b|3.*|3\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chatHelper.createPatteraChatMessage("To be implemented");
                //TODO go into correct state
                return new GuidedSearchState(chatHelper);
            }
        });

        //4. Get another question
        this.Rules.put(Pattern.compile("(?i)\\b(get|fetch|retrieve)\\b.*\\b(another|next|new)\\b.*\\b(question)\\b|4.*|4\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                chatHelper.createPatteraChatMessage("To be implemented");
                if(getNewQuestion()){
                    return new GuidedSearchState(chatHelper);
                }
                //TODO go into correct state
                return new GuidedSearchErrorState(chatHelper);
            }
        });
        this.Rules.put(Pattern.compile(".*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                //Todo implement fallback
                //TODO go into correct state
                chatHelper.createPatteraChatMessage("Sorry i could not understand what your intent is could you please try again.");
                return new GuidedSearchErrorState(chatHelper);
            }
        });
    }

    @Override
    public void setupOptions() {
        this.Options.add("1. Stop search");
        this.Options.add("2. Restart search");
        this.Options.add("3. Print currently filtered pattern");
        this.Options.add("4. Get another question");
        this.Options.add("5. Retry to answer last question");
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

    public boolean getNewQuestion(){
        NewQuestionResponseDto questionResult = null;
        String nextSearchTag = (String ) VaadinSession.getCurrent().getAttribute("nextSearchTag");
        ArrayList<String> excludedTags;
        //TODO fix unchecked function error
        excludedTags = (ArrayList<String>) VaadinSession.getCurrent().getAttribute("excludedTags");
        excludedTags.add(nextSearchTag);
        try{
            Gson gson = new GsonBuilder().create();
            URL url = URI.create("http://localhost:8080/getNewSearchQuestion").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            NewQuestionDto newQuestionDto = new NewQuestionDto(excludedTags);
            String reqBody = gson.toJson(newQuestionDto);
            try(DataOutputStream os = new DataOutputStream(conn.getOutputStream())){
                byte[] input = reqBody.getBytes("utf-8");
                os.write(input, 0, input.length);
                //os.writeUTF(reqBody);
                //os.writeBytes(reqBody);
                os.flush();
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                StringBuilder response = new StringBuilder();
                try (BufferedReader searchResponseReader = new BufferedReader( new InputStreamReader( conn.getInputStream()))) {
                    String newQuestionResponseline;
                    while ((newQuestionResponseline = searchResponseReader.readLine()) != null) {
                        response.append(newQuestionResponseline); // Adds every line to response till the end of file.
                    }
                }
                //TODO check why parsing does not work correctly
                questionResult= gson.fromJson(response.toString(), NewQuestionResponseDto.class);
                if(questionResult.getNextSearchTag().equalsIgnoreCase("No more Tags available")){
                    StringBuilder startPhrase = new StringBuilder("Sorry there are no more questions available. What would you like to do?");
                    startPhrase.append("\nOptions:");
                    for(String option : this.Options) {
                        if(!option.equalsIgnoreCase("4. Get another question")){
                            startPhrase.append("\n").append(option);
                        }
                    }
                    chatHelper.createPatteraChatMessage(startPhrase.toString());
                }
            }else{
                chatHelper.createPatteraChatMessage("Sorry a error occurred please try again");
                return false;
            }
        }catch(Exception e){
            chatHelper.createPatteraChatMessage("Sorry a error occurred please try again");
            return false;
        }
        return true;
    }
}

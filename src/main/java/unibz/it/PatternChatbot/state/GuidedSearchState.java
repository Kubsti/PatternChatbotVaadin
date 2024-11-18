package unibz.it.PatternChatbot.state;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.vaadin.flow.server.VaadinSession;
import oshi.util.tuples.Pair;
import unibz.it.PatternChatbot.model.*;
import unibz.it.PatternChatbot.ui.ErrorDialog;
import unibz.it.PatternChatbot.utility.UiHelperUtility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Pattern;

public class GuidedSearchState extends State {

    public GuidedSearchState(UiHelperUtility chatHelper, boolean showInitMesssge){
        super(chatHelper,"Search State entered. To stop search write 'Stop Search'", showInitMesssge);
    }
    public SearchResponseDto handleSearch(String searchInput) throws StateException {
        //Is one as the first try is accounted for
        int retries = 1;
        ArrayList<Pair<String,Double>> extractedKeywords = extractKeywords(searchInput);
        if(extractedKeywords.isEmpty()){
            //TODO handle error
            throw new StateException("NoKeywordsFound", searchInput);
            //chatHelper.createPatteraChatMessage("Sorry i have difficulties extracting keywords from your input, please contact my creator.");
            //VaadinSession.getCurrent().setAttribute("state","errorstate");
        }
        SearchResponseDto searchResult = searchForPattern(extractedKeywords, 0);
        //print message for user
        //TODO rework does not work correctly
        while(searchResult.getDesignPatterns().getPatterns().isEmpty() && retries < 3){
            if(extractedKeywords.size() > retries){
                retries++;
                searchResult = searchForPattern(extractedKeywords, retries);
            }else{
                retries = 3;
                if(searchResult.getDesignPatterns().getPatterns().isEmpty()){
                    throw  new StateException("NoPatternFound", searchInput);
                }
            }

        }
        return searchResult;
    }

    @Override
    public void setupResponses() {

        //1. Stop search
        this.Rules.put(Pattern.compile("(?i)\\b(1|stop|cancel|end|terminate)\\b.*\\b(search)\\b|(?i)\\b(stop|cancel|end|terminate)\\b.*\\b(search)\\b|1.*|1\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input) {
                httpHelper.intializeChatbot();
                return new IntentDiscoveryState(chatHelper,true);
            }
        });

        //2. Restart search
        this.Rules.put(Pattern.compile("(?i)\\b(2|restart|redo|start over|begin again)\\b.*\\b(search)\\b|(?i)\\b(restart|redo|start over|begin again)\\b.*\\b(search)\\b|2.*|2\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input) {
                httpHelper.intializeChatbot();
                return new GuidedSearchState(chatHelper, true);
            }
        });

        //3. Print all found pattern
        this.Rules.put(Pattern.compile("(?i)\\b(3|print|show|display|list)\\b.*\\b(all|found)\\b.*\\b(patterns|pattern)\\b|(?i)\\b(print|show|display|list)\\b.*\\b(all|found)\\b.*\\b(patterns)\\b|3.*|3\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input) {
                DesignPatterns designPatterns = (DesignPatterns)VaadinSession.getCurrent().getAttribute("designPattern");
                if(designPatterns.getPatterns().isEmpty()){
                    chatHelper.createPatteraChatMessage("I found no pattern. Maybe the last search for a question did not return anything.");
                }else{
                    StringBuilder startPhrase = new StringBuilder();
                    startPhrase.append("Pattern:");
                    designPatterns.getPatterns().forEach((pattern -> startPhrase.append("\n").append(pattern.name)));
                    chatHelper.createPatteraChatMessage(startPhrase.toString());
                }
                return new GuidedSearchState(chatHelper, false);
            }
        });

        //4. Get another question
        this.Rules.put(Pattern.compile("(?i)\\b(get|fetch|retrieve)\\b.*\\b(another|next|new)\\b.*\\b(question)\\b|4.*|4\\..*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input) {
                if(getNewQuestion(input)){
                    return new GuidedSearchState(chatHelper, false);
                }
                return new GuidedSearchErrorState(chatHelper, false);
            }
        });

        //5. Fallback (Assume Search Input for Keywords)
        this.Rules.put(Pattern.compile(".*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input) throws StateException {
                SearchResponseDto searchResponse = handleSearch(input);
                VaadinSession.getCurrent().setAttribute("excludedTags",searchResponse.getExcludedTags());
                VaadinSession.getCurrent().setAttribute("nextSearchTag",searchResponse.getNextSearchTag());
                VaadinSession.getCurrent().setAttribute("nextQuestion", searchResponse.getPatternQuestion());
                VaadinSession.getCurrent().setAttribute("designPattern",searchResponse.getDesignPatterns());
                if(searchResponse.getDesignPatterns().getPatterns().size() == 1){
                    //TODO change to pattern found state
                    chatHelper.createChatMessage("Found the following pattern: " + searchResponse.getDesignPatterns().getPatterns().get(0).name);
                    chatHelper.updateIFrame(searchResponse.getDesignPatterns().getPatterns().get(0).url);
                    VaadinSession.getCurrent().setAttribute("excludedTags",searchResponse.getExcludedTags());
                    VaadinSession.getCurrent().setAttribute("nextSearchTag",searchResponse.getNextSearchTag());
                    VaadinSession.getCurrent().setAttribute("nextQuestion", searchResponse.getPatternQuestion());
                    VaadinSession.getCurrent().setAttribute("designPattern",searchResponse.getDesignPatterns());
                }else{
                    chatHelper.createChatMessage(searchResponse.getPatternQuestion().getQuestion());
                }
                return null;
            }
        });
    }


    @Override
    public void setupOptions() {
        this.Options.add("1. Stop search");
        this.Options.add("2. Restart search");
        this.Options.add("3. Print all found pattern");
        this.Options.add("4. Get another question");
        this.Options.add("5. Search (continue to answer the questions given by Pattera)");
    }

    @Override
    public void createInitMessage() {
        StringBuilder startPhrase = new StringBuilder(this.InitializationMessage);
        startPhrase.append("\nOptions:");
        for(String option : this.Options) {
            startPhrase.append("\n").append(option);
        }
        PatternQuestion question = (PatternQuestion) VaadinSession.getCurrent().getAttribute("nextQuestion");
        startPhrase.append("\n").append(question.getQuestion());
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
                (String input) -> {;
                    return new GuidedSearchErrorState(chatHelper, true);
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

    public  ArrayList<Pair<String,Double>> extractKeywords(String searchInput){
        File file = new File("Python/keyword_extractor.py");
        String absolutePathOfKeywordExtractor = file.getAbsolutePath();
        ProcessBuilder processBuilder = new ProcessBuilder("python", absolutePathOfKeywordExtractor,  searchInput);
        ArrayList<Pair<String,Double>> tupleList = new ArrayList<>();
        try{
            Process pythonKeywordExtractor = processBuilder.start();
            BufferedReader reader = new BufferedReader((new InputStreamReader(pythonKeywordExtractor.getInputStream())));
            StringBuilder output = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                output.append(line);
            }
            reader.close();
            int exitCode = pythonKeywordExtractor.waitFor();
            if(exitCode == 0){
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<ArrayList<Object>>>() {}.getType();
                List<ArrayList<Object>> javaList = gson.fromJson(output.toString(), listType);
                //if we did not find any token/
                if(javaList.isEmpty()){
//                    MessageListItem newBotMessage = new MessageListItem(
//                            "I could not understand you answer could you try again?",
//                            Instant.now(), "PatternSearchBot");
//                    listOfMessages.add(newBotMessage);
//                    chat.setItems(listOfMessages);
                    //TODO handle no token could be generated
                    //
                }else{
                    for (ArrayList<Object> item : javaList) {
                        String first = (String) item.get(0);
                        Double second = Double.valueOf(item.get(1).toString()); // JSON numbers are parsed as Double
                        tupleList.add(new Pair<>(first, second));
                    }
                }
            }else{
                //TODO handle error of python script
                //Set state to error state, let error state handle recovery
            }
        }catch(Exception e){
         //TODO handle keyword search exception
        }
        return tupleList;
    }
    //TODO rework must be done
    public SearchResponseDto searchForPattern(ArrayList<Pair<String,Double>> keywords,int keywordToSearchWith){
        //Call controller to search for keywords
        //handle no pattern found
        //handle pattern found
        //handle error
        //search with keywords for a pattern
        SearchResponseDto searchResult = null;
        String nextSearchTag = (String ) VaadinSession.getCurrent().getAttribute("nextSearchTag");
        DesignPatterns designPatterns = (DesignPatterns)VaadinSession.getCurrent().getAttribute("designPattern");
        ArrayList<String> excludedTags;
        //TODO fix unchecked function error
        excludedTags = (ArrayList<String>) VaadinSession.getCurrent().getAttribute("excludedTags");
        try{
            Gson gson = new GsonBuilder().create();
            //Gson gson = new Gson();
            URL url = URI.create("http://localhost:8080/searchPattern").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            SearchDto searchRequest = new SearchDto(nextSearchTag,keywords.get(keywordToSearchWith).getA(),designPatterns,excludedTags);
            String reqBody = gson.toJson(searchRequest);
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
                    String searchResponseline;
                    while ((searchResponseline = searchResponseReader.readLine()) != null) {
                        response.append(searchResponseline); // Adds every line to response till the end of file.
                    }
                }
                //TODO check why parsing does not work correctly
                searchResult= gson.fromJson(response.toString(), SearchResponseDto.class);

            }else{
                //TODO Hanlde error with state etc.
            }
            //conn.connect();

        }catch(Exception e){
            //Todo implement error hanlding
        }
        return searchResult;
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
                chatHelper.createChatMessage(questionResult.getPatternQuestion().getQuestion());
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

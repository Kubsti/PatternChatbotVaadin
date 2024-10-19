package unibz.it.PatternChatbot.state;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.server.VaadinSession;
import oshi.util.tuples.Pair;
import unibz.it.PatternChatbot.model.DesignPatterns;
import unibz.it.PatternChatbot.model.Response;
import unibz.it.PatternChatbot.model.SearchDto;
import unibz.it.PatternChatbot.model.SearchResponseDto;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

public class GuidedSearchState extends State {

    public GuidedSearchState(){
        this.Rules = new LinkedHashMap<Pattern, Response>();
        this.Options = new ArrayList<String>();
        this.InitializationMessage ="Search State entered";
        this.setupResponses();
        this.setupOptions();

    }
    public SearchResponseDto handleSearch(String searchInput) {
        int retries = 0;
        ArrayList<Pair<String,Double>> extractedKeywords = extractKeywords(searchInput);
        if(extractedKeywords.isEmpty()){
            //TODO handle error
            VaadinSession.getCurrent().setAttribute("state","errorstate");
        }
        SearchResponseDto searchResult = searchForPattern(extractedKeywords, 0);
        //print message for user
        while(searchResult.getDesignPatterns().getPatterns().isEmpty() && retries < 3){
            if(extractedKeywords.size() > retries){
                retries++;
                searchResult = searchForPattern(extractedKeywords, retries);
            }else{
                //set retries to 3 enter error state and try to recover
                retries = 3;
                //TODO implement a better error handing. Maybe with a special state?
                //VaadinSession.getCurrent().setAttribute("state","errorstate");
                //VaadinSession.getCurrent().setAttribute("errorcause","searchFailedOnRetries");
            }

        }
        return searchResult;
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
        //TODO go into correct state
        return Optional.of(new GuidedSearchState());
    }

    @Override
    public void setupResponses() {

        //1. Ask for Nearest Pattern to a Given Pattern
        this.Rules.put(Pattern.compile("(?i)(nearest|closest|similar|related).*pattern.*(to|like).*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                //currState = new SearchState();
                chat.getItems().add(new MessageListItem(
                        "To be implemented",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new GuidedSearchState();
            }
        });
        //2. Display Currently Found Patterns
        this.Rules.put(Pattern.compile("(?i)(show|display|list).*current.*pattern(s)?"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                //currState = new SearchState();
                chat.getItems().add(new MessageListItem(
                        "To be implemented",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return null;
            }
        });
        //3. Requesting a Specific Pattern Type (e.g., design pattern, behavior pattern)
        this.Rules.put(Pattern.compile("(?i)(design|behavior(al)?|structural|creational).*pattern(s)?"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                //currState = new SearchState();
                chat.getItems().add(new MessageListItem(
                        "To be implemented",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new GuidedSearchState();
            }
        });
        //4. Go to IntentDiscoveryState
        this.Rules.put(Pattern.compile("(?i)(what|how).*help|(discover|guide|guess).*intent|(how|can).*start"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                //TODO Reset search, or mabye call intialization method again? Then go back to IntentDiscoveryState,
                chat.getItems().add(new MessageListItem(
                        "To be implemented",
                        Instant.now(), "Pattera"));
                //TODO go into correct state
                return new IntentDiscoveryState();
            }
        });
        //5. Fallback (Assume Search Input for Keywords)
        this.Rules.put(Pattern.compile("(?i).*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, MessageList chat, IFrame webpageIFrame) {
                SearchResponseDto searchResponse = handleSearch(input);
                VaadinSession.getCurrent().setAttribute("excludedTags",searchResponse.getExcludedTags());
                VaadinSession.getCurrent().setAttribute("nextSearchTag",searchResponse.getNextSearchTag());
                VaadinSession.getCurrent().setAttribute("nextQuestion", searchResponse.getPatternQuestion());
                VaadinSession.getCurrent().setAttribute("designPattern",searchResponse.getDesignPatterns());
                List<MessageListItem> messages = new ArrayList<MessageListItem>();
                messages.addAll(chat.getItems());
                messages.add(new MessageListItem(
                        searchResponse.getPatternQuestion().getQuestion(),
                        Instant.now(), "Pattera"));
                chat.setItems(messages);
                return new GuidedSearchState();
            }
        });
    }


    @Override
    public void setupOptions() {
        //TODO to be implemented
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
}

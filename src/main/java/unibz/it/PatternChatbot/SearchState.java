package unibz.it.PatternChatbot;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.vaadin.flow.server.VaadinSession;
import oshi.util.tuples.Pair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchState {
    public SearchResponseDto handleSearch(String searchInput) {
        ArrayList<Pair<String,Double>> extractedKeywords = extractKeywords(searchInput);
        if(extractedKeywords.isEmpty()){
            //TODO handle error
        }
        return searchForPattern(extractedKeywords);
        //TODO do implement search
    }
    public void handleError(){

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

    public SearchResponseDto searchForPattern(ArrayList<Pair<String,Double>> keywords){
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
            SearchDto searchRequest = new SearchDto(nextSearchTag,keywords.get(0).getA(),designPatterns,excludedTags);
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

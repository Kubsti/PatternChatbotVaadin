package unibz.it.PatternChatbot.utility;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import oshi.util.tuples.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HelperUtilityImpl implements HelperUtility{

    public ArrayList<Pair<String,Double>> extractKeywords(String searchInput){
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
}

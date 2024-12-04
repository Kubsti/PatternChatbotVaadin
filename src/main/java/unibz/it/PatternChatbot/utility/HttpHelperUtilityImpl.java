package unibz.it.PatternChatbot.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.util.tuples.Pair;
import unibz.it.PatternChatbot.model.*;
import unibz.it.PatternChatbot.ui.ErrorDialog;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class HttpHelperUtilityImpl implements HttpHelperUtility {
    private final String server = "http://127.0.0.1:8080" ;
    private static final Logger logger = LoggerFactory.getLogger(HttpHelperUtilityImpl.class);
    @Override
    public void intializeChatbot() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.server + "/initialization"))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int responseCode = response.statusCode();
            if (responseCode != 200) {
                ErrorDialog.showError("Error occurred when trying to contact backend");
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                ObjectMapper mapper = new ObjectMapper();
                try{
                    SearchResponseDto initResponse = mapper.readValue(response.body(), SearchResponseDto.class);
                    VaadinSession.getCurrent().setAttribute("excludedTags", initResponse.getExcludedTags());
                    VaadinSession.getCurrent().setAttribute("nextSearchTag", initResponse.getNextSearchTag());
                    VaadinSession.getCurrent().setAttribute("nextQuestion", initResponse.getPatternQuestion());
                    VaadinSession.getCurrent().setAttribute("designPattern", initResponse.getDesignPatterns());
                    VaadinSession.getCurrent().setAttribute("possibleAnswers", initResponse.getCurrPossibleAnswersToQuestion());
                }catch (Exception e) {
                    ErrorDialog.showError("Error occurred when trying to read response from backend ");
                    System.out.println(e.getMessage());
                }

            }
        } catch (IOException | InterruptedException e) {
            ErrorDialog.showError("Error could not initialize chatbot.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void intializeChatbotWithFixedPatternSearchTag() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.server + "/initializationWithFixedPatternSearchTag"))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int responseCode = response.statusCode();
            if (responseCode != 200) {
                ErrorDialog.showError("Error occurred when trying to contact backend");
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                ObjectMapper mapper = new ObjectMapper();
                try{
                    SearchResponseDto initResponse = mapper.readValue(response.body(), SearchResponseDto.class);
                    VaadinSession.getCurrent().setAttribute("excludedTags", initResponse.getExcludedTags());
                    VaadinSession.getCurrent().setAttribute("nextSearchTag", initResponse.getNextSearchTag());
                    VaadinSession.getCurrent().setAttribute("nextQuestion", initResponse.getPatternQuestion());
                    VaadinSession.getCurrent().setAttribute("designPattern", initResponse.getDesignPatterns());
                    VaadinSession.getCurrent().setAttribute("possibleAnswers", initResponse.getCurrPossibleAnswersToQuestion());
                }catch (Exception e) {
                    ErrorDialog.showError("Error occurred when trying to read response from backend ");
                    System.out.println(e.getMessage());
                }

            }
        } catch (IOException | InterruptedException e) {
            ErrorDialog.showError("Error could not initialize chatbot.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public HttpResponse<String> getAnotherQuestion() {
        logger.info("Started to get another Question");
        String nextSearchTag = (String) VaadinSession.getCurrent().getAttribute("nextSearchTag");
        //TODO fix unchecked function error
        ArrayList<String> excludedTags = (ArrayList<String>) VaadinSession.getCurrent().getAttribute("excludedTags");
        DesignPatterns designPatterns = (DesignPatterns)VaadinSession.getCurrent().getAttribute("designPattern");
        //excludedTags.add(nextSearchTag);
        try{
            Gson gson = new GsonBuilder().create();
            HttpClient client = HttpClient.newHttpClient();
            NewQuestionDto newQuestionDto = new NewQuestionDto(excludedTags,designPatterns);
            String reqBody = gson.toJson(newQuestionDto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.server + "/getNewSearchQuestion"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int responseCode = response.statusCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                logger.info("Finished to get another Question");
                return  response;
            }

        }catch(Exception e){
            logger.error("Error in getting another question: {}", e.getMessage());
            e.printStackTrace();
            ErrorDialog.showError("An error occurred trying to get another question");
        }
        return null;
    }

    @Override
    public DesignPatterns getAllPattern() {
        logger.info("Started to get all pattern");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.server + "/getAllPattern"))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int responseCode = response.statusCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                Gson gson = new GsonBuilder().create();
                logger.info("Finished to get all pattern");
                return gson.fromJson(response.body(), DesignPatterns.class);
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error in getting all pattern {}",e.getMessage());
            ErrorDialog.showError("Error could not get all pattern");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SearchResponseDto searchForPatternWithKeywords(ArrayList<Pair<String, Double>> keywords, int keywordToSearchWith) {
        String nextSearchTag = (String ) VaadinSession.getCurrent().getAttribute("nextSearchTag");
        DesignPatterns designPatterns = (DesignPatterns)VaadinSession.getCurrent().getAttribute("designPattern");
        ArrayList<String> excludedTags;
        //TODO fix unchecked function error
        excludedTags = (ArrayList<String>) VaadinSession.getCurrent().getAttribute("excludedTags");

        try{
            Gson gson = new GsonBuilder().create();
            HttpClient client = HttpClient.newHttpClient();
            SearchDto searchRequest = new SearchDto(nextSearchTag,keywords.get(keywordToSearchWith).getA(),designPatterns,excludedTags);
            String reqBody = gson.toJson(searchRequest);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.server + "/searchPattern"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK){
                return  gson.fromJson(response.body(), SearchResponseDto.class);
            }else{
                //TODO implement better logging of errors
                ErrorDialog.showError("Error in the rest request for search pattern");
            }
        }catch (Exception e){
            ErrorDialog.showError("Error could not get all pattern");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SearchResponseDto searchForPattern(String tagValue) {
        logger.info("HttpHelper started to search for pattern");
        String nextSearchTag = (String ) VaadinSession.getCurrent().getAttribute("nextSearchTag");
        DesignPatterns designPatterns = (DesignPatterns)VaadinSession.getCurrent().getAttribute("designPattern");
        ArrayList<String> excludedTags;
        //TODO fix unchecked function error
        excludedTags = (ArrayList<String>) VaadinSession.getCurrent().getAttribute("excludedTags");

        try{
            Gson gson = new GsonBuilder().create();
            HttpClient client = HttpClient.newHttpClient();
            SearchDto searchRequest = new SearchDto(nextSearchTag,tagValue,designPatterns,excludedTags);
            String reqBody = gson.toJson(searchRequest);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.server + "/searchPattern"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK){
                logger.info("HttpHelper finished to search for pattern");
                return  gson.fromJson(response.body(), SearchResponseDto.class);
            }else{
                logger.error("HttpHelper search for pattern error in http request. HttpError {}\nErrorMessage: {}", response.statusCode(), response.body());
                ErrorDialog.showError("Error in the rest request for search pattern");
            }
        }catch (Exception e){
            logger.error("HttpHelper search for pattern error.\nErrorMessage: {}", e.getMessage());
            ErrorDialog.showError("Error could not get all pattern");
        }
        return null;
    }

    @Override
    public SearchResponseDto excludePattern(String tagValue) {
        logger.info("HttpHelper started to build request to exclude pattern");
        String nextSearchTag = (String ) VaadinSession.getCurrent().getAttribute("nextSearchTag");
        DesignPatterns designPatterns = (DesignPatterns)VaadinSession.getCurrent().getAttribute("designPattern");
        ArrayList<String> excludedTags;
        //TODO fix unchecked function error
        excludedTags = (ArrayList<String>) VaadinSession.getCurrent().getAttribute("excludedTags");

        try{
            Gson gson = new GsonBuilder().create();
            HttpClient client = HttpClient.newHttpClient();
            SearchDto searchRequest = new SearchDto(nextSearchTag,tagValue,designPatterns,excludedTags);
            String reqBody = gson.toJson(searchRequest);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.server + "/excludePattern"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK){
                logger.info("HttpHelper finished to search for pattern");
                return  gson.fromJson(response.body(), SearchResponseDto.class);
            }else{
                logger.error("HttpHelper search for pattern error in http request. HttpError {}\nErrorMessage: {}", response.statusCode(), response.body());
                ErrorDialog.showError("Error in the rest request for search pattern");
            }
        }catch (Exception e){
            logger.error("HttpHelper search for pattern error.\nErrorMessage: {}", e.getMessage());
            ErrorDialog.showError("Error could not get all pattern");
        }
        return null;
    }

    @Override
    public NearestPatternWeightedResponseDto getNearestPatternWeigthed(Pattern searchPattern, double similarityThreshold) {
        logger.info("Started get nearest Pattern weighted search");
        try{
            Gson gson = new GsonBuilder().create();
            HttpClient client = HttpClient.newHttpClient();
            NearestPatternWeightedDto nearestPatternDto = new NearestPatternWeightedDto(searchPattern,similarityThreshold);
            String reqBody = gson.toJson(nearestPatternDto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.server + "/getNearestPatternWeighted"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK){
                logger.info("Finished get nearest Pattern weighted search");
                return  gson.fromJson(response.body(), NearestPatternWeightedResponseDto.class);
            }else{
                logger.error("HttpHelper get nearest Pattern weighted search error in http request. HttpError {}\nErrorMessage: {}", response.statusCode(), response.body());
                ErrorDialog.showError("Error in the rest request to get the nearest Pattern weighted");
            }
        }catch (Exception e){
            logger.error("HttpHelper get nearest Pattern weighted search error.\nErrorMessage: {}", e.getMessage());
            ErrorDialog.showError("Error could not get nearest pattern");
        }
        return null;
    }

}

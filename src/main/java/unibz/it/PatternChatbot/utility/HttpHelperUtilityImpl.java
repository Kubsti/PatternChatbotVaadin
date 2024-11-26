package unibz.it.PatternChatbot.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.vaadin.flow.server.VaadinSession;
import oshi.util.tuples.Pair;
import unibz.it.PatternChatbot.model.*;
import unibz.it.PatternChatbot.ui.ErrorDialog;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Optional;

public class HttpHelperUtilityImpl implements HttpHelperUtility {
    private final String server = "http://localhost:8080" ;

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
    public HttpResponse<String> getAnotherQuestion() {
        String nextSearchTag = (String) VaadinSession.getCurrent().getAttribute("nextSearchTag");
        //TODO fix unchecked function error
        ArrayList<String> excludedTags = (ArrayList<String>) VaadinSession.getCurrent().getAttribute("excludedTags");
        //excludedTags.add(nextSearchTag);
        try{
            Gson gson = new GsonBuilder().create();
            HttpClient client = HttpClient.newHttpClient();
            NewQuestionDto newQuestionDto = new NewQuestionDto(excludedTags);
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
                return  response;
            }

        }catch(Exception e){
            e.printStackTrace();
            ErrorDialog.showError("An error occurred trying to get another question");
        }
        return null;
    }

    @Override
    public DesignPatterns getAllPattern() {
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
                return gson.fromJson(response.body(), DesignPatterns.class);
            }
        } catch (IOException | InterruptedException e) {
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
}

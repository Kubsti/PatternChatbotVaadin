package unibz.it.PatternChatbot;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import oshi.util.tuples.Pair;

import java.io.*;
import java.lang.reflect.Type;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChatView extends VerticalLayout {
    private MessageList chat;
    private MessageInput input;
    private List<MessageListItem> listOfMessages = new ArrayList<MessageListItem>();
    public MessageList getMessageList(){
        return chat;
    }

    public ChatView() {
        chat = new MessageList();
        input = new MessageInput();
        add(chat, input);
        Question question = (Question) VaadinSession.getCurrent().getAttribute("nextQuestion");
        MessageListItem firstMessage = new MessageListItem(
                question.question,
                Instant.now(), "PatternSearchBot");
        listOfMessages.add(firstMessage);
        chat.setItems(listOfMessages);
        input.addSubmitListener(this::onSubmit);

      this.setHorizontalComponentAlignment(Alignment.CENTER,
                chat, input);
      this.setPadding(true); // Leave some white space
      this.setHeightFull(); // We maximize to window
      chat.setSizeFull(); // Chat takes most of the space
      input.setWidthFull(); // Full width only
      //chat.setMaxWidth("800px"); // Limit the width
      //input.setMaxWidth("800px");
    }

    private void onSubmit(MessageInput.SubmitEvent submitEvent) {
        //add message of user to chat
        MessageListItem newMessage = new MessageListItem(submitEvent.getValue());
        listOfMessages.add(newMessage);
        chat.setItems(listOfMessages);
        //TODO implement communication with chatbot(create chatbot)
        File file = new File("Python/keyword_extractor.py");
        String absolutePathOfKeywordExtractor = file.getAbsolutePath();
        ProcessBuilder processBuilder = new ProcessBuilder("python", absolutePathOfKeywordExtractor,  newMessage.getText());
        try {
            Process pythonKeywordExtractor = processBuilder.start();
            BufferedReader reader = new BufferedReader((new InputStreamReader(pythonKeywordExtractor.getInputStream())));
            StringBuilder output = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                output.append(line);
            }
            reader.close();
            int exitCode = pythonKeywordExtractor.waitFor();
            //handle exitCode
            if(exitCode == 0){
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<ArrayList<Object>>>() {}.getType();
                List<ArrayList<Object>> javaList = gson.fromJson(output.toString(), listType);
                //if we did not find any pattern
                if(javaList.isEmpty()){
                    MessageListItem newBotMessage = new MessageListItem(
                            "I could not understand you answer could you try again?",
                            Instant.now(), "PatternSearchBot");
                    listOfMessages.add(newBotMessage);
                    chat.setItems(listOfMessages);
                    return;
                }
                ArrayList<Pair<String,Double>> tupleList = new ArrayList<>();
                for (ArrayList<Object> item : javaList) {
                    String first = (String) item.get(0);
                    Double second = Double.valueOf(item.get(1).toString()); // JSON numbers are parsed as Double
                    tupleList.add(new Pair<>(first, second));
                }
                //search with keywords for a pattern
                String nextSearchTag = (String )VaadinSession.getCurrent().getAttribute("nextSearchTag");
                DesingPatterns desingPatterns = (DesingPatterns)VaadinSession.getCurrent().getAttribute("designPattern");
                ArrayList<String> excludedTags;
                excludedTags = (ArrayList<String>) VaadinSession.getCurrent().getAttribute("excludedTags");
                try{

                    URL url = URI.create("http://localhost:8080/searchPattern").toURL();
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setRequestProperty("Accept", "application/json");
                    SearchDto searchRequest = new SearchDto(nextSearchTag,tupleList.get(0).getA(),desingPatterns,excludedTags);
                    String reqBody = gson.toJson(searchRequest);
                    try(DataOutputStream os = new DataOutputStream(conn.getOutputStream())){
                        os.writeBytes(reqBody);
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
                        SearchResponseDto searchResult= gson.fromJson(response.toString(), SearchResponseDto.class);
                        System.out.println("test");
                    }else{
                        //TODO Hanlde error with state etc.
                    }
                    //conn.connect();

                }catch(Exception e){
                    //Todo implement error hanlding
                }
//                MessageListItem firstMessage = new MessageListItem(
//                        nextSearchQuestion,
//                        Instant.now(), "PatternSearchBot");
//                listOfMessages.add(firstMessage);
//                chat.setItems(listOfMessages);


            }
            //If something found add tag to list of excluded tags
            //excludedTags.add(nextSearchTag);

        } catch (IOException | InterruptedException e) {
            //TODO better handle different exceptions
            throw new RuntimeException(e);
        }
    }

//    private List<String> readProcessOutput(InputStream inputStream) {
//    }
}

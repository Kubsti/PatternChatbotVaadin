package unibz.it.PatternChatbot;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import oshi.util.tuples.Pair;

import java.lang.reflect.Type;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
        MessageListItem firstMessage = new MessageListItem(
                "What is the domain of pattern your are looking for?",
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
                Object test = VaadinSession.getCurrent().getAttribute("nextSearchTag");
            }

            //handle keywords returned
        } catch (IOException | InterruptedException e) {
            //TODO better handle different exceptions
            throw new RuntimeException(e);
        }
    }

//    private List<String> readProcessOutput(InputStream inputStream) {
//    }
}

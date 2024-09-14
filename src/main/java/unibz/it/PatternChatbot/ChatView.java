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
    private SearchState searchState;
    public MessageList getMessageList(){
        return chat;
    }

    public ChatView() {
        chat = new MessageList();
        input = new MessageInput();
        searchState = new SearchState();
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
        //TODO check that input is not empty
        //add message of user to chat
        MessageListItem newMessage = new MessageListItem(submitEvent.getValue());
        listOfMessages.add(newMessage);
        chat.setItems(listOfMessages);

        MessageListItem botCalculating = new MessageListItem("Calculating response...", Instant.now(), "PatternSearchBot");
        listOfMessages.add(botCalculating);
        chat.setItems(listOfMessages);
        //Change logic depending on state
        String currState = (String ) VaadinSession.getCurrent().getAttribute("state");
        if(currState.equalsIgnoreCase("searchstate")){
            SearchResponseDto searchResult = searchState.handleSearch(submitEvent.getValue());
            if(searchResult == null || searchResult.getDesingPatterns().getPatterns().isEmpty()){

            }else{

            }
        }else if(currState.equalsIgnoreCase("errorstate")){

        }
        //TODO implement communication with chatbot(create chatbot)
        File file = new File("Python/keyword_extractor.py");
        String absolutePathOfKeywordExtractor = file.getAbsolutePath();
        ProcessBuilder processBuilder = new ProcessBuilder("python", absolutePathOfKeywordExtractor,  newMessage.getText());
//                MessageListItem firstMessage = new MessageListItem(
//                        nextSearchQuestion,
//                        Instant.now(), "PatternSearchBot");
//                listOfMessages.add(firstMessage);
//                chat.setItems(listOfMessages);

    }

    private void updateView(){

    }

}

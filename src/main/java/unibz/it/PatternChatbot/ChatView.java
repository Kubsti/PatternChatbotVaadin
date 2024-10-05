package unibz.it.PatternChatbot;

import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;

import java.io.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChatView extends VerticalLayout {
    private MessageList chat;
    private MessageInput input;
    private List<MessageListItem> listOfMessages = new ArrayList<MessageListItem>();
    private IFrame webpageIFrame = new IFrame();
    private State currentState = new IntentDiscoveryState();
    public MessageList getMessageList(){
        return chat;
    }

    public ChatView() {
        chat = new MessageList();
        input = new MessageInput();
        add(chat, input);
        //PatternQuestion question = (PatternQuestion) VaadinSession.getCurrent().getAttribute("nextQuestion");
        String startPhrase = "Hello I'm Pattera here to help you find the right pattern for your problem. How can I help you today?";
        MessageListItem firstMessage = new MessageListItem(
                startPhrase,
                Instant.now(), "Pattera");
        listOfMessages.add(firstMessage);
        chat.setItems(listOfMessages);
        input.addSubmitListener(this::onSubmit);

      this.setHorizontalComponentAlignment(Alignment.CENTER,
                chat, input);
      this.setPadding(true); // Leave some white space
      this.setHeightFull(); // We maximize to window
      chat.setSizeFull(); // Chat takes most of the space
      input.setWidthFull(); // Full width only
    }

    private void onSubmit(MessageInput.SubmitEvent submitEvent) {
        //TODO check that input is not empty
        //add message of user to chat

        this.currentState.handleInput(submitEvent.getValue(), this.currentState, this.chat,this.webpageIFrame);
//        MessageListItem newMessage = new MessageListItem(submitEvent.getValue());
//        listOfMessages.add(newMessage);
//        chat.setItems(listOfMessages);
//
//        MessageListItem botCalculating = new MessageListItem("Calculating response...", Instant.now(), "PatternSearchBot");
//        listOfMessages.add(botCalculating);
//        chat.setItems(listOfMessages);
//        //Change logic depending on state
//        String currState = (String ) VaadinSession.getCurrent().getAttribute("state");
//        if(currState.equalsIgnoreCase("searchstate")){
//            SearchResponseDto searchResult = searchState.handleSearch(submitEvent.getValue());
//            if(searchResult == null || searchResult.getDesignPatterns().getPatterns().isEmpty()){
//                //TODO handle special case here or in state
//            }else{
//                if(searchResult.getDesignPatterns().getPatterns().size() == 1 ){
//                    this.webpageIFrame.setSrc(searchResult.getDesignPatterns().getPatterns().get(0).url);
//
//                    //TODO switch to found state, show maybe reset button make new search etc.
//                }else{
//                    this.updateChat(searchResult);
//                }
//            }
//        }else if(currState.equalsIgnoreCase("errorstate")){
//
//        }

    }

    public void updateChat(SearchResponseDto searchResult){
        //Update session
        VaadinSession.getCurrent().setAttribute("excludedTags",searchResult.getExcludedTags());
        VaadinSession.getCurrent().setAttribute("nextSearchTag",searchResult.getNextSearchTag());
        VaadinSession.getCurrent().setAttribute("nextQuestion", searchResult.getPatternQuestion());
        VaadinSession.getCurrent().setAttribute("designPattern",searchResult.getDesignPatterns());

        PatternQuestion question = (PatternQuestion) VaadinSession.getCurrent().getAttribute("nextQuestion");
        MessageListItem message = new MessageListItem(
                question.getQuestion(),
                Instant.now(), "PatternSearchBot");
        listOfMessages.add(message);
        chat.setItems(listOfMessages);
    }

    private void updateView(){
        //TODO update view to link of a currently found pattern
    }


    public IFrame getWebpageIFrame() {
        return webpageIFrame;
    }

    public void setWebpageIFrame(IFrame webpageIFrame) {
        this.webpageIFrame = webpageIFrame;
    }
}

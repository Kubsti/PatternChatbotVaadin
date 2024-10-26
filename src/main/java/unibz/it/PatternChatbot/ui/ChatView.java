package unibz.it.PatternChatbot.ui;

import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unibz.it.PatternChatbot.model.PatternQuestion;
import unibz.it.PatternChatbot.model.SearchResponseDto;
import unibz.it.PatternChatbot.utility.ChatHelperUtility;
import unibz.it.PatternChatbot.utility.ChatHelperUtilityImpl;
import unibz.it.PatternChatbot.state.IntentDiscoveryState;
import unibz.it.PatternChatbot.state.State;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatView extends VerticalLayout {
    public MessageList chat;
    private MessageInput input;
    public List<MessageListItem> listOfMessages = new ArrayList<MessageListItem>();
    public IFrame webpageIFrame = new IFrame();
    @Autowired
    private final ChatHelperUtility chatHelper;
    public State currentState;

    public MessageList getMessageList(){
        return chat;
    }

    public ChatView() {
        chat = new MessageList();
        input = new MessageInput();
        add(chat, input);
        //PatternQuestion question = (PatternQuestion) VaadinSession.getCurrent().getAttribute("nextQuestion");
        this.outputStateInitMessage();
        input.addSubmitListener(this::onSubmit);
        chatHelper = new ChatHelperUtilityImpl(this);
        currentState = new IntentDiscoveryState(this.chatHelper);

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
        chatHelper.createChatMessage(submitEvent.getValue());
        Optional<State> newState = this.currentState.handleInput(submitEvent.getValue(), this.chat,this.webpageIFrame);
        if(newState.isPresent()){
            this.currentState =  newState.get();
            this.outputStateInitMessage();
        }
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

    public void createChatMessage(String chatMessage){
        MessageListItem firstMessage = new MessageListItem(
                chatMessage,
                Instant.now(), "Pattera");
        listOfMessages.add(firstMessage);
        chat.setItems(listOfMessages);
    }

    public void createSearchStateChatMessage(){
        PatternQuestion question = (PatternQuestion) VaadinSession.getCurrent().getAttribute("nextQuestion");
        List<MessageListItem> messages = new ArrayList<MessageListItem>();
        messages.addAll(chat.getItems());
        messages.add(new MessageListItem(
                question.getQuestion(),
                Instant.now(), "Pattera"));
        chat.setItems(messages);
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

    private void outputStateInitMessage(){
//        StringBuilder startPhrase = new StringBuilder(this.currentState.InitializationMessage);
//        startPhrase.append("\nOptions:");
//        for(String option : this.currentState.Options) {
//            startPhrase.append("\n").append(option);
//        }
//        MessageListItem firstMessage = new MessageListItem(
//                startPhrase.toString(),
//                Instant.now(), "Pattera");
//        listOfMessages.add(firstMessage);
//        chat.setItems(listOfMessages);
    }

    public IFrame getWebpageIFrame() {
        return webpageIFrame;
    }

    public void setWebpageIFrame(IFrame webpageIFrame) {
        this.webpageIFrame = webpageIFrame;
    }
}

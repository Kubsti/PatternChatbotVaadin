package unibz.it.PatternChatbot.ui;

import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import unibz.it.PatternChatbot.utility.UiHelperUtility;
import unibz.it.PatternChatbot.utility.UiHelperUtilityImpl;
import unibz.it.PatternChatbot.state.IntentDiscoveryState;
import unibz.it.PatternChatbot.model.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatView extends VerticalLayout {
    public MessageList chat;
    private MessageInput input;
    public List<MessageListItem> listOfMessages = new ArrayList<MessageListItem>();
    public IFrame webpageIFrame;
    @Autowired
    private final UiHelperUtility chatHelper;
    public State currentState;

    public MessageList getMessageList(){
        return chat;
    }

    public ChatView() {
        chat = new MessageList();
        input = new MessageInput();
        add(chat, input);
        input.addSubmitListener(this::onSubmit);
        chatHelper = new UiHelperUtilityImpl(this);
        currentState = new IntentDiscoveryState(this.chatHelper, true);

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
        Optional<State> newState = this.currentState.handleInput(submitEvent.getValue());
        if(newState.isPresent()){
            this.currentState =  newState.get();
        }
    }
}

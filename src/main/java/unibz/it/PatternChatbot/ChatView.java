package unibz.it.PatternChatbot;

import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

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
        MessageListItem message1 = new MessageListItem(
                "Please tell me which pattern you would like to search?",
                Instant.now(), "Patty");
        listOfMessages.add(message1);
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
        MessageListItem newMessage = new MessageListItem(submitEvent.getValue());
        listOfMessages.add(newMessage);
        chat.setItems(listOfMessages);
        //TODO implement communication with chatbot(create chatbot)

    }
}

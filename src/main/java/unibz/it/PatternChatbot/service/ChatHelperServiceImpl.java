package unibz.it.PatternChatbot.service;

import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;
import unibz.it.PatternChatbot.model.PatternQuestion;
import unibz.it.PatternChatbot.ui.ChatView;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatHelperServiceImpl implements ChatHelperService{
    private final ChatView currentChatView;

    public ChatHelperServiceImpl(ChatView currentChatView) {
        this.currentChatView = currentChatView;
    }

    @Override
    public void createChatMessage(String chatMessage) {
        List<MessageListItem> messages = new ArrayList<MessageListItem>();
        messages.addAll(currentChatView.chat.getItems());
        messages.add(new MessageListItem(
                chatMessage,
                Instant.now(), "Pattera"));
        currentChatView.chat.setItems(messages);
    }

    @Override
    public void updateIFrame(String newSource) {
        this.currentChatView.webpageIFrame.setSrc(newSource);
    }
}

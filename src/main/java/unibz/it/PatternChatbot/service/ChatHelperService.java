package unibz.it.PatternChatbot.service;

import com.vaadin.flow.component.messages.MessageListItem;

import java.time.Instant;

public interface ChatHelperService {
    public void createChatMessage(String chatMessage);
    public void updateIFrame(String newSource);
}

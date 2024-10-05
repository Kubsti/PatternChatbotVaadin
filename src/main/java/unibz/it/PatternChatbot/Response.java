package unibz.it.PatternChatbot;

import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;

public abstract class Response {
    public abstract void responseAction(String input, State currState, MessageList chat, IFrame webpageIFrame);
    //TODO check if really needed
    //public abstract void stateSwitch(State currState, );
}

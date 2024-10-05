package unibz.it.PatternChatbot;

import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
public abstract class State {
    //Pattern must be stored in order of which they are supposed to be matched
    public HashMap<Pattern, Response> Rules;
    public ArrayList<String> Options;

    public abstract void handleError();
    public abstract void handleInput(String chatInput, State currState, MessageList chat, IFrame webpageIFrame);
    public abstract void setupResponses();
    public abstract void setupOptions();
}

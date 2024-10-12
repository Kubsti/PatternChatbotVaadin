package unibz.it.PatternChatbot.state;

import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import unibz.it.PatternChatbot.model.Response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
public abstract class State {
    //Pattern must be stored in order of which they are supposed to be matched
    public LinkedHashMap<Pattern, Response> Rules;
    public ArrayList<String> Options;

    public abstract void handleError();
    public abstract State handleInput(String chatInput, MessageList chat, IFrame webpageIFrame);
    public abstract void setupResponses();
    public abstract void setupOptions();
}

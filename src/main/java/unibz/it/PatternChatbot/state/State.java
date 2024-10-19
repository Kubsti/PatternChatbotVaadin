package unibz.it.PatternChatbot.state;

import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import unibz.it.PatternChatbot.model.Response;
import unibz.it.PatternChatbot.ui.ChatView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
public abstract class State {
    //Pattern must be stored in order of which they are supposed to be matched
    public LinkedHashMap<Pattern, Response> Rules;
    public ArrayList<String> Options;
    public String InitializationMessage;

    public abstract void handleError();
    public abstract Optional<State> handleInput(String chatInput, MessageList chat, IFrame webpageIFrame);
    public abstract void setupResponses();
    public abstract void setupOptions();
}

package unibz.it.PatternChatbot;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class State {
    public HashMap<Pattern, Response> Rules;
    public ArrayList<String> Options;

    public abstract void handleError();
    public abstract void handleInput(String chatInput, State currState);
}

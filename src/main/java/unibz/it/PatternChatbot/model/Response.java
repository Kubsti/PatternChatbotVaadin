package unibz.it.PatternChatbot.model;

import java.util.ArrayList;

public abstract class Response {
    public abstract State responseAction(String input, ArrayList<String> stateOptions) throws StateException;
}

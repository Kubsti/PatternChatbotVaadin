package unibz.it.PatternChatbot.model;

public abstract class Response {
    public abstract State responseAction(String input);
    //TODO check if really needed
    //public abstract void stateSwitch(State currState, );
}

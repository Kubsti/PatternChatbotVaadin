package unibz.it.PatternChatbot;

public abstract class Response {
    public String response;
    public abstract void responseAction();
    //TODO check if really needed
    //public abstract void stateSwitch(State currState, );
}

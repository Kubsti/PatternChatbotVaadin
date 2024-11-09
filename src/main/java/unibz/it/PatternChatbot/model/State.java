package unibz.it.PatternChatbot.model;

import unibz.it.PatternChatbot.state.GuidedSearchState;
import unibz.it.PatternChatbot.ui.ErrorDialog;
import unibz.it.PatternChatbot.utility.UiHelperUtility;

import java.util.*;
import java.util.regex.Pattern;
public abstract class State {
    //Pattern must be stored in order of which they are supposed to be matched
    public LinkedHashMap<Pattern, Response> Rules;
    public ArrayList<String> Options;
    public String InitializationMessage;
    public UiHelperUtility chatHelper;
    public HashMap<String, StateException> Exceptions ;
    public State(UiHelperUtility chatHelper, String initMessage, boolean showInitMessage){
        this.Rules = new LinkedHashMap<Pattern, Response>();
        this.Options = new ArrayList<String>();
        this.Exceptions = new HashMap<String, StateException>();
        this.setupResponses();
        this.setupOptions();
        this.chatHelper = chatHelper;
        this.InitializationMessage = initMessage;
        if(showInitMessage){
            createInitMessage();
        }
    }
    public  State handleError(StateException e, String input){
        for(Map.Entry<String, StateException> set :
                this.Exceptions.entrySet()){
            if(set.getKey().equalsIgnoreCase(e.getExceptionName())) {
                try{
                    return e.handleException(input);
                }catch (Error error){
                    ErrorDialog.showError("An error occurred");
                    return null;
                }
            }
        }
        return null;
    };
    public Optional<State> handleInput(String chatInput){
        for (Map.Entry<Pattern, Response> set :
                this.Rules.entrySet()) {
            //Try to match a Rule
            if(set.getKey().matcher(chatInput).find()) {
                try{
                    return Optional.of(new GuidedSearchState(chatHelper, false));
                }catch (Exception e){
                    if (e instanceof StateException) {
                        StateException stateException = (StateException) e;
                        return Optional.ofNullable(handleError(stateException, chatInput));
                    } else {
                        //TODO handle other exceptions
                        System.out.println("Caught a different type of exception: " + e.getClass().getSimpleName());
                    }
                }
            }
        }
        return Optional.empty();
    };
    public abstract void setupResponses();
    public abstract void setupOptions();
    public abstract void createInitMessage();
    public abstract void setupExceptions();
}

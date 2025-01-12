package unibz.it.PatternChatbot.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unibz.it.PatternChatbot.PatternChatbotController;
import unibz.it.PatternChatbot.state.GuidedSearchState;
import unibz.it.PatternChatbot.ui.ErrorDialog;
import unibz.it.PatternChatbot.utility.HelperUtilityImpl;
import unibz.it.PatternChatbot.utility.HttpHelperUtilityImpl;
import unibz.it.PatternChatbot.utility.UiHelperUtility;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
public abstract class State {
    public static final Logger logger = LoggerFactory.getLogger(State.class);
    //Pattern must be stored in order of which they are supposed to be matched
    public LinkedHashMap<Pattern, Response> Rules;
    public ArrayList<String> Options;
    public String InitializationMessage;
    public UiHelperUtility chatHelper;
    public HashMap<String, Function<String, State>> Exceptions ;
    public HttpHelperUtilityImpl httpHelper;
    public HelperUtilityImpl helperUtility;
    public State(UiHelperUtility chatHelper, String initMessage, boolean showInitMessage){
        this.Rules = new LinkedHashMap<Pattern, Response>();
        this.Options = new ArrayList<String>();
        this.Exceptions = new HashMap<String,  Function<String, State>>();
        this.setupResponses();
        this.setupOptions();
        this.setupExceptions();
        this.chatHelper = chatHelper;
        this.InitializationMessage = initMessage;
        this.httpHelper = new HttpHelperUtilityImpl();
        this.helperUtility = new HelperUtilityImpl();
        if(showInitMessage){
            createInitMessage();
            logger.info("Entered State: {}", this.getClass().getSimpleName());
        }
    }
    public  State handleError(StateException e, String input){
        for(Map.Entry<String, Function<String, State>> set :
                this.Exceptions.entrySet()){
            if(set.getKey().equalsIgnoreCase(e.getExceptionName())) {
                try{
                    return set.getValue().apply(input);
                }catch (Error error){
                    //TODO check if better error handling is needed
                    ErrorDialog.showError("An error occurred");
                    return null;
                }
            }
        }
        return null;
    };
    public Optional<State> handleInput(String chatInput, ArrayList<String> stateOptions){
        for (Map.Entry<Pattern, Response> set :
                this.Rules.entrySet()) {
            //Try to match a Rule
            if(set.getKey().matcher(chatInput).find()) {
                try{
                    logger.info("Respond with Rule: {}", set.getKey());
                    return Optional.ofNullable(set.getValue().responseAction(chatInput,stateOptions));
                }catch (Exception e){
                    if (e instanceof StateException) {
                        logger.error("Error in respond with Rule: {} going into StateException handling. Error: {}", set.getKey(), ((StateException) e).getExceptionName());
                        StateException stateException = (StateException) e;
                        return Optional.ofNullable(handleError(stateException, chatInput));
                    } else {
                        logger.error("Error in respond with Rule: {} entering normal handling. Error: {}", set.getKey(), e.getMessage());
                        e.printStackTrace();
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

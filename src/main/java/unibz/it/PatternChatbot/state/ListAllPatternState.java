package unibz.it.PatternChatbot.state;

import com.vaadin.flow.component.messages.MessageListItem;
import unibz.it.PatternChatbot.model.DesignPatterns;
import unibz.it.PatternChatbot.model.Response;
import unibz.it.PatternChatbot.model.State;
import unibz.it.PatternChatbot.model.StateException;
import unibz.it.PatternChatbot.utility.UiHelperUtility;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ListAllPatternState extends State {
    private ArrayList<unibz.it.PatternChatbot.model.Pattern> allAvailablePattern;
    public ListAllPatternState(UiHelperUtility chatHelper, boolean showInitMessage) {
        super(chatHelper, "To get more infos displayed about a pattern the name of an available pattern.", showInitMessage);
    }

    @Override
    public void setupResponses() {
        //1. Request for a Guided Search
        this.Rules.put(Pattern.compile("1.*|1\\."
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) throws StateException {
                return new GuidedSearchState(chatHelper, true);
            }
        });
        //2. List all available patterns
        this.Rules.put(Pattern.compile("2.*|2\\."
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) throws StateException {
                StringBuilder startPhrase = new StringBuilder("To get more infos displayed about a pattern input the name of an available pattern.\n");
                DesignPatterns response = httpHelper.getAllPattern();
                if (response != null) {
                    if (!response.getPatterns().isEmpty()) {
                        startPhrase.append("Pattern:");
                        response.getPatterns().forEach((pattern -> startPhrase.append("\n").append(pattern.name).append("\",")));
                        setAllAvailablePattern(response.getPatterns());
                        chatHelper.createPatteraChatMessage(startPhrase.toString());
                    } else {
                        chatHelper.createPatteraChatMessage("Sorry it seems I have no patterns stored at the moment, but maybe you could give me some ;).");
                    }
                }
                return null;
            }
        });
        //Fallback assume search for pattern to get more infos
        this.Rules.put(Pattern.compile(".*"
                , Pattern.CASE_INSENSITIVE), new Response() {
            @Override
            public State responseAction(String input, ArrayList<String> stateOptions) {
                for(unibz.it.PatternChatbot.model.Pattern pattern : getAllAvailablePattern()){
                    if(pattern.name.equalsIgnoreCase(input)){
                        StringBuilder startPhrase = new StringBuilder("Here, i loaded more infos about the patten.");
                        startPhrase.append("\nOptions:");
                        for (String option : stateOptions) {
                            startPhrase.append("\n").append(option);
                        }
                        chatHelper.createPatteraChatMessage(startPhrase.toString());
                        return null;
                    }
                }
                chatHelper.createPatteraChatMessage("Sorry i could not find any Pattern which matches " + input + ". If your Pattern is in the list of all pattern please check if the spelling is correct, and try again.");
                return null;
            }
        });
    }

    @Override
    public void setupOptions() {
        this.Options.add("1. Request for a Guided Search");
        this.Options.add("2. List all available patterns");
    }

    @Override
    public void createInitMessage() {
        allAvailablePattern = new ArrayList<unibz.it.PatternChatbot.model.Pattern>();
        StringBuilder startPhrase = new StringBuilder(this.InitializationMessage);
        startPhrase.append("\nOptions:");
        for (String option : this.Options) {
            startPhrase.append("\n").append(option);
        }
        DesignPatterns response = httpHelper.getAllPattern();
        if (response != null) {
            getAllAvailablePattern().addAll(response.getPatterns());
            if (!response.getPatterns().isEmpty()) {
                startPhrase.append("Pattern:");
                response.getPatterns().forEach((pattern -> startPhrase.append("\"").append(pattern.name).append("\"")));
                chatHelper.createPatteraChatMessage(startPhrase.toString());
            } else {
                chatHelper.createPatteraChatMessage("Sorry it seems I have no patterns stored at the moment, but maybe you could give me some ;).");
            }
        }
    }

    @Override
    public void setupExceptions() {

    }

    public ArrayList<unibz.it.PatternChatbot.model.Pattern> getAllAvailablePattern() {
        return allAvailablePattern;
    }

    public void setAllAvailablePattern(ArrayList<unibz.it.PatternChatbot.model.Pattern> allAvailablePattern) {
        this.allAvailablePattern = allAvailablePattern;
    }
}

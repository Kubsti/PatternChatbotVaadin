package unibz.it.PatternChatbot.utility;

import java.util.ArrayList;
import java.util.HashSet;

public interface UiHelperUtility {
    public void createPatteraChatMessage(String chatMessage);
    public void createPatteraSearchAnswer(String chatMessage, ArrayList<String> options, ArrayList<String> possibleAnswers);
    public void createChatMessage(String chatMessage);
    //public void updateIFrame(String newSource);
    public void updatePdfViewer(String newSource);
}

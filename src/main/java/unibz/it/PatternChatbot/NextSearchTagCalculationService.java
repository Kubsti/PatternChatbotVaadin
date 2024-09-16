package unibz.it.PatternChatbot;

import java.util.ArrayList;

public interface NextSearchTagCalculationService {
    public abstract String calculateNextSearchTag(DesignPatterns designPatterns, ArrayList<String> excludedTags);
}

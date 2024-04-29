package unibz.it.PatternChatbot;

import java.util.ArrayList;

public interface NextSearchTagCalculationService {
    public abstract String calculateNextSearchTag(DesingPatterns desingPatterns, ArrayList<String> excludedTags);
}

package unibz.it.PatternChatbot;

import java.util.ArrayList;

public interface PatternSearchService {
    public abstract DesingPatterns searchPatterns(String searchTag, String searchTagValue, ArrayList<Pattern> listOfPattern);
}

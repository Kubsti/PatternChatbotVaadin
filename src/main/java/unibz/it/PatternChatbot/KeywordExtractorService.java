package unibz.it.PatternChatbot;

import java.util.List;

public interface KeywordExtractorService {
    public abstract List<String> extractKeywords(String inputText);
}

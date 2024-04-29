package unibz.it.PatternChatbot;

import java.io.IOException;

public interface FileReaderService {
    public abstract DesingPatterns getDesingPatterns(String filePath) throws IOException;
    public abstract PatternQuestions getPatternQuestions(String filePath) throws IOException;
}

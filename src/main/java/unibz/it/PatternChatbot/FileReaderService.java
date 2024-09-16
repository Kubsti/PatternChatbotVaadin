package unibz.it.PatternChatbot;

import java.io.IOException;

public interface FileReaderService {
    public abstract DesignPatterns getDesignPatterns(String filePath) throws IOException;
    public abstract PatternQuestions getPatternQuestions(String filePath) throws IOException;
}

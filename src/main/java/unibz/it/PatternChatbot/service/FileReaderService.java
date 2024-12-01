package unibz.it.PatternChatbot.service;

import unibz.it.PatternChatbot.model.DesignPatterns;
import unibz.it.PatternChatbot.model.PatternQuestions;

import java.io.IOException;

public interface FileReaderService {
    public abstract DesignPatterns getDesignPatterns(String filePath) throws IOException;
    public abstract PatternQuestions getPatternQuestions(String filePath) throws IOException;
}

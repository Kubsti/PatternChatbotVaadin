package unibz.it.PatternChatbot.service;

import unibz.it.PatternChatbot.model.DesignPatterns;

import java.util.ArrayList;
import java.util.HashSet;

public interface QuestionAnswerClaculationService {
    public abstract ArrayList<String> calculatePossibleAnswers(String tagName, DesignPatterns designPatterns);
}

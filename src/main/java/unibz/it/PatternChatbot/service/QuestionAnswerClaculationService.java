package unibz.it.PatternChatbot.service;

import unibz.it.PatternChatbot.model.DesignPatterns;

import java.util.HashSet;

public interface QuestionAnswerClaculationService {
    public abstract HashSet<String> calculatePossibleAnswers(String tagName, DesignPatterns designPatterns);
}

package unibz.it.PatternChatbot.service;

import unibz.it.PatternChatbot.model.DesignPatterns;
import unibz.it.PatternChatbot.model.PatternQuestions;

public interface QuestionWriterService
{
    boolean writeQuestions(PatternQuestions listOfQuestions);
}

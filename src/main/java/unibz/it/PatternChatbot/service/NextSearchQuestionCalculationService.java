package unibz.it.PatternChatbot.service;

import unibz.it.PatternChatbot.model.PatternQuestion;
import unibz.it.PatternChatbot.model.PatternQuestions;

public interface NextSearchQuestionCalculationService {
    public abstract PatternQuestion calculateNextSearchQuestion(String nextSearchTag, PatternQuestions patternQuestions);
}

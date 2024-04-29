package unibz.it.PatternChatbot;

import java.util.ArrayList;

public interface NextSearchQuestionCalculationService {
    public abstract Question calculateNextSearchQuestion(String nextSearchTag, PatternQuestions patternQuestions);
}

package unibz.it.PatternChatbot;

public interface NextSearchQuestionCalculationService {
    public abstract PatternQuestion calculateNextSearchQuestion(String nextSearchTag, PatternQuestions patternQuestions);
}

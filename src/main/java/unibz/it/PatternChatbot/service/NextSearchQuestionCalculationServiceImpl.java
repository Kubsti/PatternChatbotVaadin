package unibz.it.PatternChatbot.service;

import org.springframework.stereotype.Service;
import unibz.it.PatternChatbot.model.PatternQuestion;
import unibz.it.PatternChatbot.model.PatternQuestions;

@Service
public class NextSearchQuestionCalculationServiceImpl implements NextSearchQuestionCalculationService {
    @Override
    public PatternQuestion calculateNextSearchQuestion(String nextSearchTag, PatternQuestions patternQuestions) {
        PatternQuestion nextQuestion = new PatternQuestion("","");
        nextQuestion.setQuestion("No question found");
        for(PatternQuestion currQuestion : patternQuestions.getQuestions()){
            if(currQuestion.getTagName().equalsIgnoreCase(nextSearchTag)){
                nextQuestion = currQuestion;
            }
        }
        return nextQuestion;
    }
}

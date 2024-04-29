package unibz.it.PatternChatbot;

import org.springframework.stereotype.Service;

@Service
public class NextSearchQuestionCalculationServiceImpl implements NextSearchQuestionCalculationService{
    @Override
    public Question calculateNextSearchQuestion(String nextSearchTag, PatternQuestions patternQuestions) {
        Question nextQuestion = new Question();
        nextQuestion.setQuestion("No question found");
        for(Question currQuestion : patternQuestions.getQuestions()){
            if(currQuestion.getTagName().equalsIgnoreCase(nextSearchTag)){
                nextQuestion = currQuestion;
            }
        }
        return nextQuestion;
    }
}

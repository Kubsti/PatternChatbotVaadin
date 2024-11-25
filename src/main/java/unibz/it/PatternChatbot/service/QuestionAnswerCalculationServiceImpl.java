package unibz.it.PatternChatbot.service;

import org.springframework.stereotype.Service;
import unibz.it.PatternChatbot.model.DesignPatterns;
import unibz.it.PatternChatbot.model.Pattern;

import java.util.HashSet;
@Service
public class QuestionAnswerCalculationServiceImpl implements QuestionAnswerClaculationService{
    @Override
    public HashSet<String> calculatePossibleAnswers(String tagName, DesignPatterns designPatterns) {
        HashSet<String> possibleAnswers = new HashSet<String>();
        designPatterns.getPatterns().stream().forEach(pattern -> {
            pattern.getTags().stream().forEach(tag -> {
                if(tag.tagName.equalsIgnoreCase(tagName)){
                    possibleAnswers.add(tag.tagName);
                }
            });
        });
        return possibleAnswers;
    }
}

package unibz.it.PatternChatbot.service;

import org.springframework.stereotype.Service;
import unibz.it.PatternChatbot.model.DesignPatterns;
import unibz.it.PatternChatbot.model.Pattern;

import java.util.ArrayList;
import java.util.HashSet;
@Service
public class QuestionAnswerCalculationServiceImpl implements QuestionAnswerClaculationService{
    @Override
    public ArrayList<String> calculatePossibleAnswers(String tagName, DesignPatterns designPatterns) {
        HashSet<String> possibleAnswers = new HashSet<String>();
        designPatterns.getPatterns().stream().forEach(pattern -> {
            pattern.getTags().stream().forEach(tag -> {
                if(tag.tagName.equalsIgnoreCase(tagName)){
                    possibleAnswers.add(tag.tagValue);
                }
            });
        });
        return new ArrayList<String>(possibleAnswers);
    }
}

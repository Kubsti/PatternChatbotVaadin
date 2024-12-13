package unibz.it.PatternChatbot.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class UpdatePatternAndQuestionDto {
    private ArrayList<Pattern>  patterns;
    private ArrayList<PatternQuestion> questions;
    @JsonCreator
    public UpdatePatternAndQuestionDto(@JsonProperty("patterns") ArrayList<Pattern> patterns,@JsonProperty("questions") ArrayList<PatternQuestion> questions) {
        this.patterns = patterns;
        this.questions = questions;
    }

    public ArrayList<Pattern>  getPatterns() {
        return patterns;
    }

    public void setPatterns(ArrayList<Pattern>  patterns) {
        this.patterns = patterns;
    }

    public ArrayList<PatternQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<PatternQuestion> questions) {
        this.questions = questions;
    }
}

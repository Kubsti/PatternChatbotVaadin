package unibz.it.PatternChatbot.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class NewQuestionResponseDto {
    private PatternQuestion patternQuestion;
    private ArrayList<String> excludedTags;
    private String nextSearchTag;

    @JsonCreator
    public NewQuestionResponseDto(@JsonProperty("patternQuestion") PatternQuestion patternQuestion, @JsonProperty("excludedTags")  ArrayList<String> excludedTags, @JsonProperty("nextSearchTag") String nextSearchTag) {
        this.patternQuestion = patternQuestion;
        this.excludedTags = excludedTags;
        this.nextSearchTag = nextSearchTag;
    }
    public PatternQuestion getPatternQuestion() {
        return patternQuestion;
    }

    public void setPatternQuestion(PatternQuestion patternQuestion) {
        this.patternQuestion = patternQuestion;
    }

    public ArrayList<String> getExcludedTags() {
        return excludedTags;
    }

    public void setExcludedTags(ArrayList<String> excludedTags) {
        this.excludedTags = excludedTags;
    }

    public String getNextSearchTag() {
        return nextSearchTag;
    }

    public void setNextSearchTag(String nextSearchTag) {
        this.nextSearchTag = nextSearchTag;
    }
}

package unibz.it.PatternChatbot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class SearchResponseDto {
    private DesingPatterns desingPatterns;
    private Question question;
    private ArrayList<String> excludedTags;

    private String nextSearchTag;
    @JsonCreator
    public SearchResponseDto(@JsonProperty("desingPatterns") DesingPatterns desingPatterns, @JsonProperty("question") Question question,   @JsonProperty("excludedTags")  ArrayList<String> excludedTags, @JsonProperty("nextSearchTag") String nextSearchTag) {
        this.desingPatterns = desingPatterns;
        this.question = question;
        this.excludedTags = excludedTags;
        this.nextSearchTag = nextSearchTag;
    }

    public DesingPatterns getDesingPatterns() {
        return desingPatterns;
    }

    public void setDesingPatterns(DesingPatterns desingPatterns) {
        this.desingPatterns = desingPatterns;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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

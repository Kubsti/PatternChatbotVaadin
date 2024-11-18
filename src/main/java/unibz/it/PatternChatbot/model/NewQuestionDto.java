package unibz.it.PatternChatbot.model;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
public class NewQuestionDto {
    private ArrayList<String> excludedTags;
    
    @JsonCreator
    public NewQuestionDto(@JsonProperty("excludedTags")  ArrayList<String> excludedTags) {
        this.excludedTags = excludedTags;
    }

    public ArrayList<String> getExcludedTags() {
        return excludedTags;
    }

    public void setExcludedTags(ArrayList<String> excludedTags) {
        this.excludedTags = excludedTags;
    }
}

package unibz.it.PatternChatbot.model;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
public class NewQuestionDto {
    private ArrayList<String> excludedTags;
    private DesignPatterns designPatterns;
    @JsonCreator
    public NewQuestionDto(@JsonProperty("excludedTags")  ArrayList<String> excludedTags, @JsonProperty("designPatterns")  DesignPatterns designPatterns) {
        this.excludedTags = excludedTags;
        this.designPatterns = designPatterns;
    }

    public ArrayList<String> getExcludedTags() {
        return excludedTags;
    }

    public void setExcludedTags(ArrayList<String> excludedTags) {
        this.excludedTags = excludedTags;
    }

    public DesignPatterns getDesignPatterns() {
        return designPatterns;
    }

    public void setDesignPatterns(DesignPatterns designPatterns) {
        this.designPatterns = designPatterns;
    }
}

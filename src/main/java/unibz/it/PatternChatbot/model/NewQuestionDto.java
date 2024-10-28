package unibz.it.PatternChatbot.model;

import java.util.ArrayList;

public class NewQuestionDto {
    private ArrayList<String> excludedTags;

    public NewQuestionDto(ArrayList<String> excludedTags) {
        this.excludedTags = excludedTags;
    }
    public ArrayList<String> getExcludedTags() {
        return excludedTags;
    }

    public void setExcludedTags(ArrayList<String> excludedTags) {
        this.excludedTags = excludedTags;
    }
}

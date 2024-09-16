package unibz.it.PatternChatbot;

import java.util.ArrayList;

public class SearchDto {
    private String currSearchTag;
    private String searchTagValue;
    private DesignPatterns designPatterns;
    private ArrayList<String> excludedTags;

    public String getCurrSearchTag() {
        return currSearchTag;
    }

    public void setCurrSearchTag(String currSearchTag) {
        this.currSearchTag = currSearchTag;
    }

    public String getSearchTagValue() {
        return searchTagValue;
    }

    public void setSearchTagValue(String searchTagValue) {
        this.searchTagValue = searchTagValue;
    }

    public DesignPatterns getDesignPatterns() {
        return designPatterns;
    }

    public void setDesignPatterns(DesignPatterns designPatterns) {
        this.designPatterns = designPatterns;
    }

    public ArrayList<String> getExcludedTags() {
        return excludedTags;
    }

    public void setExcludedTags(ArrayList<String> excludedTags) {
        this.excludedTags = excludedTags;
    }

    public SearchDto(String currSearchTag, String searchTagValue, DesignPatterns designPatterns, ArrayList<String> excludedTags) {
        this.currSearchTag = currSearchTag;
        this.searchTagValue = searchTagValue;
        this.designPatterns = designPatterns;
        this.excludedTags = excludedTags;
    }
}

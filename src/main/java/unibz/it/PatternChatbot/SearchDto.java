package unibz.it.PatternChatbot;

import java.util.ArrayList;

public class SearchDto {
    private String currSearchTag;
    private String searchTagValue;
    private DesingPatterns desingPatterns;
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

    public DesingPatterns getDesingPatterns() {
        return desingPatterns;
    }

    public void setDesingPatterns(DesingPatterns desingPatterns) {
        this.desingPatterns = desingPatterns;
    }

    public ArrayList<String> getExcludedTags() {
        return excludedTags;
    }

    public void setExcludedTags(ArrayList<String> excludedTags) {
        this.excludedTags = excludedTags;
    }

    public SearchDto(String currSearchTag, String searchTagValue, DesingPatterns desingPatterns, ArrayList<String> excludedTags) {
        this.currSearchTag = currSearchTag;
        this.searchTagValue = searchTagValue;
        this.desingPatterns = desingPatterns;
        this.excludedTags = excludedTags;
    }
}

package unibz.it.PatternChatbot.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NearestPatternWeightedDto {
    private Pattern searchPattern;
    private double similarityThreshold;

    @JsonCreator
    public NearestPatternWeightedDto(@JsonProperty("searchPattern") Pattern searchPattern, @JsonProperty("similarityThreshold") double similarityThreshold) {
        this.searchPattern = searchPattern;
        this.similarityThreshold = similarityThreshold;
    }

    public Pattern getSearchPattern() {
        return searchPattern;
    }

    public void setSearchPattern(Pattern searchPattern) {
        this.searchPattern = searchPattern;
    }

    public double getSimilaryThreshold() {
        return similarityThreshold;
    }

    public void setSimilaryThreshold(double similaryThreshold) {
        this.similarityThreshold = similaryThreshold;
    }

}

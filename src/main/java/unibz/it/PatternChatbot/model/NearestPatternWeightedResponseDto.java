package unibz.it.PatternChatbot.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class NearestPatternWeightedResponseDto {
    private ArrayList<Pattern> nearestPattern;

    @JsonCreator
    public NearestPatternWeightedResponseDto(@JsonProperty("nearestPattern") ArrayList<Pattern> nearestPattern) {
        this.nearestPattern = nearestPattern;
    }
    public ArrayList<Pattern> getNearestPattern() {
        return nearestPattern;
    }

    public void setNearestPattern(ArrayList<Pattern> nearestPattern) {
        this.nearestPattern = nearestPattern;
    }
}

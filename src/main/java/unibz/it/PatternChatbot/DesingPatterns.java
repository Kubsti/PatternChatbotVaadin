package unibz.it.PatternChatbot;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class DesingPatterns{
    @JsonProperty("patterns")
    public ArrayList<Pattern> getPatterns() {
        return this.patterns; }
    public void setPatterns(ArrayList<Pattern> patterns) {
        this.patterns = patterns; }
    ArrayList<Pattern> patterns;
}



package unibz.it.PatternChatbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class DesignPatterns{
    @JsonProperty("patterns")
    public ArrayList<Pattern> getPatterns() {
        return this.patterns; }
    public void setPatterns(ArrayList<Pattern> patterns) {
        this.patterns = patterns; }
    public ArrayList<Pattern> patterns;
}



package unibz.it.PatternChatbot.service;

import unibz.it.PatternChatbot.model.DesignPatterns;
import unibz.it.PatternChatbot.model.Pattern;

import java.util.ArrayList;

public interface PatternSearchService {
    public abstract DesignPatterns searchPatterns(String searchTag, String searchTagValue, ArrayList<Pattern> listOfPattern);
    public abstract DesignPatterns excludePatternWithTag(String exclusionTag, ArrayList<Pattern> listOfPattern);
}

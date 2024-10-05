package unibz.it.PatternChatbot.service;

import unibz.it.PatternChatbot.model.DesignPatterns;

import java.util.ArrayList;

public interface NextSearchTagCalculationService {
    public abstract String calculateNextSearchTag(DesignPatterns designPatterns, ArrayList<String> excludedTags);
}

package unibz.it.PatternChatbot.service;

import unibz.it.PatternChatbot.model.Pattern;

import java.util.List;

public interface PatternSimilarityCalculationService {
    public double calculatSimilarityWithLevenshteinDistance(String patternName, String compareToPatternName);
    public double computeWeightedPatternSimilarity(Pattern p1, Pattern p2);
    public Pattern findNearestPatternWeighted(Pattern inputPattern, List<Pattern> patterns);
}

package unibz.it.PatternChatbot.service;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import unibz.it.PatternChatbot.model.Pattern;
import unibz.it.PatternChatbot.model.Tag;

import java.util.List;
import java.util.Map;
@Service
public class PatternSimilarityCalculationServiceImpl implements PatternSimilarityCalculationService{
    // Define weights for each tagName
    private final Map<String, Integer> tagWeights = Map.of(
            "Domain", 3,
            "Category", 2,
            "Benefit", 1
    );
    // Compute normalized string similarity (Levenshtein-based)
    @Override
    public double calculatSimilarityWithLevenshteinDistance(String patternName, String compareToPatternName) {
        LevenshteinDistance distance = new LevenshteinDistance();
        int dist = distance.apply(patternName, compareToPatternName);
        int maxLength = Math.max(patternName.length(), compareToPatternName.length());
        return 1.0 - ((double) dist / maxLength);
    }

    // Compute weighted similarity between two patterns
    @Override
    public double computeWeightedPatternSimilarity(Pattern p1, Pattern p2) {
        double similarity = 0.0;
        int totalWeight = 0;

        for (Tag tag1 : p1.tags) {
            for (Tag tag2 : p2.tags) {
                if (tag1.tagName.equals(tag2.tagName)) {
                    int weight = tagWeights.getOrDefault(tag1.tagName, 1);
                    similarity += weight * calculatSimilarityWithLevenshteinDistance(tag1.tagValue, tag2.tagValue);
                    totalWeight += weight;
                }
            }
        }
        return similarity / totalWeight; // Normalize by total weight
    }

    @Override
    public Pattern findNearestPatternWeighted(Pattern inputPattern, List<Pattern> patterns) {
        Pattern nearest = null;
        double maxSimilarity = -1;

        for (Pattern candidate : patterns) {
            if (!candidate.name.equals(inputPattern.name)) { // Exclude self
                double similarity = computeWeightedPatternSimilarity(inputPattern, candidate);
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    nearest = candidate;
                }
            }
        }
        return nearest;
    }
}

package unibz.it.PatternChatbot.service;

import org.springframework.stereotype.Service;
import unibz.it.PatternChatbot.model.DesignPatterns;
import unibz.it.PatternChatbot.model.Pattern;
import unibz.it.PatternChatbot.model.Tag;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class NextSearchTagCalculationVarianceFilteredServiceImpl implements NextSearchTagCalculationService{

    @Override
    public String calculateNextSearchTag(DesignPatterns designPatterns, ArrayList<String> excludedTags) {
        Map<String, Map<String, Integer>> tagDistribution = new HashMap<>();

        // Step 1: Calculate the distribution of tag values for each tag
        for (Pattern pattern : designPatterns.getPatterns()) {
            for (Tag tag : pattern.tags) {
                String tagName = tag.tagName;
                String tagValue = tag.tagValue;

                if (!excludedTags.contains(tagName)) {
                    tagDistribution.putIfAbsent(tagName, new HashMap<>());
                    tagDistribution.get(tagName).put(tagValue,
                            tagDistribution.get(tagName).getOrDefault(tagValue, 0) + 1);
                }
            }
        }

        // Step 2: Evaluate each tag
        String bestTag = null;
        double bestScore = Double.MAX_VALUE;

        double w1 = 0.3; // Weight for normalized variance
        double w2 = 0.7; // Weight for filtering power
        double penaltyCoefficient = 0.06; // Penalty for too many tag options

        for (Map.Entry<String, Map<String, Integer>> entry : tagDistribution.entrySet()) {
            String tagName = entry.getKey();
            Map<String, Integer> valueCounts = entry.getValue();

            // Calculate normalized variance
            double mean = valueCounts.values().stream().mapToInt(Integer::intValue).average().orElse(0);
            double variance = valueCounts.values().stream()
                    .mapToDouble(count -> Math.pow(count - mean, 2))
                    .average().orElse(0);
            double normalizedVariance = variance / valueCounts.size();

            // Calculate filtering power
            int remainingPatterns = valueCounts.values().stream().mapToInt(Integer::intValue).sum();
            double filteringPower = (double) remainingPatterns / designPatterns.getPatterns().size();

            // Add penalty for number of tag values
            //double penalty = Math.min(valueCounts.size() * penaltyCoefficient, 1.0); // Cap penalty
            double penalty = valueCounts.size() * penaltyCoefficient;

            // Combine normalized variance, penalty, and filtering power into a single score
            double score = w1 * (normalizedVariance + penalty) + w2 * (1 - filteringPower);

            if (score < bestScore) {
                bestScore = score;
                bestTag = tagName;
            }
        }

        if(!bestTag.equalsIgnoreCase("No more Tags available")){
            excludedTags.add(bestTag);
        }
        return bestTag;
    }
}

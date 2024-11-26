package unibz.it.PatternChatbot.utility;

import oshi.util.tuples.Pair;

import java.util.ArrayList;

public interface HelperUtility {
    public ArrayList<Pair<String,Double>> extractKeywords(String searchInput);
}

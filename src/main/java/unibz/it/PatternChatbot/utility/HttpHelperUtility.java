package unibz.it.PatternChatbot.utility;

import oshi.util.tuples.Pair;
import unibz.it.PatternChatbot.model.*;

import java.net.http.HttpResponse;
import java.util.ArrayList;

public interface HttpHelperUtility {
    public void intializeChatbot();
    public HttpResponse<String> getAnotherQuestion();
    public DesignPatterns  getAllPattern();
    public SearchResponseDto searchForPatternWithKeywords(ArrayList<Pair<String,Double>> keywords, int keywordToSearchWith);
    public SearchResponseDto searchForPattern(String tagValue);
    public NearestPatternWeightedResponseDto getNearestPatternWeigthed(Pattern searchPattern,double similarityThreshold);
    public SearchResponseDto excludePattern(String tagValue);
}

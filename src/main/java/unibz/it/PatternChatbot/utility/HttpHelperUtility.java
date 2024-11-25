package unibz.it.PatternChatbot.utility;

import oshi.util.tuples.Pair;
import unibz.it.PatternChatbot.model.DesignPatterns;
import unibz.it.PatternChatbot.model.SearchResponseDto;

import java.net.http.HttpResponse;
import java.util.ArrayList;

public interface HttpHelperUtility {
    public void intializeChatbot();
    public HttpResponse<String> getAnotherQuestion();
    public DesignPatterns  getAllPattern();
    public SearchResponseDto searchForPattern(ArrayList<Pair<String,Double>> keywords, int keywordToSearchWith);
}

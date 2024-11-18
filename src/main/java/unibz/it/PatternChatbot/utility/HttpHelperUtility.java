package unibz.it.PatternChatbot.utility;

import java.net.http.HttpResponse;

public interface HttpHelperUtility {
    public void intializeChatbot();
    public HttpResponse<String> getAnotherQuestion();
    public HttpResponse<String> getAllPattern();
}

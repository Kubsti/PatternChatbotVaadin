package unibz.it.PatternChatbot;

import java.util.ArrayList;

public class SearchResponse {
    private DesingPatterns desingPatterns;
    private Question question;
    private ArrayList<String> excludedTags;

    private String nextSearchTag;

    public SearchResponse(DesingPatterns desingPatterns, Question question,ArrayList<String> excludedTags,String nextSearchTag) {
        this.desingPatterns = desingPatterns;
        this.question = question;
        this.excludedTags = excludedTags;
        this.nextSearchTag = nextSearchTag;
    }

    public DesingPatterns getDesingPatterns() {
        return desingPatterns;
    }

    public void setDesingPatterns(DesingPatterns desingPatterns) {
        this.desingPatterns = desingPatterns;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public ArrayList<String> getExcludedTags() {
        return excludedTags;
    }
}

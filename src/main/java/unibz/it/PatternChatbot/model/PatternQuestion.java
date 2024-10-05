package unibz.it.PatternChatbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
public class PatternQuestion {
    private String tagName;
    private String question;

    public PatternQuestion(@JsonProperty("tagName") String tagName, @JsonProperty("question") String question){
        this.tagName = tagName;
        this.question = question;
    }

    public String getTagName() {
        return this.tagName; }
    public void setTagName(String tagName) {
        this.tagName = tagName; }
    public String getQuestion() {
        return this.question; }
    public void setQuestion(String question) {
        this.question = question; }

}

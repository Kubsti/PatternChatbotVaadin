package unibz.it.PatternChatbot;

import com.fasterxml.jackson.annotation.JsonProperty;
public class Question{
    @JsonProperty("TagName")
    public String getTagName() {
        return this.tagName; }
    public void setTagName(String tagName) {
        this.tagName = tagName; }
    String tagName;
    @JsonProperty("question")
    public String getQuestion() {
        return this.question; }
    public void setQuestion(String question) {
        this.question = question; }
    String question;
}

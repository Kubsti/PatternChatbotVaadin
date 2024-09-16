package unibz.it.PatternChatbot;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tag{
    @JsonProperty("tagName")
    public String getTagName() {
        return this.tagName; }
    public void setTagName(String tagName) {
        this.tagName = tagName; }
    String tagName;
    @JsonProperty("tagValue")
    public String getTagValue() {
        return this.tagValue; }
    public void setTagValue(String tagValue) {
        this.tagValue = tagValue; }
    String tagValue;
    @Override
    public boolean equals(Object obj){
        //Check if it is not null and if the class is Pattern Class
        if(this == obj){
            return true;
        }
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }
        if(this.tagName.equals(((Tag) obj).tagName) && this.tagValue.equals(((Tag) obj).tagValue)){
            return true;
        }
        return false;
    }
}
package unibz.it.PatternChatbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */


public class PatternQuestions{
    @JsonProperty("questions")
    public ArrayList<PatternQuestion> getQuestions() {
        return this.questions; }
    public void setQuestions(ArrayList<PatternQuestion> questions) {
        this.questions = questions; }
    ArrayList<PatternQuestion> questions;

    public boolean containsQuestionForTag(String searchTag){
        for(PatternQuestion currQuestion : this.getQuestions()){
            if(currQuestion.getTagName().equalsIgnoreCase(searchTag)){
                return true;
            }
        }
        return false;
    }
}




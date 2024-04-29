package unibz.it.PatternChatbot;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */


public class PatternQuestions{
    @JsonProperty("questions")
    public ArrayList<Question> getQuestions() {
        return this.questions; }
    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions; }
    ArrayList<Question> questions;

    public boolean containsQuestionForTag(String searchTag){
        for(Question currQuestion : this.getQuestions()){
            if(currQuestion.getTagName().equalsIgnoreCase(searchTag)){
                return true;
            }
        }
        return false;
    }
}




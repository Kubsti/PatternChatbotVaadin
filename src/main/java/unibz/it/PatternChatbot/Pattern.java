package unibz.it.PatternChatbot;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
public class Pattern{

    @JsonProperty("Name")
    public String getName() {
        return this.name; }
    public void setName(String name) {
        this.name = name; }

    String name;
    @JsonProperty("Description")
    public String getDescription() {
        return this.description; }
    public void setDescription(String description) {
        this.description = description; }
    String description;
    @JsonProperty("URL")
    public String getURL() {
        return this.uRL; }
    public void setURL(String uRL) {
        this.uRL = uRL; }

    String uRL;
    @JsonProperty("tags")
    public ArrayList<Tag> getTags() {
        return this.tags; }
    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags; }

    ArrayList<Tag> tags;
    @Override
    public boolean equals(Object obj){
        //Check if it is not null and if the class is Pattern Class
        if(this == obj){
            return true;
        }
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }
        if(this.name.equals(((Pattern) obj).name) && this.uRL.equals(((Pattern) obj).uRL) && this.description.equals(((Pattern) obj).description) && this.tags.equals(((Pattern) obj).tags)){
            return true;
        }
        return false;
    }
}

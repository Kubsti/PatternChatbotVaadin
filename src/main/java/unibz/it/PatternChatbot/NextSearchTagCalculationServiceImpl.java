package unibz.it.PatternChatbot;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
@Service
public class NextSearchTagCalculationServiceImpl implements NextSearchTagCalculationService{
    private class TagInfo{
        private int occurenceOfTag = 0;
        private HashSet<String> listOfPossibleTagValues = new HashSet<String>();

        public int getOccurenceOfTag() {
            return occurenceOfTag;
        }

        public void setOccurenceOfTag(int occurenceOfTag) {
            this.occurenceOfTag = occurenceOfTag;
        }

        public HashSet<String> getListOfPossibleTagValues() {
            return listOfPossibleTagValues;
        }

        public void setListOfPossibleTagValues(HashSet<String> listOfPossibleTagValues) {
            this.listOfPossibleTagValues = listOfPossibleTagValues;
        }
    }
    @Override
    public String calculateNextSearchTag(DesignPatterns designPatterns, ArrayList<String> excludedTags) {

        Hashtable<String,TagInfo> listOfTags = new Hashtable<String,TagInfo>();
        for(Pattern pattern : designPatterns.patterns){
            for(Tag tag : pattern.tags){
                if(!excludedTags.contains(tag.tagName)){
                    if(listOfTags.containsKey(tag.tagName)){
                        TagInfo currTag = listOfTags.get(tag.tagName);
                        currTag.setOccurenceOfTag(currTag.getOccurenceOfTag()+1);
                        currTag.getListOfPossibleTagValues().add(tag.tagValue);
                    }else{
                        TagInfo newInfo = new TagInfo();
                        newInfo.setOccurenceOfTag(newInfo.getOccurenceOfTag()+1);
                        newInfo.getListOfPossibleTagValues().add(tag.tagValue);
                        listOfTags.put(tag.tagName,newInfo);
                    }
                }
            }

        }
        String nextSearchTag = "No more Tags available";
        if(!listOfTags.isEmpty()){
            int maxOcc = 0;
            int maxDiffTagValues = 0;
            for(Map.Entry<String,TagInfo> entry : listOfTags.entrySet()){
                if(entry.getValue().getOccurenceOfTag() > maxOcc && entry.getValue().getListOfPossibleTagValues().size() > maxDiffTagValues){
                    maxOcc = entry.getValue().getOccurenceOfTag();
                    maxDiffTagValues = entry.getValue().getListOfPossibleTagValues().size();
                    nextSearchTag = entry.getKey();
                }
            }

        }
        if(!nextSearchTag.equalsIgnoreCase("No more Tags available")){
            excludedTags.add(nextSearchTag);
        }
        return nextSearchTag;
    }
}



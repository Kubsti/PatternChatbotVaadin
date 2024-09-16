package unibz.it.PatternChatbot;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
@Service
public class PatternSearchServiceImpl implements PatternSearchService{
    @Override
    //TODO implement a comparison with similarity of search Tag and search Tag Value
    public DesignPatterns searchPatterns(String searchTag,String searchTagValue,ArrayList<Pattern> listOfPattern) {
        DesignPatterns designPatterns = new DesignPatterns();
        ArrayList<Pattern> foundPatterns = new ArrayList<Pattern>();
        for(Pattern currPattern : listOfPattern){
            for(Tag currTag : currPattern.getTags()){
                if(currTag.tagName.equalsIgnoreCase(searchTag) && currTag.tagValue.equalsIgnoreCase(searchTagValue)){
                    foundPatterns.add(currPattern);
                }
            }
        }
        designPatterns.setPatterns(foundPatterns);
        return designPatterns;
    }
}

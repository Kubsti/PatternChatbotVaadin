package unibz.it.PatternChatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class PatternChatbotController {
    private DesingPatterns desingPatterns;
    private PatternQuestions patternQuestions;
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    FileReaderService fileReaderService;
    @Autowired
    NextSearchTagCalculationService nextSearchTagCalculationService;
    @Autowired
    NextSearchQuestionCalculationService nextSearchQuestionCalculationService;
    @Autowired
    PatternSearchService patternSearchService;
    @Autowired
    PatternWriterService patternWriterService;

    //Get Data for initialization.
    @GetMapping("/initialization")
    public ResponseEntity<String> init() throws IOException {
        try{
            desingPatterns = fileReaderService.getDesingPatterns("Pattern/pattern.json");
            patternQuestions = fileReaderService.getPatternQuestions("Pattern/questions.json");
            ArrayList<String> excludedTags = new ArrayList<String>();
            String nextSearchTag = nextSearchTagCalculationService.calculateNextSearchTag(desingPatterns, excludedTags);
            Question nextQuestion = nextSearchQuestionCalculationService.calculateNextSearchQuestion(nextSearchTag, patternQuestions);
            SearchResponse currResponse = new SearchResponse(desingPatterns, nextQuestion, excludedTags, nextSearchTag);
            return ResponseEntity.ok().body(objectMapper.writeValueAsString(currResponse));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    @PostMapping("/searchPattern")
    public SearchResponse searchPattern(@RequestBody String currSearchTag, String searchTagValue, DesingPatterns desingPatterns, ArrayList<String> excludedTags) {
        //Caculate next searchtag
        DesingPatterns filteredDesignPattern = patternSearchService.searchPatterns(currSearchTag, searchTagValue, desingPatterns.getPatterns());
        String nextSearchTag = nextSearchTagCalculationService.calculateNextSearchTag(desingPatterns, excludedTags);
        Question nextQuestion = nextSearchQuestionCalculationService.calculateNextSearchQuestion(nextSearchTag, patternQuestions);
        SearchResponse currResponse = new SearchResponse(filteredDesignPattern, nextQuestion, excludedTags, nextSearchTag);
        return currResponse;
    }
    //if user inserts pattern frontend should check if we have new tags,for start disallow multiple question for one tag
    @PostMapping(
            value = "/insertPattern",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<String> insertPattern(@RequestBody Pattern pattern,  PatternQuestions questionForPatternTags) {
        //Try insertion of pattern
        try {
            //Check if the pattern already exists
            if (desingPatterns.getPatterns().isEmpty() == false) {
                for (Pattern currPattern : desingPatterns.getPatterns()) {
                    //TODO implement a better comparison
                    if (currPattern.name.equalsIgnoreCase(pattern.name)) {
                        return new ResponseEntity<>("A pattern with the Name: " + pattern.name + " already exists.", HttpStatus.BAD_REQUEST);
                    }
                }
                //Check if we have a question for the given tags or new question to insert in the questionForPatternTags
                for(Tag currTag : pattern.getTags()){
                    if(questionForPatternTags.getQuestions().isEmpty()){
                        //wrong cannot compare tag with questions
                        if(!patternQuestions.containsQuestionForTag(currTag.tagName)){
                            return new ResponseEntity<>("A question is missing for the Tag :" + currTag.tagName , HttpStatus.BAD_REQUEST);
                        }
                    }else{
                        //wrong cannot compare tag with questions
                        if(!questionForPatternTags.containsQuestionForTag(currTag.tagName) && !patternQuestions.containsQuestionForTag(currTag.tagName)){
                            return new ResponseEntity<>("A question is missing for the Tag :" + currTag.tagName , HttpStatus.BAD_REQUEST);
                        }
                    }
                }
            }
            //If question list is not empty check if we already have a question for a tag
            if(!questionForPatternTags.getQuestions().isEmpty()){
                for(Question currQuestion: questionForPatternTags.getQuestions()){
                    if(patternQuestions.containsQuestionForTag(currQuestion.tagName)){
                        return new ResponseEntity<>("A question for the Tag :" + currQuestion.tagName + " already exists.", HttpStatus.BAD_REQUEST);
                    }
                }
            }
            //Insert pattern
            desingPatterns.getPatterns().add(pattern);
            patternWriterService.writePattern(desingPatterns);
            return ResponseEntity.ok("Pattern inserted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PutMapping("/updatePattern")
    public ResponseEntity<String> updatePattern(@RequestBody Pattern pattern) {
        //Try update of pattern
        //TODO create a generic method which checks if a pattern sent is valid and use it to handle wrong formatted input
        try {
            if (desingPatterns.getPatterns().isEmpty() == false) {
                for (Pattern currPattern : desingPatterns.getPatterns()) {
                    if (currPattern.name.equalsIgnoreCase(pattern.name)) {
                        desingPatterns.getPatterns().remove(currPattern);
                        desingPatterns.getPatterns().add(pattern);
                        patternWriterService.writePattern(desingPatterns);
                        return ResponseEntity.ok("Pattern: " + pattern.name +  " successfully updated");
                    }
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pattern to update found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @DeleteMapping("/deletePattern")
    public ResponseEntity<String> deletePattern(@RequestBody Pattern pattern) {
        //Try deletion of pattern
        try {
            if (desingPatterns.getPatterns().isEmpty() == false) {
                if(desingPatterns.getPatterns().contains(pattern)){
                    if(desingPatterns.getPatterns().remove(pattern)){
                        patternWriterService.writePattern(desingPatterns);
                        return ResponseEntity.ok("Pattern: " + pattern.name +  " successfully deleted");
                    }else{
                        //TODO check if case can occur or if it is always caught by exception
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error when trying to delete " +pattern.name +" from List of pattern");
                    }
                }else{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pattern: " + pattern.name +" not contained in List of pattern.");
                }
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("List of pattern empty so no pattern could be deleted");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

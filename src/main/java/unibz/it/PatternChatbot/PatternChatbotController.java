package unibz.it.PatternChatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vaadin.flow.server.VaadinSession;
import unibz.it.PatternChatbot.model.*;
import unibz.it.PatternChatbot.service.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

@RestController
public class PatternChatbotController {
    private DesignPatterns designPatterns;
    private PatternQuestions patternQuestions;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String currentSearchTag = "Domain";
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
    @Autowired
    QuestionAnswerCalculationServiceImpl questionAnswerCalculationService;
    @Autowired
    PatternSimilarityCalculationServiceImpl patternSimilarityCalculationService;
    //Get Data for initialization.
    @GetMapping("/initialization")
    public ResponseEntity<String> init() throws IOException {
        try{
            designPatterns = fileReaderService.getDesignPatterns("Pattern/pattern.json");
            patternQuestions = fileReaderService.getPatternQuestions("Pattern/questions.json");
            ArrayList<String> excludedTags = new ArrayList<String>();
            //TODO recheck next search Tag calculation, and nextQuestion calculation
            String nextSearchTag = nextSearchTagCalculationService.calculateNextSearchTag(designPatterns, excludedTags);
            PatternQuestion nextQuestion = nextSearchQuestionCalculationService.calculateNextSearchQuestion(nextSearchTag, patternQuestions);
            HashSet<String> possibleAnswers = questionAnswerCalculationService.calculatePossibleAnswers(nextSearchTag,designPatterns);
            SearchResponseDto currResponse = new SearchResponseDto(designPatterns, nextQuestion, excludedTags, nextSearchTag,possibleAnswers);
            return ResponseEntity.ok().body(objectMapper.writeValueAsString(currResponse));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    @PostMapping(path="searchPattern", consumes="application/json", produces="application/json")
    public SearchResponseDto searchPattern(@RequestBody SearchDto searchDto) {
        //Caculate next searchtag
        DesignPatterns filteredDesignPattern = patternSearchService.searchPatterns(searchDto.getCurrSearchTag(), searchDto.getSearchTagValue(), designPatterns.getPatterns());
        //Check if we found something
        if(filteredDesignPattern.getPatterns().isEmpty()){
            return new SearchResponseDto(filteredDesignPattern, new PatternQuestion("",""), searchDto.getExcludedTags(), searchDto.getCurrSearchTag(), new HashSet<String>());
        }
        //TODO rework
        String nextSearchTag = nextSearchTagCalculationService.calculateNextSearchTag(designPatterns, searchDto.getExcludedTags());
        PatternQuestion nextQuestion = nextSearchQuestionCalculationService.calculateNextSearchQuestion(nextSearchTag, patternQuestions);
        HashSet<String> possibleAnswers = questionAnswerCalculationService.calculatePossibleAnswers(nextSearchTag,designPatterns);
        return new SearchResponseDto(filteredDesignPattern, nextQuestion, searchDto.getExcludedTags(), nextSearchTag,possibleAnswers);
    }

    @PostMapping(path="/getNewSearchQuestion", consumes="application/json", produces="application/json")
    public NewQuestionResponseDto getNewSearchQuestion(@RequestBody NewQuestionDto newQuestionDto) {
        String nextSearchTag = nextSearchTagCalculationService.calculateNextSearchTag(designPatterns, newQuestionDto.getExcludedTags());
        PatternQuestion nextQuestion = nextSearchQuestionCalculationService.calculateNextSearchQuestion(nextSearchTag, patternQuestions);
        HashSet<String> possibleAnswers = questionAnswerCalculationService.calculatePossibleAnswers(nextSearchTag,designPatterns);
        return new NewQuestionResponseDto( nextQuestion, newQuestionDto.getExcludedTags(), nextSearchTag, possibleAnswers);
    }
    @GetMapping(path="/getAllPattern")
    public DesignPatterns  getAllPattern(){
        return this.designPatterns;
    }
    @PostMapping(path="/getNearestPatternWeighted", consumes="application/json", produces="application/json")
    public NearestPatternWeightedResponseDto getNearestPatternWeigthed(@RequestBody NearestPatternWeightedDto nearestPatternDto) {
        Pattern nearestfoundPattern = patternSimilarityCalculationService.findNearestPatternWeighted(nearestPatternDto.getSearchPattern(),this.designPatterns.getPatterns());
        ArrayList<Pattern> nearestPattern = new ArrayList<Pattern>();
        if(null != nearestfoundPattern){
            nearestPattern.add(nearestfoundPattern);
        }
        return new NearestPatternWeightedResponseDto(nearestPattern);
    }
    //if user inserts pattern frontend should check if we have new tags,for start disallow multiple question for one tag
    @PostMapping(
            value = "/insertPattern",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<String> insertPattern(@RequestBody Pattern pattern, PatternQuestions questionForPatternTags) {
        //Try insertion of pattern
        try {
            //Check if the pattern already exists
            if (!designPatterns.getPatterns().isEmpty()) {
                for (Pattern currPattern : designPatterns.getPatterns()) {
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
                for(PatternQuestion currQuestion: questionForPatternTags.getQuestions()){
                    if(patternQuestions.containsQuestionForTag(currQuestion.getTagName())){
                        return new ResponseEntity<>("A question for the Tag :" + currQuestion.getTagName() + " already exists.", HttpStatus.BAD_REQUEST);
                    }
                }
            }
            //Insert pattern
            designPatterns.getPatterns().add(pattern);
            patternWriterService.writePattern(designPatterns);
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
            if (!designPatterns.getPatterns().isEmpty()) {
                for (Pattern currPattern : designPatterns.getPatterns()) {
                    if (currPattern.name.equalsIgnoreCase(pattern.name)) {
                        designPatterns.getPatterns().remove(currPattern);
                        designPatterns.getPatterns().add(pattern);
                        patternWriterService.writePattern(designPatterns);
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
            if (!designPatterns.getPatterns().isEmpty()) {
                if(designPatterns.getPatterns().contains(pattern)){
                    if(designPatterns.getPatterns().remove(pattern)){
                        patternWriterService.writePattern(designPatterns);
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
//
//    public void searchForNextPattern(String currSearchTag, String searchTagValue, DesingPatterns desingPatterns, ArrayList<String> excludedTags){
//        DesingPatterns filteredDesignPattern = patternSearchService.searchPatterns(currSearchTag, searchTagValue, desingPatterns.getPatterns());
//        String nextSearchTag = nextSearchTagCalculationService.calculateNextSearchTag(desingPatterns, excludedTags);
//        Question nextQuestion = nextSearchQuestionCalculationService.calculateNextSearchQuestion(nextSearchTag, patternQuestions);
//    }
}

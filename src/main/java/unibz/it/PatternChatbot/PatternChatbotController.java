package unibz.it.PatternChatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unibz.it.PatternChatbot.model.*;
import unibz.it.PatternChatbot.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

@RestController
public class PatternChatbotController {
    private DesignPatterns designPatterns;
    private PatternQuestions patternQuestions;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(PatternChatbotController.class);
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
    @Autowired
    NextSearchTagCalculationVarianceFilteredServiceImpl varianceSearchTagCalculationService;
    //Get Data for initialization.
    @GetMapping("/initialization")
    public ResponseEntity<String> init() throws IOException {
        logger.info("Started initialization of PatternChatbot");
        try{
            designPatterns = fileReaderService.getDesignPatterns("Pattern/pattern.json");
            patternQuestions = fileReaderService.getPatternQuestions("Pattern/questions.json");
            ArrayList<String> excludedTags = new ArrayList<String>();
            String nextSearchTag = varianceSearchTagCalculationService.calculateNextSearchTag(designPatterns, excludedTags);
            //String nextSearchTag = nextSearchTagCalculationService.calculateNextSearchTag(designPatterns, excludedTags);
            PatternQuestion nextQuestion = nextSearchQuestionCalculationService.calculateNextSearchQuestion(nextSearchTag, patternQuestions);
            ArrayList<String> possibleAnswers = questionAnswerCalculationService.calculatePossibleAnswers(nextSearchTag,designPatterns);
            if (this.tagIsNotContainedByAtLeastOnePattern(designPatterns,nextSearchTag)){
                possibleAnswers.add("None (this will exclude all pattern with the current search tag");
            }
            SearchResponseDto currResponse = new SearchResponseDto(designPatterns, nextQuestion, excludedTags, nextSearchTag,possibleAnswers);
            logger.info("Finished initialization of PatternChatbot");
            return ResponseEntity.ok().body(objectMapper.writeValueAsString(currResponse));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    @GetMapping("/initializationWithFixedPatternSearchTag")
    public ResponseEntity<String> initWithFixedPatternSearchTag() throws IOException {
        logger.info("Started initialization of PatternChatbot");
        try{
            designPatterns = fileReaderService.getDesignPatterns("Pattern/pattern.json");
            patternQuestions = fileReaderService.getPatternQuestions("Pattern/questions.json");
            ArrayList<String> excludedTags = new ArrayList<String>();
            excludedTags.add("Extent");
            String nextSearchTag = "Extent";
            PatternQuestion nextQuestion = nextSearchQuestionCalculationService.calculateNextSearchQuestion(nextSearchTag, patternQuestions);
            ArrayList<String> possibleAnswers = questionAnswerCalculationService.calculatePossibleAnswers(nextSearchTag,designPatterns);
            if (this.tagIsNotContainedByAtLeastOnePattern(designPatterns,nextSearchTag)){
                possibleAnswers.add("None (this will exclude all pattern with the current search tag");
            }
            SearchResponseDto currResponse = new SearchResponseDto(designPatterns, nextQuestion, excludedTags, nextSearchTag,possibleAnswers);
            logger.info("Finished initialization of PatternChatbot");
            return ResponseEntity.ok().body(objectMapper.writeValueAsString(currResponse));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    @PostMapping(path="searchPattern", consumes="application/json", produces="application/json")
    public SearchResponseDto searchPattern(@RequestBody SearchDto searchDto) {
        logger.info("Started search of pattern");
        DesignPatterns filteredDesignPattern = patternSearchService.searchPatterns(searchDto.getCurrSearchTag(), searchDto.getSearchTagValue(), searchDto.getDesignPatterns().getPatterns());
        logger.info("Finished search of pattern");
        //Check if we found something
        if(filteredDesignPattern.getPatterns().isEmpty()){
            StringBuilder errorMessage = new StringBuilder("Search of pattern did not return any result. Search Tag Is:" + searchDto.getCurrSearchTag() +" search Tag value is: " + searchDto.getSearchTagValue());
            errorMessage.append("\nPattern received:");
            searchDto.getDesignPatterns().getPatterns().forEach(pattern -> {errorMessage.append("\"").append(pattern.name).append("\",");});
            logger.error(errorMessage.toString());
            return new SearchResponseDto(filteredDesignPattern, new PatternQuestion("",""), searchDto.getExcludedTags(), searchDto.getCurrSearchTag(), new ArrayList<String>());
        }
        //String nextSearchTag = nextSearchTagCalculationService.calculateNextSearchTag(filteredDesignPattern, searchDto.getExcludedTags());

        if(!searchDto.getExcludedTags().contains("Category") || !searchDto.getExcludedTags().contains("Purpose")){
            if(!searchDto.getExcludedTags().contains("Category")){
                searchDto.getExcludedTags().add("Category");
                PatternQuestion nextQuestion = nextSearchQuestionCalculationService.calculateNextSearchQuestion("Category", patternQuestions);
                ArrayList<String> possibleAnswers = questionAnswerCalculationService.calculatePossibleAnswers("Category",filteredDesignPattern);
                if (this.tagIsNotContainedByAtLeastOnePattern(filteredDesignPattern,"Category")){
                    possibleAnswers.add("None (this will exclude all pattern with the current search tag");
                }
                return new SearchResponseDto(filteredDesignPattern, nextQuestion, searchDto.getExcludedTags(), "Category",possibleAnswers);
            }else{
                searchDto.getExcludedTags().add("Purpose");
                PatternQuestion nextQuestion = nextSearchQuestionCalculationService.calculateNextSearchQuestion("Purpose", patternQuestions);
                ArrayList<String> possibleAnswers = questionAnswerCalculationService.calculatePossibleAnswers("Purpose",filteredDesignPattern);
                if (this.tagIsNotContainedByAtLeastOnePattern(filteredDesignPattern,"Purpose")){
                    possibleAnswers.add("None (this will exclude all pattern with the current search tag");
                }
                return new SearchResponseDto(filteredDesignPattern, nextQuestion, searchDto.getExcludedTags(), "Purpose",possibleAnswers);
            }
        }
        if(filteredDesignPattern.getPatterns().size() == 1){
            return new SearchResponseDto(filteredDesignPattern, new PatternQuestion("",""), searchDto.getExcludedTags(), "",new ArrayList<String>());
        }
        String nextSearchTag = varianceSearchTagCalculationService.calculateNextSearchTag(filteredDesignPattern, searchDto.getExcludedTags());
        PatternQuestion nextQuestion = nextSearchQuestionCalculationService.calculateNextSearchQuestion(nextSearchTag, patternQuestions);
        ArrayList<String> possibleAnswers = questionAnswerCalculationService.calculatePossibleAnswers(nextSearchTag,filteredDesignPattern);
        if (this.tagIsNotContainedByAtLeastOnePattern(filteredDesignPattern,"Category")){
            possibleAnswers.add("None (this will exclude all pattern with the current search tag");
        }
        return new SearchResponseDto(filteredDesignPattern, nextQuestion, searchDto.getExcludedTags(), nextSearchTag,possibleAnswers);
    }

    @PostMapping(path="excludePattern", consumes="application/json", produces="application/json")
    public SearchResponseDto excludePattern(@RequestBody SearchDto searchDto) {
        logger.info("Started to exclude pattern with tag: " + searchDto.getCurrSearchTag());
        DesignPatterns filteredDesignPattern = patternSearchService.excludePatternWithTag(searchDto.getCurrSearchTag(), searchDto.getDesignPatterns().getPatterns());
        logger.info("Finished to exclude pattern with tag: \" + searchDto.getCurrSearchTag()");
        //Check if we found something
        if(filteredDesignPattern.getPatterns().isEmpty()){
            StringBuilder errorMessage = new StringBuilder("Exclusion of pattern with input Tag lead to an empty result. Search Tag Is:" + searchDto.getCurrSearchTag() +" search Tag value is: " + searchDto.getSearchTagValue());
            errorMessage.append("\nPattern received:");
            searchDto.getDesignPatterns().getPatterns().forEach(pattern -> {errorMessage.append("\"").append(pattern.name).append("\",");});
            logger.error(errorMessage.toString());
            return new SearchResponseDto(filteredDesignPattern, new PatternQuestion("",""), searchDto.getExcludedTags(), searchDto.getCurrSearchTag(), new ArrayList<String>());
        }
        //String nextSearchTag = nextSearchTagCalculationService.calculateNextSearchTag(filteredDesignPattern, searchDto.getExcludedTags());
        String nextSearchTag = varianceSearchTagCalculationService.calculateNextSearchTag(filteredDesignPattern, searchDto.getExcludedTags());
        PatternQuestion nextQuestion = nextSearchQuestionCalculationService.calculateNextSearchQuestion(nextSearchTag, patternQuestions);
        ArrayList<String> possibleAnswers = questionAnswerCalculationService.calculatePossibleAnswers(nextSearchTag,filteredDesignPattern);
        if (this.tagIsNotContainedByAtLeastOnePattern(filteredDesignPattern,nextSearchTag)){
            possibleAnswers.add("None (this will exclude all pattern with the current search tag");
        }
        return new SearchResponseDto(filteredDesignPattern, nextQuestion, searchDto.getExcludedTags(), nextSearchTag,possibleAnswers);
    }

    @PostMapping(path="/getNewSearchQuestion", consumes="application/json", produces="application/json")
    public NewQuestionResponseDto getNewSearchQuestion(@RequestBody NewQuestionDto newQuestionDto) {
        logger.info("Started get new Search Question");
        String nextSearchTag = nextSearchTagCalculationService.calculateNextSearchTag(newQuestionDto.getDesignPatterns(), newQuestionDto.getExcludedTags());
        PatternQuestion nextQuestion = nextSearchQuestionCalculationService.calculateNextSearchQuestion(nextSearchTag, patternQuestions);
        ArrayList<String> possibleAnswers = questionAnswerCalculationService.calculatePossibleAnswers(nextSearchTag,newQuestionDto.getDesignPatterns());
        if (!possibleAnswers.isEmpty() && this.tagIsNotContainedByAtLeastOnePattern(newQuestionDto.getDesignPatterns(),nextSearchTag)){
            possibleAnswers.add("None (this will exclude all pattern with the current search tag");
        }
        logger.info("Finished get new Search Question");
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

    private boolean tagIsNotContainedByAtLeastOnePattern(DesignPatterns patterns, String nextSearchTag) {
        return patterns.getPatterns().parallelStream() // Process patterns in parallel
                .anyMatch(pattern ->
                        pattern.getTags().stream() // Check tags in each pattern
                                .noneMatch(tag -> tag.tagName.equalsIgnoreCase(nextSearchTag)) // Ensure no tag matches nextSearchTag
                );
    }
}

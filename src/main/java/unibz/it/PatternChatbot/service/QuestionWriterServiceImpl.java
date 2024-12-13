package unibz.it.PatternChatbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import org.springframework.stereotype.Service;
import unibz.it.PatternChatbot.model.PatternQuestions;

import java.io.File;
import java.io.FileWriter;

@Service
public class QuestionWriterServiceImpl implements QuestionWriterService{
    @Override
    public boolean writeQuestions(PatternQuestions listOfQuestions) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File("Pattern/questions.json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, listOfQuestions);
            System.out.println("JSON written to file: " + file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

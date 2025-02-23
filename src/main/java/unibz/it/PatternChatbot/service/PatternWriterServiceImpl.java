package unibz.it.PatternChatbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import org.springframework.stereotype.Service;
import unibz.it.PatternChatbot.model.DesignPatterns;

import java.io.File;
import java.io.FileWriter;

@Service
public class PatternWriterServiceImpl implements PatternWriterService {

    @Override
    public boolean writePattern(DesignPatterns listOfPattern) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File("Pattern/pattern.json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, listOfPattern);
            System.out.println("JSON written to file: " + file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

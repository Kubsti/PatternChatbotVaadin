package unibz.it.PatternChatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
@Service
public class PatternWriterServiceImpl implements PatternWriterService{

    @Override
    public boolean writePattern(DesingPatterns listOfPattern) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File("Pattern/pattern.json"), listOfPattern);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

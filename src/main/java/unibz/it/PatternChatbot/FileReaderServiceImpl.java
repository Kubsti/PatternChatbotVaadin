package unibz.it.PatternChatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class FileReaderServiceImpl implements FileReaderService{
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public DesingPatterns getDesingPatterns(String filePath) throws IOException {
        DesingPatterns currPatterns = null;
            File file = new File(filePath);
            assert file !=null;
            if(file.isDirectory()){
               throw new IOException("Path of Patterns cannot be a directory");
            } else if (file.isHidden()) {
                throw new IOException("The file in the given path cannot be hidden");
            } else if (!file.exists()) {
                throw new IOException("The file in the given path does not exist");
            } else if (!file.canRead()) {
                throw new IOException("The file in the give path cannot be read");
            } else if (!file.getName().toLowerCase().endsWith(".json")) {
                throw new IOException("The file in the given path must be a json");
            }else{
                currPatterns = objectMapper.readValue(Files.readString(file.toPath()), DesingPatterns.class);
            }
        return currPatterns;
    }

    @Override
    public PatternQuestions getPatternQuestions(String filePath) throws IOException {
        PatternQuestions currQuestions = null;
        File file = new File(filePath);
        assert file !=null;
        if(file.isDirectory()){
            throw new IOException("Path of Patterns cannot be a directory");
        } else if (file.isHidden()) {
            throw new IOException("The file in the given path cannot be hidden");
        } else if (!file.exists()) {
            throw new IOException("The file in the given path does not exist");
        } else if (!file.canRead()) {
            throw new IOException("The file in the give path cannot be read");
        } else if (!file.getName().toLowerCase().endsWith(".json")) {
            throw new IOException("The file in the given path must be a json");
        }else{
            currQuestions = objectMapper.readValue(Files.readString(file.toPath()), PatternQuestions.class);
        }
        return currQuestions;
    }
}

package unibz.it.PatternChatbot;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class KeywordExtractorServiceImpl implements KeywordExtractorService{

    @Override
    public List<String> extractKeywords(String inputText) {
        List<String> keywords = new ArrayList<>();
        // Load POS model
        try {
            InputStream modelIn = new FileInputStream("en-pos-maxent.bin");
            POSModel posModel = new POSModel(modelIn);
            POSTaggerME posTagger = new POSTaggerME(posModel);
            // Tokenize the chat message
            SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
            String[] tokens = tokenizer.tokenize(inputText);
            // Perform part-of-speech tagging
            String[] tags = posTagger.tag(tokens);
            for (int i = 0; i < tokens.length; i++) {
                if (tags[i].startsWith("NN") || tags[i].startsWith("JJ")) {
                    keywords.add(tokens[i]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return keywords;
    }
}

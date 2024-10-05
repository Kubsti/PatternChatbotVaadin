package unibz.it.PatternChatbot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unibz.it.PatternChatbot.service.*;

@Configuration
public class PatternChatbotApiApplicationConfig {
    @Bean
    public NextSearchTagCalculationService nextSearchTagCalculationService(){
        return new NextSearchTagCalculationServiceImpl();
    }
    @Bean
    public NextSearchQuestionCalculationService nextSearchQuestionCalculationService(){
        return new NextSearchQuestionCalculationServiceImpl();
    }
    @Bean
    public PatternSearchService patternSearchService(){
        return new PatternSearchServiceImpl();
    }
    @Bean
    public PatternWriterService patternWriterService() {
        return new PatternWriterServiceImpl();
    }
    @Bean
    public FileReaderService fileReaderService(){
        return  new FileReaderServiceImpl();
    }
}

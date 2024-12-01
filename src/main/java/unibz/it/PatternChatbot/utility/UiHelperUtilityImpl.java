package unibz.it.PatternChatbot.utility;

import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unibz.it.PatternChatbot.PatternChatbotController;
import unibz.it.PatternChatbot.model.PatternQuestion;
import unibz.it.PatternChatbot.ui.ChatView;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.*;

public class UiHelperUtilityImpl implements UiHelperUtility {
    private final ChatView currentChatView;
    private static final Logger logger = LoggerFactory.getLogger(UiHelperUtilityImpl.class);
    public UiHelperUtilityImpl(ChatView currentChatView) {
        this.currentChatView = currentChatView;
    }

    @Override
    public void createPatteraChatMessage(String chatMessage) {
        List<MessageListItem> messages = new ArrayList<MessageListItem>();
        messages.addAll(currentChatView.chat.getItems());
        messages.add(new MessageListItem(
                chatMessage,
                Instant.now(), "Pattera"));
        currentChatView.chat.setItems(messages);
    }

    @Override
    public void createPatteraSearchAnswer(String chatMessage, ArrayList<String> options, HashSet<String> possibleAnswers) {
        List<MessageListItem> messages = new ArrayList<MessageListItem>();
        messages.addAll(currentChatView.chat.getItems());

        StringBuilder finalMessage = new StringBuilder();
        finalMessage.append(chatMessage);
        finalMessage.append("Options:");
        for(String option : options) {
            finalMessage.append("\n").append(option);
        }
        PatternQuestion question = (PatternQuestion) VaadinSession.getCurrent().getAttribute("nextQuestion");
        finalMessage.append("\n").append(question.getQuestion());
        finalMessage.append("\n").append("Possible answers are:\n");
        for(String possibleAnswer : possibleAnswers){
            finalMessage.append("\"").append(possibleAnswer).append("\",");
        }
        messages.add(new MessageListItem(
                finalMessage.toString(),
                Instant.now(), "Pattera"));
        currentChatView.chat.setItems(messages);
    }

    @Override
    public void createChatMessage(String chatMessage) {
        List<MessageListItem> messages = new ArrayList<MessageListItem>();
        messages.addAll(currentChatView.chat.getItems());
        messages.add(new MessageListItem(
                chatMessage,
                Instant.now(), ""));
        currentChatView.chat.setItems(messages);
    }

    @Override
    public void updatePdfViewer(String url) {
        logger.info("Started to update PdfViewer");
        ChromeOptions options = new ChromeOptions();
        https://stackoverflow.com/questions/79004567/selenium-headless-broke-after-chrome-update
        //the new headless mode has of this comment 28.09.24 a bug why it does not work. So there are two options to resolve this. Option 1 is below.
        options.addArguments("--headless=new");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-position=-2400,-2400");
        options.addArguments("--no-sandbox");
        //Second option
        //options.addArguments("--headless=old");
        ChromeDriver driver = new ChromeDriver(options);

        driver.get(url);

        Map<String, Object> params = new HashMap<>();
        params.put("paperWidth", 8.27);  // A4 size width in inches
        params.put("paperHeight", 11.69); // A4 size height in inches
        params.put("printBackground", true);
        logger.info("Started to get Url as PDF");
        String pdfBase64 = (String) driver.executeCdpCommand("Page.printToPDF", params).get("data");
        logger.info("Finished get Url as PDF");
        // Decode the base64 string to get the PDF as bytes
        byte[] pdfBytes = Base64.getDecoder().decode(pdfBase64);
        StreamResource pdfResource = new StreamResource("example.pdf",
                () -> new ByteArrayInputStream(pdfBytes));

        // Specify the content type
        pdfResource.setContentType("application/pdf");

        driver.quit();
        this.currentChatView.pdfViewer.setSrc(pdfResource);
        logger.info("Finished to update PdfViewer");
    }
}

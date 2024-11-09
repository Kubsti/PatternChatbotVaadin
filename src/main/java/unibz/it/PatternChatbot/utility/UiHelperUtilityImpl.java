package unibz.it.PatternChatbot.utility;

import com.vaadin.flow.component.messages.MessageListItem;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import unibz.it.PatternChatbot.ui.ChatView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class UiHelperUtilityImpl implements UiHelperUtility {
    private final ChatView currentChatView;

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
    public void createChatMessage(String chatMessage) {
        List<MessageListItem> messages = new ArrayList<MessageListItem>();
        messages.addAll(currentChatView.chat.getItems());
        messages.add(new MessageListItem(
                chatMessage,
                Instant.now(), ""));
        currentChatView.chat.setItems(messages);
    }

    @Override
    public void updateIFrame(String url) {
        ChromeOptions options = new ChromeOptions();
        https://stackoverflow.com/questions/79004567/selenium-headless-broke-after-chrome-update
        //the new headless mode has of this comment 28.09.24 a bug why it does not work. So there are two options to resolve this. Option 1 is below.
        options.addArguments("--headless=new");
        options.addArguments("--window-position=-2400,-2400");
        //Second option
        //options.addArguments("--headless=old");
        ChromeDriver driver = new ChromeDriver(options);

        driver.get(url);

        Map<String, Object> params = new HashMap<>();
        params.put("paperWidth", 8.27);  // A4 size width in inches
        params.put("paperHeight", 11.69); // A4 size height in inches
        params.put("printBackground", true);

        String pdfBase64 = (String) driver.executeCdpCommand("Page.printToPDF", params).get("data");

        // Decode the base64 string to get the PDF as bytes
        byte[] pdfBytes = Base64.getDecoder().decode(pdfBase64);

        // Write the byte array to a file
        try (FileOutputStream fos = new FileOutputStream("src/main/webapp/assets/output.pdf",false)) {
            fos.write(pdfBytes);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        driver.quit();

//       this.anchor =  new Anchor(new StreamResource("output.pdf",
//                () ->  VaadinServlet.getCurrent().getServletContext()
//                        .getResourceAsStream("assets/output.pdf")),
//                "A document");
//        anchor.getElement().setAttribute("router-ignore", true);
        this.currentChatView.webpageIFrame.setSrc("assets/output.pdf");
        this.currentChatView.webpageIFrame.reload();
    }

    @Override
    public void showError(String error) {

    }

}

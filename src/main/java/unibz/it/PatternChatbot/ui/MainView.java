package unibz.it.PatternChatbot.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import unibz.it.PatternChatbot.model.SearchResponseDto;
import unibz.it.PatternChatbot.utility.ChatHelperUtilityImpl;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Route("view")
public class MainView extends HorizontalLayout {
private IFrame webpageIFrame;

    public MainView() {
        this.setSizeFull();

        //TODO find an alternative to the IFrame or make iframe usable;
        webpageIFrame = new IFrame("https://www.wikipedia.org/");
        webpageIFrame.setId("patternIFrame");
        webpageIFrame.setHeightFull();
        webpageIFrame.setWidth("70%");
        webpageIFrame.setSandbox(IFrame.SandboxType.ALLOW_SCRIPTS, IFrame.SandboxType.ALLOW_SAME_ORIGIN);
        add(webpageIFrame);
        try{
            //TODO change to correct url or refactor to a better solution
            URL url = URI.create("http://localhost:8080/initialization").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }else{
                ObjectMapper mapper = new ObjectMapper();
                try (InputStream inputStream = conn.getInputStream()) {
                    SearchResponseDto response = mapper.readValue(inputStream, SearchResponseDto.class);
                    VaadinSession.getCurrent().setAttribute("excludedTags",response.getExcludedTags());
                    VaadinSession.getCurrent().setAttribute("nextSearchTag",response.getNextSearchTag());
                    VaadinSession.getCurrent().setAttribute("nextQuestion", response.getPatternQuestion());
                    VaadinSession.getCurrent().setAttribute("designPattern",response.getDesignPatterns());
                }catch(Exception e){
                    //TODO handle exception
                    System.out.println(e.getMessage());
                }
            }
        }catch (Exception e){
            //TODO handle exception
            System.out.println(e.getMessage());
        }
//        MessageListItem message1 = new MessageListItem(
//                "Please tell me which pattern you would like to search?",
//                Instant.now(), "Patty");
        ChatView chatView = new ChatView();
        chatView.setWebpageIFrame(webpageIFrame);
        MessageList messageList = chatView.chat;
        chatView.setHeight(this.getMaxHeight());
        chatView.setWidth("30%");
        add(chatView);
        ChatHelperUtilityImpl chatHelper = new ChatHelperUtilityImpl(chatView);
        chatHelper.updateIFrame("https://learn.microsoft.com/en-us/azure/architecture/patterns/choreography");
        //this.setPage("https://learn.microsoft.com/en-us/azure/architecture/patterns/choreography");

    }

//    private void setPage(String url){
//        ChromeOptions options = new ChromeOptions();
//        https://stackoverflow.com/questions/79004567/selenium-headless-broke-after-chrome-update
//        //the new headless mode has of this comment 28.09.24 a bug why it does not work. So there are two options to resolve this. Option 1 is below.
//        options.addArguments("--headless=new");
//        options.addArguments("--window-position=-2400,-2400");
//        //Second option
//        //options.addArguments("--headless=old");
//        ChromeDriver driver = new ChromeDriver(options);
//
//        driver.get(url);
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("paperWidth", 8.27);  // A4 size width in inches
//        params.put("paperHeight", 11.69); // A4 size height in inches
//        params.put("printBackground", true);
//
//        String pdfBase64 = (String) driver.executeCdpCommand("Page.printToPDF", params).get("data");
//
//        // Decode the base64 string to get the PDF as bytes
//        byte[] pdfBytes = Base64.getDecoder().decode(pdfBase64);
//
//        // Write the byte array to a file
//        try (FileOutputStream fos = new FileOutputStream("src/main/webapp/assets/output.pdf")) {
//            fos.write(pdfBytes);
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        driver.quit();
//
////       this.anchor =  new Anchor(new StreamResource("output.pdf",
////                () ->  VaadinServlet.getCurrent().getServletContext()
////                        .getResourceAsStream("assets/output.pdf")),
////                "A document");
////        anchor.getElement().setAttribute("router-ignore", true);
//        this.webpageIFrame.setSrc("assets/output.pdf");
//    }

    private String fetchExternalPage(String pageUrl) throws IOException{
        URL url = new URL(pageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        connection.disconnect();

        return content.toString();
    }
}
package unibz.it.PatternChatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinSession;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriver;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Route("view")
public class MainView extends HorizontalLayout {
private IFrame webpageIFrame;
private Anchor anchor = new Anchor();

    public MainView() {
        this.setSizeFull();
        //layout.setAlignItems(FlexComponent.Alignment.CENTER);
        //VerticalLayout chatLayout  = new VerticalLayout();
        //TODO find an alternative to the IFrame or make iframe usable;
        //webpageIFrame = new IFrame("https://learn.microsoft.com/en-us/azure/architecture/patterns/choreography");
        webpageIFrame = new IFrame("https://www.wikipedia.org/");
        webpageIFrame.setId("patternIFrame");
        webpageIFrame.setHeightFull();
        webpageIFrame.setWidth("70%");
        webpageIFrame.setSandbox(IFrame.SandboxType.ALLOW_SCRIPTS, IFrame.SandboxType.ALLOW_SAME_ORIGIN);
        add(webpageIFrame);
//        anchor.setHeightFull();
//        anchor.setWidth("70%");
//        add(anchor);
        //this.setPage("https://learn.microsoft.com/en-us/azure/architecture/patterns/choreography");
        this.setPage("https://www.wiktionary.org/");
//        div.setHeightFull();
//        div.setMaxHeight(this.getMaxHeight());
//        div.setWidth("70%");
//        div.setId("patternIFrame");
        try {

//            String externalPageContent = fetchExternalPage("https://learn.microsoft.com/en-us/azure/architecture/patterns/choreography");
//            Html content = new Html("<div>" +externalPageContent + "<div>");
//            Html content = new Html("<div>" +"<embed\n" +
//                    "  type=\"video/quicktime\"\n" +
//                    "  src=\"movie.mov\"\n" +
//                    "  width=\"640\"\n" +
//                    "  height=\"480\"\n" +
//                    "  title=\"Title of my video\" />")
//            Html content = new Html("<object data=" +"\""+"https://learn.microsoft.com/en-us/azure/architecture/patterns/choreography"+"\""+ "type=\"text/html\">");
//            div.add(content);
//            add(div);
        }catch(Exception e){
            //TODO handle exception
        }

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
                    VaadinSession.getCurrent().setAttribute("state","searchstate");
                }catch(Exception e){
                    //TODO handle exception
                    System.out.println(e.getMessage());
                }
            }
        }catch (Exception e){
            //TODO handle exception
            System.out.println(e.getMessage());
        }
        MessageListItem message1 = new MessageListItem(
                "Please tell me which pattern you would like to search?",
                Instant.now(), "Patty");
        ChatView chatView = new ChatView();
        chatView.setWebpageIFrame(webpageIFrame);
        MessageList messageList = chatView.getMessageList();
        chatView.setHeight(this.getMaxHeight());
        chatView.setWidth("30%");
        add(chatView);
        //TODO initialize data

        //IFrame testIframe = new IFrame("https://www.w3schools.com/html/html_iframe.asp");
        // Use TextField for standard text input
        //TextField textField = new TextField("Your name");

        // Use custom CSS classes to apply styling. This is defined in shared-styles.css.
        //addClassName("centered-content");

    }

    private void setPage(String url){
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
        try (FileOutputStream fos = new FileOutputStream("src/main/webapp/assets/output.pdf")) {
            fos.write(pdfBytes);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        driver.quit();
//        StreamResource resource = new StreamResource("output.pdf",
//                () -> new ByteArrayInputStream(pdfBytes));

       this.anchor =  new Anchor(new StreamResource("output.pdf",
                () ->  VaadinServlet.getCurrent().getServletContext()
                        .getResourceAsStream("assets/output.pdf")),
                "A document");
        anchor.getElement().setAttribute("router-ignore", true);
        this.webpageIFrame.setSrc("assets/output.pdf");
    }

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

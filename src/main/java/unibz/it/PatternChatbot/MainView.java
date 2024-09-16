package unibz.it.PatternChatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.Instant;


@Route("view")
public class MainView extends HorizontalLayout {
private IFrame webpageIFrame;

    public MainView() {
        this.setSizeFull();
        //layout.setAlignItems(FlexComponent.Alignment.CENTER);
        //VerticalLayout chatLayout  = new VerticalLayout();
        //TODO find an alternative to the IFrame or make iframe usable;
        webpageIFrame = new IFrame("https://de.wikipedia.org/wiki/Wikipedia:Hauptseite");
        webpageIFrame.setId("patternIFrame");
        webpageIFrame.setHeightFull();
        webpageIFrame.setWidth("70%");
        //WebpageIFrame.setWidth(String.valueOf(Float.parseFloat(test)/3));
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
}

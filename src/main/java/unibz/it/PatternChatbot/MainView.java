package unibz.it.PatternChatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    public MainView() {
        this.setSizeFull();
        //layout.setAlignItems(FlexComponent.Alignment.CENTER);
        //VerticalLayout chatLayout  = new VerticalLayout();
        IFrame WebpageIFrame = new IFrame("https://de.wikipedia.org/wiki/Wikipedia:Hauptseite");
        WebpageIFrame.setHeightFull();
        WebpageIFrame.setWidth("70%");
        //WebpageIFrame.setWidth(String.valueOf(Float.parseFloat(test)/3));
        add(WebpageIFrame);
        try{
            URL url = URI.create("https:http://localhost:8080/initialization").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }else{
                ObjectMapper mapper = new ObjectMapper();
                try (InputStream inputStream = conn.getInputStream()) {
                    SearchResponse response = mapper.readValue(inputStream, SearchResponse.class);
                    VaadinSession.getCurrent().setAttribute("excludedTags",response.getExcludedTags());
                    VaadinSession.getCurrent().setAttribute("nextSearchTag",response.getNextSearchTag());
                    VaadinSession.getCurrent().setAttribute("nextQuestion", response.getQuestion());
                    VaadinSession.getCurrent().setAttribute("designPattern",response.getDesingPatterns());
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

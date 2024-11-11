package unibz.it.PatternChatbot.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import unibz.it.PatternChatbot.model.SearchResponseDto;
import unibz.it.PatternChatbot.utility.HttpHelperUtilityImpl;
import unibz.it.PatternChatbot.utility.UiHelperUtilityImpl;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@Route("view")
public class MainView extends HorizontalLayout {
private IFrame webpageIFrame;
    private HttpHelperUtilityImpl httpHelper = new HttpHelperUtilityImpl();
    public MainView() {
        this.setSizeFull();

        //TODO find an alternative to the IFrame or make iframe usable;
        webpageIFrame = new IFrame("https://www.wikipedia.org/");
        webpageIFrame.setId("patternIFrame");
        webpageIFrame.setHeightFull();
        webpageIFrame.setWidth("70%");
        webpageIFrame.setSandbox(IFrame.SandboxType.ALLOW_SCRIPTS, IFrame.SandboxType.ALLOW_SAME_ORIGIN);
        add(webpageIFrame);
        httpHelper.intializeChatbot();
//        try{
//            //TODO change to correct url or refactor to a better solution
//            URL url = URI.create("http://localhost:8080/initialization").toURL();
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.connect();
//
//            int responseCode = conn.getResponseCode();
//
//            if (responseCode != 200) {
//                ErrorDialog.showError("Error occurred when trying to contact backend");
//                throw new RuntimeException("HttpResponseCode: " + responseCode);
//            }else{
//                ObjectMapper mapper = new ObjectMapper();
//                try (InputStream inputStream = conn.getInputStream()) {
//                    SearchResponseDto response = mapper.readValue(inputStream, SearchResponseDto.class);
//                    VaadinSession.getCurrent().setAttribute("excludedTags",response.getExcludedTags());
//                    VaadinSession.getCurrent().setAttribute("nextSearchTag",response.getNextSearchTag());
//                    VaadinSession.getCurrent().setAttribute("nextQuestion", response.getPatternQuestion());
//                    VaadinSession.getCurrent().setAttribute("designPattern",response.getDesignPatterns());
//                }catch(Exception e){
//                    ErrorDialog.showError("Error occurred when trying to read response from backend ");
//                    System.out.println(e.getMessage());
//                }
//            }
//        }catch (Exception e){
//            ErrorDialog.showError("Error could not initialize chatbot.");
//            System.out.println(e.getMessage());
//        }

        ChatView chatView = new ChatView();
        chatView.webpageIFrame = webpageIFrame;
        MessageList messageList = chatView.chat;
        chatView.setHeight(this.getMaxHeight());
        chatView.setWidth("30%");
        add(chatView);
        UiHelperUtilityImpl chatHelper = new UiHelperUtilityImpl(chatView);
        chatHelper.updateIFrame("https://learn.microsoft.com/en-us/azure/architecture/patterns/choreography");

    }

}

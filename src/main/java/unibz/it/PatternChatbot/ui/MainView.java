package unibz.it.PatternChatbot.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
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
//private IFrame webpageIFrame;
private PdfViewer pdfViewer;
    private HttpHelperUtilityImpl httpHelper = new HttpHelperUtilityImpl();
    public MainView() {
        this.setSizeFull();

        //TODO find an alternative to the IFrame or make iframe usable;
//        webpageIFrame = new IFrame("https://www.wikipedia.org/");
//        webpageIFrame.setId("patternIFrame");
//        webpageIFrame.setHeightFull();
//        webpageIFrame.setWidth("70%");
//        webpageIFrame.setSandbox(IFrame.SandboxType.ALLOW_SCRIPTS, IFrame.SandboxType.ALLOW_SAME_ORIGIN);
        pdfViewer= new PdfViewer();
        pdfViewer.setHeightFull();
        pdfViewer.setWidth("70%");
        //add(webpageIFrame);
        add(pdfViewer);
        httpHelper.intializeChatbot();

        ChatView chatView = new ChatView();
        //chatView.webpageIFrame = webpageIFrame;
        chatView.pdfViewer = pdfViewer;
        try (InputStream pdf = new FileInputStream("Pattern/PatternChatbotInfo.pdf")) {
            byte[] pdfBytes = pdf.readAllBytes();
            StreamResource resource = new StreamResource("PatternChatbotInfo.pdf", () -> new ByteArrayInputStream(pdfBytes));
            resource.setContentType("application/pdf");
            pdfViewer.setSrc(resource);
        }catch(Exception e){
            e.printStackTrace();
            ErrorDialog.showError("Error in the loading PatternChatbotInfo pdf");
        }



        MessageList messageList = chatView.chat;
        chatView.setHeight(this.getMaxHeight());
        chatView.setWidth("30%");
        add(chatView);
        UiHelperUtilityImpl chatHelper = new UiHelperUtilityImpl(chatView);
        //chatHelper.updateIFrame("https://learn.microsoft.com/en-us/azure/architecture/patterns/choreography");
        //chatHelper.updatePdfViewer("https://learn.microsoft.com/en-us/azure/architecture/patterns/choreography");
    }

}

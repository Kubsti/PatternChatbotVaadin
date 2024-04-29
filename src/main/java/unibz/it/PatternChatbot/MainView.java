package unibz.it.PatternChatbot;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

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

        MessageListItem message1 = new MessageListItem(
                "Please tell me which pattern you would like to search?",
                Instant.now(), "Patty");
        ChatView chatView = new ChatView();
        MessageList messageList = chatView.getMessageList();
        chatView.setHeight(this.getMaxHeight());
        chatView.setWidth("30%");
        add(chatView);

        //IFrame testIframe = new IFrame("https://www.w3schools.com/html/html_iframe.asp");
        // Use TextField for standard text input
        //TextField textField = new TextField("Your name");

        // Use custom CSS classes to apply styling. This is defined in shared-styles.css.
        //addClassName("centered-content");

    }
}

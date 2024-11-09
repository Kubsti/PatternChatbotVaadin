package unibz.it.PatternChatbot.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ErrorDialog extends Dialog {

    public ErrorDialog(String errorMessage) {
        // Set up the layout
        VerticalLayout layout = new VerticalLayout();

        // Error icon and message
        Span messageLabel = new Span(errorMessage);
        messageLabel.getStyle().set("color", "red");

        // Close button
        Button closeButton = new Button("Close", VaadinIcon.CLOSE.create());
        closeButton.addClickListener(event -> this.close());

        layout.add(messageLabel, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        // Add the layout to the dialog
        add(layout);
    }

    public static void showError(String message) {
        ErrorDialog dialog = new ErrorDialog(message);
        dialog.open();
    }
}

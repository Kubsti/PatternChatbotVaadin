package unibz.it.PatternChatbot;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

/**
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@PWA(name = "Pattern Chatbot", shortName = "PatternChatbot")
@Theme("my-theme")
public class AppShell implements AppShellConfigurator {
}

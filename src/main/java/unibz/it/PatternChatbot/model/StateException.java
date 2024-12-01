package unibz.it.PatternChatbot.model;

import unibz.it.PatternChatbot.ui.ErrorDialog;

import java.util.Optional;

public class StateException extends Exception{
    private final String exceptionName;

    public StateException(String exceptionName, String message) {
        super(message);  // Set the exception message
        this.exceptionName = exceptionName;  // Store the name of the exception
    }

    public String getExceptionName() {
        return exceptionName;
    }

    @Override
    public String toString() {
        return "StateException{" +
                "exceptionName='" + exceptionName + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}

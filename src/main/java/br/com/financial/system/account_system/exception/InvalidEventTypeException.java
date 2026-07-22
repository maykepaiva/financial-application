package br.com.financial.system.account_system.exception;

public class InvalidEventTypeException extends RuntimeException {

    public InvalidEventTypeException(String type) {
        super("Invalid event type: " + type);
    }
}

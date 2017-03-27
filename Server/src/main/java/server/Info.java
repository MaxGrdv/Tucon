package server;

import java.io.Serializable;

/**
 * This class is used to send messages from client to server
 */

public class Info implements Serializable {

    private String sender;
    private String message;

    Info(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}

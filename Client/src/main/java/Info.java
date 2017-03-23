import java.io.Serializable;

public class Info implements Serializable {

    public String sender;
    public String message;

    public Info(String sender, String message) {
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

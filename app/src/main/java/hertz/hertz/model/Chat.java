package hertz.hertz.model;

/**
 * Created by rsbulanon on 11/24/15.
 */
public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String timeStamp;

    public Chat() {
    }

    public Chat(String sender, String receiver, String message, String timeStamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}

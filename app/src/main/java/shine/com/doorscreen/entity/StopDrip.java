package shine.com.doorscreen.entity;

/**
 * Created by Administrator on 2016/10/27.
 */

public class StopDrip {

    /**
     * action : clientinfusionfinish
     * bedno : 101
     * sender : server
     */

    private String action;
    private String bedno;
    private String sender;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBedno() {
        return bedno;
    }

    public void setBedno(String bedno) {
        this.bedno = bedno;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}

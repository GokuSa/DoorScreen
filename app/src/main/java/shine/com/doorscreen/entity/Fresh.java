package shine.com.doorscreen.entity;

/**
 * Created by Administrator on 2016/8/4.
 */
public class Fresh {

    /**
     * action : fresh
     * clientmac : aa:bb:cc:dd:ee:ff
     * clientip : 12.21.2.2
     * sender : client
     */

    private String action;
    private String clientmac;
    private String clientip;
    private String sender;

    public Fresh() {
    }

    public Fresh(String action, String clientmac, String clientip, String sender) {
        this.action = action;
        this.clientmac = clientmac;
        this.clientip = clientip;
        this.sender = sender;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getClientmac() {
        return clientmac;
    }

    public void setClientmac(String clientmac) {
        this.clientmac = clientmac;
    }

    public String getClientip() {
        return clientip;
    }

    public void setClientip(String clientip) {
        this.clientip = clientip;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Fresh{" +
                "action='" + action + '\'' +
                ", clientmac='" + clientmac + '\'' +
                ", clientip='" + clientip + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}

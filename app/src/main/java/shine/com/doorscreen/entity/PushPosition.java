package shine.com.doorscreen.entity;

import java.util.List;

/**
 * Created by 李晓林 on 2017/3/8
 * qq:1220289215
 */

public class PushPosition {

    /**
     * action : pushposition
     * sender : server
     * callmessage : [{"bedno":"172.168.49.6"}]
     */

    private String action;
    private String sender;
    private List<CallMessage> callmessage;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<CallMessage> getCallmessage() {
        return callmessage;
    }

    public void setCallmessage(List<CallMessage> callmessage) {
        this.callmessage = callmessage;
    }

    public static class CallMessage {
        /**
         * bedno : 172.168.49.6
         */

        private String bedno;

        public String getBedno() {
            return bedno;
        }

        public void setBedno(String bedno) {
            this.bedno = bedno;
        }

        @Override
        public String toString() {
            return "CallMessage{" +
                    "bedno='" + bedno + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PushPosition{" +
                "action='" + action + '\'' +
                ", sender='" + sender + '\'' +
                ", callmessage=" + callmessage +
                '}';
    }
}

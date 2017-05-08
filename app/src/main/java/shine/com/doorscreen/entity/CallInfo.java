package shine.com.doorscreen.entity;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 * 门口屏叫号
 */
public class CallInfo {

    /**
     * action : pushdoorcallinfo
     * sender : server
     * callmessage : [{"msg":"16床呼叫..."}]
     */

    private String action;
    private String sender;
    /**
     * msg : 16床呼叫...
     */

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
        private String msg;
        private String bedno;
        private int type;
        private int client;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getBedno() {
            return bedno;
        }

        public int getType() {
            return type;
        }

        public int getClient() {
            return client;
        }

        @Override
        public String toString() {
            return "CallMessage{" +
                    "bedno='" + bedno + '\'' +
                    ", msg='" + msg + '\'' +
                    ", type=" + type +
                    ", client=" + client +
                    '}';
        }
    }


}

package shine.com.doorscreen.entity;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */
public class SystemVolume {

    /**
     * action : soundsvolume
     * list : [{"day":0,"start":"07:00","stop":"09:00","value":50}]
     * sender : server
     */

    private String action;
    private String sender;
    /**
     * day : 0
     * start : 07:00
     * stop : 09:00
     * value : 50
     */

    private List<VolumeParam> list;

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

    public List<VolumeParam> getList() {
        return list;
    }

    public void setList(List<VolumeParam> list) {
        this.list = list;
    }

    public static class VolumeParam {
        private int day;
        private String start;
        private String stop;
        private int value;

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getStop() {
            return stop;
        }

        public void setStop(String stop) {
            this.stop = stop;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

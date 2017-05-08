package shine.com.doorscreen.entity;

import java.util.List;

import shine.com.doorscreen.app.AppEntrance;

/**
 * Created by Administrator on 2016/9/21.
 * 输液信息
 */
public class DripInfo {

    /**
     * action : pushdoorinfusioninfo
     * sender : server
     * infusionwarnings : [{"patientid":56,"patientname":"张三","patientage":56,"bedno":"16床","start":123123132,"left":5}]
     */

    private String action;
    private String sender;
    /**
     * patientid : 56
     * patientname : 张三
     * patientage : 56
     * bedno : 16床
     * start : 123123132
     * left : 5
     */

    private List<Infusionwarnings> infusionwarnings;

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

    public List<Infusionwarnings> getInfusionwarnings() {
        return infusionwarnings;
    }

    public void setInfusionwarnings(List<Infusionwarnings> infusionwarnings) {
        this.infusionwarnings = infusionwarnings;
    }

    public static class Infusionwarnings {
        private int patientid;
        private String patientname;
        private String patientage;
        private String bedno;
        private long start;
        private String begin;
        private String speed;
        private int left;
        private int total;
        private int current_bai;
        private int current_shi;
        private int current_ge;
        private int next_bai;
        private int next_shi;
        private int next_ge;

        public Infusionwarnings() {

        }

        public void initilize(String begin) {
            this.begin=begin;
            setCurrentNumber();
            setNextNumber();
        }
        public void setCurrentNumber() {
            if (left >= 0) {
                current_bai=left/100;
                current_shi=(left-100*current_bai)/10;
                current_ge=left-100*current_bai-10*current_shi;
            }
        }
        public void setNextNumber() {
            next_bai=left/100;
            next_shi=(left-100*next_bai)/10;
            next_ge=left-100*next_bai-10*next_shi;
        }
        public void countDown() {
            if (left > 0) {
                left--;
                setNextNumber();
            }
        }

        public int getCurrentDripPackage() {
            if (total==0) {
                return AppEntrance.resIds[0];
            }
            int  rank = left *12/ total;
            return AppEntrance.resIds[rank];
        }

        public int getPatientid() {
            return patientid;
        }

        public void setPatientid(int patientid) {
            this.patientid = patientid;
        }

        public String getPatientname() {
            return patientname;
        }

        public void setPatientname(String patientname) {
            this.patientname = patientname;
        }

        public String getPatientage() {
            return patientage;
        }

        public void setPatientage(String patientage) {
            this.patientage = patientage;
        }

        public String getBedno() {
            return bedno;
        }

        public void setBedno(String bedno) {
            this.bedno = bedno;
        }

        public long getStart() {
            return start;
        }

        public String getSpeed() {
            return speed;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getCurrent_bai() {
            return current_bai;
        }

        public void setCurrent_bai(int current_bai) {
            this.current_bai = current_bai;
        }

        public int getCurrent_shi() {
            return current_shi;
        }

        public void setCurrent_shi(int current_shi) {
            this.current_shi = current_shi;
        }

        public int getCurrent_ge() {
            return current_ge;
        }

        public void setCurrent_ge(int current_ge) {
            this.current_ge = current_ge;
        }

        public int getNext_bai() {
            return next_bai;
        }

        public void setNext_bai(int next_bai) {
            this.next_bai = next_bai;
        }

        public int getNext_shi() {
            return next_shi;
        }

        public void setNext_shi(int next_shi) {
            this.next_shi = next_shi;
        }

        public int getNext_ge() {
            return next_ge;
        }

        public void setNext_ge(int next_ge) {
            this.next_ge = next_ge;
        }

        public String getBegin() {
            return begin;
        }

        public void setBegin(String begin) {
            this.begin = begin;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        @Override
        public String toString() {
            return "Infusionwarnings{" +
                    "patientid=" + patientid +
                    ", patientname='" + patientname + '\'' +
                    ", patientage=" + patientage +
                    ", bedno='" + bedno + '\'' +
                    ", start=" + start +
                    ", left=" + left +
                    '}';
        }
    }

}

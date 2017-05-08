package shine.com.doorscreen.entity;

import java.util.List;

/**
 * Created by Administrator on 2016/8/8.
 */
@Deprecated
public class PushDoorInfo {

    /**
     * action : pushdoorinfo
     * sender : server
     * hospitalname : 第一人民医院
     * departname : 心内科
     * week : 星期二
     * date : 2016年08月02日
     * time : 14:23
     * weather : 晴
     * temperature : 26
     * doctorlist : [{"doctorname":"王医生","title":"责任医生","img":"http://1.2.2.2/a.jpg"}]
     * patientlist : [{"bedno":"1101","patientname":"李先生"}]
     * callmessage : 10011床正在呼叫… …
     * watchtime : {"morning":"07:00~08:00","noon":"11:00~13:00","night":"17:00~22:00"}
     * warning : [{"bedno":"110011","left":5}]
     */

    private String action;
    private String sender;
    private String hospitalname;
    private String departname;
    private String week;
    private String date;
    private String time;
    private String weather;
    private String temperature;
    private String callmessage;
    /**
     * morning : 07:00~08:00
     * noon : 11:00~13:00
     * night : 17:00~22:00
     */

    private WatchtimeBean watchtime;
    /**
     * doctorname : 王医生
     * title : 责任医生
     * img : http://1.2.2.2/a.jpg
     */

    private List<DoctorBean> doctorlist;
    /**
     * bedno : 1101
     * patientname : 李先生
     */

    private List<Patient> patientlist;
    /**
     * bedno : 110011
     * left : 5
     */

    private List<WarningBean> warning;

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

    public String getHospitalname() {
        return hospitalname;
    }

    public void setHospitalname(String hospitalname) {
        this.hospitalname = hospitalname;
    }

    public String getDepartname() {
        return departname;
    }

    public void setDepartname(String departname) {
        this.departname = departname;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getCallmessage() {
        return callmessage;
    }

    public void setCallmessage(String callmessage) {
        this.callmessage = callmessage;
    }

    public WatchtimeBean getWatchtime() {
        return watchtime;
    }

    public void setWatchtime(WatchtimeBean watchtime) {
        this.watchtime = watchtime;
    }

    public List<DoctorBean> getDoctorlist() {
        return doctorlist;
    }

    public void setDoctorlist(List<DoctorBean> doctorlist) {
        this.doctorlist = doctorlist;
    }

    public List<Patient> getPatientlist() {
        return patientlist;
    }

    public void setPatientlist(List<Patient> patientlist) {
        this.patientlist = patientlist;
    }

    public List<WarningBean> getWarning() {
        return warning;
    }

    public void setWarning(List<WarningBean> warning) {
        this.warning = warning;
    }

    public static class WatchtimeBean {
        private String morning;
        private String noon;
        private String night;

        @Override
        public String toString() {
            return "WatchTime{" +
                    "morning='" + morning + '\'' +
                    ", noon='" + noon + '\'' +
                    ", night='" + night + '\'' +
                    '}';
        }

        public String getMorning() {
            return morning;
        }

        public void setMorning(String morning) {
            this.morning = morning;
        }

        public String getNoon() {
            return noon;
        }

        public void setNoon(String noon) {
            this.noon = noon;
        }

        public String getNight() {
            return night;
        }

        public void setNight(String night) {
            this.night = night;
        }
    }

    public static class DoctorBean {
        private String doctorname;
        private String title;
        private String img;

        @Override
        public String toString() {
            return "DoctorBean{" +
                    "doctorname='" + doctorname + '\'' +
                    ", title='" + title + '\'' +
                    ", img='" + img + '\'' +
                    '}';
        }

        public String getDoctorname() {
            return doctorname;
        }

        public void setDoctorname(String doctorname) {
            this.doctorname = doctorname;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

    public static class Patient {
        private String bedno;
        private String patientname;
        private boolean isCalling;

        public Patient() {
        }

        public Patient(String bedno, String patientname, boolean isCalling) {
            this.bedno = bedno;
            this.patientname = patientname;
            this.isCalling = isCalling;
        }

        @Override
        public String toString() {
            return "Patient{" +
                    "bedno='" + bedno + '\'' +
                    ", patientname='" + patientname + '\'' +
                    '}';
        }

        public String getBedno() {
            return bedno;
        }

        public void setBedno(String bedno) {
            this.bedno = bedno;
        }

        public String getPatientname() {
            return patientname;
        }

        public void setPatientname(String patientname) {
            this.patientname = patientname;
        }

        public boolean isCalling() {
            return isCalling;
        }

        public void setCalling(boolean calling) {
            isCalling = calling;
        }
    }

    public static class WarningBean {
        private String bedno;
        private int left;
        private int total;
        private int current_bai;
        private int current_shi;
        private int current_ge;
        private int next_bai;
        private int next_shi;
        private int next_ge;
        private int[] resIds;
        public WarningBean() {
        }

        public WarningBean(String bedno, int left,int[] resIds) {
            this.bedno = bedno;
            this.left = left;
            this.resIds=resIds;
            total=left;
            setCurrentNumber();
            countDown();
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
            if (resIds != null) {
                int  rank = left *12/ total;
                return resIds[rank];
            }
            return -1;
        }

        @Override
        public String toString() {
            return "WarningBean{" +
                    "bedno='" + bedno + '\'' +
                    ", left=" + left +
                    ", total=" + total +
                    ", current_bai=" + current_bai +
                    ", current_shi=" + current_shi +
                    ", current_ge=" + current_ge +
                    ", next_bai=" + next_bai +
                    ", next_shi=" + next_shi +
                    ", next_ge=" + next_ge +
                    '}';
        }

        public String getBedno() {
            return bedno;
        }

        public void setBedno(String bedno) {
            this.bedno = bedno;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public void setResIds(int[] resIds) {
            this.resIds = resIds;
        }

        public int getCurrent_shi() {
            return current_shi;
        }


        public int getCurrent_ge() {
            return current_ge;
        }


        public int getCurrent_bai() {

            return current_bai;
        }

        public int getNext_bai() {
            return next_bai;
        }

        public int getNext_shi() {
            return next_shi;
        }

        public int getNext_ge() {
            return next_ge;
        }

        public int getTotal() {
            return total;
        }
    }

    @Override
    public String toString() {
        return "PushDoorInfo{" +
                "action='" + action + '\'' +
                ", sender='" + sender + '\'' +
                ", hospitalname='" + hospitalname + '\'' +
                ", departname='" + departname + '\'' +
                ", week='" + week + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", weather='" + weather + '\'' +
                ", temperature='" + temperature + '\'' +
                ", callmessage='" + callmessage + '\'' +
                '}';
    }
}

package shine.com.doorscreen.entity;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 * 病室医生信息
 */
public class DoctorInfo {

    /**
     * action : pushdoordoctorinfo
     * sender : server
     * doctorlist : [{"doctorname":"王医生","title":"责任医生","img":"http://1.2.2.2/a.jpg"}]
     */

    private String action;
    private String sender;
    /**
     * doctorname : 王医生
     * title : 责任医生
     * img : http://1.2.2.2/a.jpg
     */

    private List<Doctor> doctorlist;

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

    public List<Doctor> getDoctorlist() {
        return doctorlist;
    }

    public void setDoctorlist(List<Doctor> doctorlist) {
        this.doctorlist = doctorlist;
    }

    public static class Doctor extends Person{
        private String doctorname;
        private int flag=1;
        public Doctor() {

        }
        public Doctor(String doctorname, String title, String img) {
            this.doctorname = doctorname;
            this.title = title;
            this.img = img;
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

        public int getFlag() {
            return flag;
        }

        @Override
        public String toString() {
            return "Doctor{" +
                    "doctorname='" + doctorname + '\'' +
                    ", flag=" + flag +
                    '}';
        }

        @Override
        public String getName() {
            return doctorname;
        }
    }

    @Override
    public String toString() {
        return "DoctorInfo{" +
                "action='" + action + '\'' +
                ", sender='" + sender + '\'' +
                ", doctorlist=" + doctorlist +
                '}';
    }
}

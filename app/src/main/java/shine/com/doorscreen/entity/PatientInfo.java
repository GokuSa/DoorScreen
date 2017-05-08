package shine.com.doorscreen.entity;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */
public class PatientInfo {

    /**
     * action : pushdoorpatientinfo
     * sender : server
     * patientlist : [{"bedno":"1101","patientname":"李先生"}]
     */

    private String action;
    private String sender;
    /**
     * bedno : 1101
     * patientname : 李先生
     */

    private List<Patient> patientlist;

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

    public List<Patient> getPatientlist() {
        return patientlist;
    }

    public void setPatientlist(List<Patient> patientlist) {
        this.patientlist = patientlist;
    }

    public static class Patient {
        private String bedno;
        private String patientname;
        private boolean isCalling;
        public Patient() {
        }

        public Patient(String bedno, String patientname) {
            this.bedno = bedno;
            this.patientname = patientname;
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

        @Override
        public String toString() {
            return "Patient{" +
                    "bedno='" + bedno + '\'' +
                    ", patientname='" + patientname + '\'' +
                    ", isCalling=" + isCalling +
                    '}';
        }
    }
}

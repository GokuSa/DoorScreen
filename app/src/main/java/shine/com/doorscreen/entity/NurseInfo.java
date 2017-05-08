package shine.com.doorscreen.entity;

import java.util.List;

/**
 * Created by Administrator on 2016/10/24.
 */

public class NurseInfo {

    /**
     * action : pushdoornurseinfo
     * sender : server
     * nurselist : [{"nursename":"儿科护士","title":"护士","img":"http://10.0.31.3/admin/images/upload/5a6919a9391a617d67b5774b8dd87dfd.jpg"}]
     */

    private String action;
    private String sender;
    /**
     * nursename : 儿科护士
     * title : 护士
     * img : http://10.0.31.3/admin/images/upload/5a6919a9391a617d67b5774b8dd87dfd.jpg
     */

    private List<Nurse> nurselist;

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

    public List<Nurse> getNurselist() {
        return nurselist;
    }

    public void setNurselist(List<Nurse> nurselist) {
        this.nurselist = nurselist;
    }

    public static class Nurse extends Person{
        //医生还是护士的标记
        private int flag=2;
        private String nursename;

        public int getFlag() {
            return flag;
        }
        public String getNursename() {
            return nursename;
        }

        public void setNursename(String nursename) {
            this.nursename = nursename;
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

        @Override
       public String getName() {
            return nursename;
        }

        @Override
        public String toString() {
            return "Nurse{" +
                    "flag=" + flag +
                    ", nursename='" + nursename + '\'' +
                    '}';
        }
    }
}

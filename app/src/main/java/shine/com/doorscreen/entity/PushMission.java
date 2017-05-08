package shine.com.doorscreen.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/8.
 * 给门口屏发送宣教信息
 */
public class PushMission {

    /**
     * action : pushmission
     * id : 22  播出单ID，此id唯一
     * sender : server
     * tasktype : 1  1:插播优先级最高，2：主任务优先级次之，3：垫片优先级最低
     * orderno : 2  //播放顺序id，数字小的先播放
     * startdate : 2016-08-02
     * stopdate : 2016-08-12
     * starttime : 02:00:00
     * stoptime : 15:00:00
     * cycle : 1111111  //按周播放
     */

    private String action;
    private int id;
    private String sender;
    private int tasktype;
    private int orderno;
    private String startdate;
    private String stopdate;
    private String starttime;
    private String stoptime;
    private int allday;
    /**
     * templateid : 23  //模板id
     * life : 11   模板切换时长（秒）
     * width : 1920
     * height : 1080
     * background : /ftproot/images/bg.jpg
     */

    private List<Templates> templates;
    private List<PlayTime> playtimes;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getAllday() {
        return allday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getTasktype() {
        return tasktype;
    }

    public void setTasktype(int tasktype) {
        this.tasktype = tasktype;
    }

    public int getOrderno() {
        return orderno;
    }

    public void setOrderno(int orderno) {
        this.orderno = orderno;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getStopdate() {
        return stopdate;
    }

    public void setStopdate(String stopdate) {
        this.stopdate = stopdate;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getStoptime() {
        return stoptime;
    }

    public void setStoptime(String stoptime) {
        this.stoptime = stoptime;
    }



    public List<Templates> getTemplates() {
        return templates;
    }

    public void setTemplates(List<Templates> templates) {
        this.templates = templates;
    }

    public List<PlayTime> getPlayTime() {
        return playtimes;
    }

    public static class PlayTime {
        private String start;
        private String stop;

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
    }
    public static class Templates {
        private int templateid;
        private int life;
        private int width;
        private int height;
        private String background;

        @Override
        public String toString() {
            return "Templates{" +
                    "templateid=" + templateid +
                    ", life=" + life +
                    ", width=" + width +
                    ", height=" + height +
                    ", background='" + background + '\'' +
                    ", regions=" + regions +
                    '}';
        }

        /**
         * regionid : 32
         * height : 1080
         * width : 1920
         * mainflag : 1  //1：最大区域，0：其他区域
         * left : 23   //距离模板左边的距离
         * top : 65  //距离模板顶部的距离
         * elements : [{"name":"功夫熊猫","type":1,"src":"/ftproot/video/xm.mpg","life":66}]
         */

        private List<Regions> regions;

        public int getTemplateid() {
            return templateid;
        }

        public void setTemplateid(int templateid) {
            this.templateid = templateid;
        }

        public int getLife() {
            return life;
        }

        public void setLife(int life) {
            this.life = life;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getBackground() {
            return background;
        }

        public void setBackground(String background) {
            this.background = background;
        }

        public List<Regions> getRegions() {
            return regions;
        }

        public void setRegions(List<Regions> regions) {
            this.regions = regions;
        }

        public static class Regions {
            private int regionid;
            private int height;
            private int width;
            private int mainflag;
            private int left;
            private int top;

            @Override
            public String toString() {
                return "Regions{" +
                        "regionid=" + regionid +
                        ", height=" + height +
                        ", width=" + width +
                        ", mainflag=" + mainflag +
                        ", left=" + left +
                        ", top=" + top +
                        ", elements=" + elements +
                        '}';
            }

            /**
             * name : 功夫熊猫
             * type : 1  //1:视频，2：图片，3：网页
             * src : /ftproot/video/xm.mpg
             * life : 66 //播放时长
             */

            private ArrayList<Elements> elements;

            public int getRegionid() {
                return regionid;
            }

            public void setRegionid(int regionid) {
                this.regionid = regionid;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getMainflag() {
                return mainflag;
            }

            public void setMainflag(int mainflag) {
                this.mainflag = mainflag;
            }

            public int getLeft() {
                return left;
            }

            public void setLeft(int left) {
                this.left = left;
            }

            public int getTop() {
                return top;
            }

            public void setTop(int top) {
                this.top = top;
            }

            public ArrayList<Elements> getElements() {
                return elements;
            }

            public void setElements(ArrayList<Elements> elements) {
                this.elements = elements;
            }
        }
    }
}

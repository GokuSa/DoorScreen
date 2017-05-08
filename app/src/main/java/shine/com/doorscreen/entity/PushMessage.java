package shine.com.doorscreen.entity;

import java.util.List;

/**
 * Created by Administrator on 2016/8/8.
 * 7.2.给门口屏发送文字信息
 */
public class PushMessage {

    /**
     * action : pushmessage
     * sender : server
     * message : 今日新闻今日新闻今日新闻今日新闻
     * 文字播放的有效日期和时间
     * startdate : 2016-08-02
     * stopdate : 2016-08-06
     * starttime : 06:00:00
     * stoptime : 12:00:00
     * 文字播放相关的方向，大小，颜色 字体 背景
     * direction : 1
     * fontsize : 24
     * fontcolor : #00ff00
     * fontname : 微软雅黑
     * background : #ff0000
     * 此信息的id，位置，速度
     * id : 55
     * left : 12
     * right : 12
     * bottom : 50
     * 快速，2：中速，3：慢速，
     * speed : 1
     * 插播优先级最高，2：主任务优先级次之，3：垫片优先级最低
     * tasktype : 1
     */

    private String action;
    private String sender;
    private String message;
    private String startdate;
    private String stopdate;
    private String starttime;
    private String stoptime;

    private int direction;
    private int fontsize;
    private String fontcolor;
    private String fontname;
    private String background;
    private int id;
    private int left;
    private int right;
    private int bottom;
    private int speed;
    private int tasktype;
    private List<PlayTime> playtimes;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getFontsize() {
        return fontsize;
    }

    public void setFontsize(int fontsize) {
        this.fontsize = fontsize;
    }

    public String getFontcolor() {
        return fontcolor;
    }

    public void setFontcolor(String fontcolor) {
        this.fontcolor = fontcolor;
    }

    public String getFontname() {
        return fontname;
    }

    public void setFontname(String fontname) {
        this.fontname = fontname;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getTasktype() {
        return tasktype;
    }

    public void setTasktype(int tasktype) {
        this.tasktype = tasktype;
    }

    public List<PlayTime> getPlayTimes() {
        return playtimes;
    }

    public static class PlayTime{
        private String start;
        private String stop;

        public String getStart() {
            return start;
        }

        public String getStop() {
            return stop;
        }
    }

}

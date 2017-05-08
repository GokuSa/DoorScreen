package shine.com.doorscreen.entity;

/**
 * Created by Administrator on 2016/8/8.
 * 1停止播放文字信息,
 * 2.删除文字信息
 * 3.停止播放宣教信息
 * 4.删除宣教信息
 * 以id为唯一标识码
 */
public class StopMessage {
    private String action;
    private String sender;
    private int id;

    public String getAction() {
        return action;
    }

    public String getSender() {
        return sender;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "StopMessage{" +
                "action='" + action + '\'' +
                ", sender='" + sender + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}

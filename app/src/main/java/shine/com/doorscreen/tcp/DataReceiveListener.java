package shine.com.doorscreen.tcp;

/**
 * Created by Administrator on 2016/8/5.
 */
public interface DataReceiveListener {
    void onDataReceive(int type, String json);
}

package shine.com.doorscreen.util;

import android.util.Log;

import shine.com.doorscreen.BuildConfig;

/**
 * Created by Administrator on 2016/9/21.
 * 打印日志工具
 */
public class LogUtil {
    private static final String TAG = "DoorScreen";
    public static void d(String caller,Object content) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, String.format("called with: [%s], content = [%s]",caller,content));
    }
    public static void e(String caller,Object content) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, String.format("called with: [%s], content = [%s]",caller,content));
    }
}

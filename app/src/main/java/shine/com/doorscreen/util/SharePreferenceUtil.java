package shine.com.doorscreen.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by Administrator on 2016/9/21.
 */
public class SharePreferenceUtil {
    private static final String TAG = "SharePreferenceUtil";
    public static final String name = "doorScreen";


    public static void saveTitle(@NonNull Context context, @NonNull String content) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("title", content).apply();
    }

    public static void clearTitle(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(name).apply();
    }
/*    public static PushDoorTitle getTitle(@NonNull Context context) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        String content = sp.getString("title", "");
        if (!TextUtils.isEmpty(content)) {
            return new Gson().fromJson(content, PushDoorTitle.class);
        }
        return null;
    }  */
    public static String  getTitle(@NonNull Context context) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getString("title", "");
    }


    public static void saveVolumeParam(@NonNull Context context,int volumeDay,int volumeNight,
                                       int volumeDayStartHour,int volumeDayStartMin,
                                       int volumeDayEndHour,int volumeDayEndMin) {
        context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().
                putInt("volumeDay",volumeDay).
                putInt("volumeNight",volumeNight).
                putInt("volumeDayStartHour",volumeDayStartHour).
                putInt("volumeDayStartMin",volumeDayStartMin).
                putInt("volumeDayEndHour",volumeDayEndHour).
                putInt("volumeDayEndMin",volumeDayEndMin).
                apply();
    }

    /**
     * 获取音量设置
     * 默认白天时间端9-17，白天音量50，夜晚20
     * @param context
     * @return
     */
    public static int[] getVolumnParam(@NonNull Context context) {
        int[] param=new int[6];
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        param[0]=sharedPreferences.getInt("volumeDay",30);
        param[1]=sharedPreferences.getInt("volumeNight",20);
        param[2]=sharedPreferences.getInt("volumeDayStartHour",8);
        param[3]=sharedPreferences.getInt("volumeDayStartMin",30);
        param[4]=sharedPreferences.getInt("volumeDayEndHour",19);
        param[5]=sharedPreferences.getInt("volumeDayEndMin",30);
        return param;
    }

    /**
     * 保存开关屏时间点
     * @param context
     * @param open_hour
     * @param close_hour
     */
    public static void saveDisplayTime(@NonNull Context context, int open_hour,int open_min,  int close_hour, int close_min) {
        context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().
                putInt("open_hour",open_hour).
                putInt("open_min",open_min).
                putInt("close_hour",close_hour).
                putInt("close_min",close_min).
                apply();
    }

    /**
     * 获取开关屏参数
     * 默认全天开屏
     * 00：01 开屏
     * 23：59关屏
     * @param context
     * @return
     */
    public static int[] getDisplayTime(@NonNull Context context) {
        int[] param=new int[4];
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        param[0]=sharedPreferences.getInt("open_hour",0);
        param[1]=sharedPreferences.getInt("open_min",1);
        param[2]=sharedPreferences.getInt("close_hour",23);
        param[3]=sharedPreferences.getInt("close_min",59);
        return param;
    }
}

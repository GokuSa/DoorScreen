package com.shine.timingboot;

/**
 * Created by zwb on 2016/1/13.
 * 包名 类名不可更改
 */
public class TimingBootUtils {
    static {
        System.loadLibrary("jni_rtc");
    }
    public native int setRtcTime(String str);
}


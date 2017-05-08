package com.shine.utilitylib;

/**
 * Created by GP on 2016/7/30.
 * 包名不可更改
 */
@Deprecated
public class CR16PadUtility {

    static {
        System.loadLibrary("R16PadUtility");
    }

    public native int DoInitBlLvds();
    public native int SetBlOn();
    public native int SetBlOff();
    public native int SetLvdsOn();
    public native int SetLvdsOff();
    public native int SetSpkAmpOn();
    public native int SetSpkAmpOff();
}

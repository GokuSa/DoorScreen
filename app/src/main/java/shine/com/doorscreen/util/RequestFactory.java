package shine.com.doorscreen.util;

import android.util.Log;

import com.google.gson.Gson;

import shine.com.doorscreen.entity.Fresh;

/**
 * Created by Administrator on 2016/7/4.
 * 请求工程
 */
public class RequestFactory {
    private static final String TAG = "RequestFactory";
    private static RequestFactory sRequestFactory;
    private Gson mGson = new Gson();
    private String ip;
    private String mac;
    private RequestFactory() {
        ip=Common.getIpAddress();
        mac=Common.getMacAddress();
    }

    public static RequestFactory getInstance() {
        if (sRequestFactory == null) {
            sRequestFactory = new RequestFactory();
        }
        return sRequestFactory;
    }

    /*刷新请求*/
    public String getFreshRequest() {
        Fresh baseRequest = new Fresh("fresh", mac,ip, "client");
        Log.d(TAG, baseRequest.toString());
        return String.format("[%s]",mGson.toJson(baseRequest));
    }

    public String getDataRequest() {
        Fresh baseRequest = new Fresh("getdoorscreeninfo", mac,ip, "client");
        Log.d(TAG, baseRequest.toString());
        return String.format("[%s]",mGson.toJson(baseRequest));
    }

}

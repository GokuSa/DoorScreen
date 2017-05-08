package shine.com.doorscreen.tcp;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import shine.com.doorscreen.activity.MainActivity;
import shine.com.doorscreen.app.AppEntrance;
import shine.com.doorscreen.util.LogUtil;
import shine.com.doorscreen.util.RequestFactory;
import shine.com.doorscreen.util.RootCommand;

/**
 * Created by Administrator on 2016/8/5.
 *
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<Object>{
    private static final String TAG = "NettyClientHandler";
    private SparseArray<DataReceiveListener> mRequestListeners = new SparseArray<>();
    long startTime = -1;
    private String mHeartbeat;
    private AlertDialog mAlertDialog;
    //断网信息标题
    private String mTitle="网络中断";

    public NettyClientHandler() {
        AlertDialog.Builder builder=new  AlertDialog.Builder(MainActivity.getInstance());
        mAlertDialog=builder.create();

    }

    @SuppressWarnings("handlerleak")
    private Handler mHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mTitle = "服务器正在维护";
                    break;
                case 2:
                    mTitle = "和服务器断开连接";
                    break;
                case 3 :
                    if (!mAlertDialog.isShowing()) {
                        mAlertDialog.setTitle(mTitle);

                        mAlertDialog.setMessage("正在连接服务器");
                        mAlertDialog.show();
                    }
                    break;
            }
        }
    };
    private boolean isopen=true;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LogUtil.d(TAG, "channelActive() called");
        if (startTime < 0) {
            startTime = System.currentTimeMillis();
        }
        mHeartbeat = RequestFactory.getInstance().getFreshRequest();
        ctx.writeAndFlush(mHeartbeat);
        //请求医生护士等信息
        ctx.writeAndFlush(RequestFactory.getInstance().getDataRequest());
        if (mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, Object msg) throws Exception {
        String receive = ((String) msg);
        //此次协议返回的json数据是数组格式，包含【】，所以在解析之前需要去除【和】
        String result=receive.substring(1,receive.length()-1);
        Log.d(TAG, "verify : " + result);
        JSONObject jsonObject = new JSONObject(result);
        String action = jsonObject.optString("action");
        switch (action) {
            case "fresh":
                long time = jsonObject.optLong("time");
                if (Math.abs(System.currentTimeMillis() - time) > 2 * 1000) {
                    LogUtil.d(TAG, "modify time");
                    if (time / 1000 < Integer.MAX_VALUE) {
                        ((AlarmManager) AppEntrance.getAppEntrance().getSystemService(Context.ALARM_SERVICE))
                                .setTime(time);
                        new Thread(){
                            @Override
                            public void run() {
                                boolean result = new RootCommand().checkTime();
                                if (result) {
                                    callListeners(1, MainActivity.CHECK_TIME, "");
                                }
                            }
                        }.start();
                    }
                }
                final EventLoop loop = ctx.channel().eventLoop();
                loop.schedule(new Runnable() {
                    @Override
                    public void run() {
                        ctx.writeAndFlush(mHeartbeat);
                    }
                }, NettyClient.RECONNECT_DELAY, TimeUnit.SECONDS);
                break;
            // 给门口屏发送跑马灯文字信息
            case"pushmessage":
                callListeners(1, MainActivity.MARQUEE_UPDATE, result);
                break;
            //停止跑马灯文字信息
            case "stopmessage":
                callListeners(1, MainActivity.MARQUEE_STOP, result);
                break;
            //删除跑马灯文字信息
            case "deletemessage":
                callListeners(1, MainActivity.MARQUEE_DELETE, result);
                break;
            //更新门口屏标题信息
            case "pushdoortitleinfo":
                callListeners(1, MainActivity.DOOR_TITLE_UPDATE, result);
                break;
            //给门口屏发送宣教信息
            case "pushmission":
                callListeners(1, MainActivity.MEDIA_DOWNLOAD, result);
                break;
            //停止播放宣教信息
            case "stopmission":
                callListeners(1, MainActivity.MEDIA_STOP, result);
                break;
            //删除播放宣教信息
            case "deletemission":
                callListeners(1, MainActivity.MEDIA_DELETE, result);
                break;
            //输液信息
            case "pushdoorinfusioninfo":
                callListeners(1, MainActivity.DRIP_UPDATE, result);
                break;
            case "clientinfusionfinish":
                callListeners(1, MainActivity.STOP_DRIP, result);
                break;
            //医生信息
            case "pushdoordoctorinfo":
                callListeners(1, MainActivity.DOCTOR_INFO, result);
                break;
            case "pushdoornurseinfo":
                callListeners(1, MainActivity.NURSOR_INFO, result);
                break;
            //呼叫信息
            case "pushdoorcallinfo":
                callListeners(1, MainActivity.CALL_INFO, result);
                break;
            //定位消息
            case "pushposition":
                callListeners(1, MainActivity.PUSHPOSITION, result);
                break;
                //患者信息
            case "pushdoorpatientinfo":
                callListeners(1, MainActivity.PATIENT_INFO, result);
                break;
            //系统亮度，开关屏
            case "systemlight":
                callListeners(1, MainActivity.SCREEN_SWITCH, result);
               /* if (isopen) {
                    Common.closeScreen();
                }else{
                    Common.openScreen();
                }
                isopen=!isopen;*/
                break;
            //系统音量
            case "soundsvolume":
                callListeners(1, MainActivity.VOLUME_SWITCH, result);
                break;
            case "reboot":
                callListeners(1,MainActivity.REBOOT,result);
                break;
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }

        IdleStateEvent e = (IdleStateEvent) evt;
        if (e.state() == IdleState.READER_IDLE) {
            // The connection was OK but there was no traffic for last period.
            mHandler.sendEmptyMessage(1);
            ctx.close();
        }
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        mHandler.sendEmptyMessage(2);
//        println("Disconnected from: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        mHandler.sendEmptyMessage(3);
        final EventLoop loop = ctx.channel().eventLoop();
        loop.schedule(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance().connect(NettyClient.getInstance().configureBootstrap(new Bootstrap(),loop));
            }
        }, NettyClient.RECONNECT_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 添加请求监听
     * @param requestCode 请求代码
     * @param receiveListener 请求成功的回调
     */
    public void addRequestListener(int requestCode, DataReceiveListener receiveListener) {
        if (receiveListener != null) {
            mRequestListeners.put(requestCode, receiveListener);
        }
    }

    /**
     * @param requestCode
     * 在监听页面销毁的时候会移除监听
     */
    public void removeRequest(int requestCode) {
        mRequestListeners.remove(requestCode);
    }
    //移除所有监听
    public void removeAll() {
        mRequestListeners.clear();
    }

    /**
     *
     * @param requestCode 请求码，确定监听器的身份,目前同一为1，是在同一个页面的监听
     * @param type 返回类型，同一监听器可监听一类回调，根据type区分
     * @param response 具体返回数据信息
     */
    private void callListeners(int requestCode,final int type,final String response) {
       final  DataReceiveListener listener = mRequestListeners.get(requestCode);
        if (listener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onDataReceive(type,response);
                }
            });
        }
    }
    void println(String msg) {
        if (startTime < 0) {
            System.err.format("[SERVER IS DOWN] %s%n", msg);
        } else {
            System.err.format("[UPTIME: %5ds] %s%n", (System.currentTimeMillis() - startTime) / 1000, msg);
        }
    }
}

package shine.com.doorscreen.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import shine.com.doorscreen.R;
import shine.com.doorscreen.adapter.DoorInfoPagerAdapter;
import shine.com.doorscreen.app.AppEntrance;
import shine.com.doorscreen.customview.CustomViewPager;
import shine.com.doorscreen.database.DoorScreenDataBase;
import shine.com.doorscreen.entity.CallInfo;
import shine.com.doorscreen.entity.DoctorInfo;
import shine.com.doorscreen.entity.DripInfo;
import shine.com.doorscreen.entity.Elements;
import shine.com.doorscreen.entity.NurseInfo;
import shine.com.doorscreen.entity.PatientInfo;
import shine.com.doorscreen.entity.PushDoorTitle;
import shine.com.doorscreen.entity.PushMessage;
import shine.com.doorscreen.entity.PushMission;
import shine.com.doorscreen.entity.PushPosition;
import shine.com.doorscreen.entity.StopDrip;
import shine.com.doorscreen.entity.StopMessage;
import shine.com.doorscreen.fragment.DoorFragment;
import shine.com.doorscreen.fragment.MediaFragment;
import shine.com.doorscreen.service.DoorService;
import shine.com.doorscreen.service.DownLoadService;
import shine.com.doorscreen.tcp.DataReceiveListener;
import shine.com.doorscreen.tcp.NettyClient;
import shine.com.doorscreen.util.IniReaderNoSection;
import shine.com.doorscreen.util.LogUtil;
import shine.com.doorscreen.util.SharePreferenceUtil;

/**
 * 主页面,承载门口屏和视频宣教页面，
 *1. 启动后加载这两个页面，并显示门口屏DoorFragment，为了和后台同步，在重启等情况下其从本地数据库查询输液，医生，患者，标题信息完成初始化
 * 2.添加服务端数据监听，接受如输液信息，跑马灯信息，开关屏设置等，这些信息都会保存在本地
 * 3.添加广播用于通信，如输液结束，播放视频，显示呼叫，可用广播通知相关页面更新
 * 4.启动后台服务，10 s后（即10秒延时初始化，防止占资源）使用本地保存的信息设置开关屏和音量，
 *    12 s后启动宣教信息和跑马灯信息定时（60s）扫描一次，检索符合当前时间播放的多媒体和跑马灯id
 *    通过广播发给主页面处理，多媒体处理逻辑如下：
 *    a.如果需要播发宣教视频
 *      当前正在呼叫，只通知多媒体页面（MediaFragment）更新数据，并标记播放状态，但不切换到此页面，当呼叫结束才会切换到此页面
 *      不在呼叫，切换到多媒体播放页面并更新数据
 *      正在播放，后台有更新，则更新数据，否则不处理
 *     b.如果不需要播放视频
 *          当前在播放，停止并切换到门口屏页面
 *          不在播放，不做处理
 *     跑马灯播放逻辑类似：
 *     如果在门口屏页面：更新并且播放，不在门口屏页面，只更新不播放，无更新 不处理
 *
 *  5 .停止或删除宣教或跑马灯id，都会先更新数据库内容然后通知后台服务重新定时扫描
 *
 * 暂时通过broadcast通知相关页面更新，以后可能使用content provider和loader
 * 使用DoorFragment 展示住院相关信息为主页面，有输液信息优先展示，没有显示医生信息
 * 使用MediaFragment 播放视频，收到后台插播的宣教切换到此页面，在播放过程中如果有呼叫切回输液界面
 */
public class MainActivity extends AppCompatActivity implements DataReceiveListener {
    private static final String TAG = "MainActivity";
    //宣教信息下載廣播action
    public static final String MEDIA_ACTION="com.action.media";
    //存储目录
    public static final String MOVIE_PATH = "/storage/emulated/legacy/Movies";
    public static final String PICTURE_PATH = "/storage/emulated/legacy/Pictures";
    public static final int CONNECT_SERVER = 0;
    public static final int MARQUEE_UPDATE = 1;
    public static final int MARQUEE_STOP = 2;
    public static final int MARQUEE_DELETE = 3;
    public static final int DOOR_TITLE_UPDATE = 4;
    public static final int MEDIA_DOWNLOAD = 5;
    public static final int MEDIA_STOP = 6;
    public static final int MEDIA_DELETE = 7;
    public static final int DRIP_UPDATE = 8;
    public static final int DOCTOR_INFO = 9;
    public static final int NURSOR_INFO = 44;
    public static final int CALL_INFO = 10;
    public static final int PATIENT_INFO = 11;
    public static final int SCREEN_SWITCH = 12;
    public static final int VOLUME_SWITCH = 13;
    public static final int REBOOT = 14;
   public   static final int VOLUME_SET = 20;
   public   static final int SWITCH_SCREEN = 23;
   public   static final int DOWNLOAD_DONE = 40;
   public   static final int DRIP_DONE = 42;
   public   static final int SCAN_MEDIA = 45;
   public   static final int SCAN_MEDIA_INTERVAL = 60*1000;
    public   static final int SCAN_MARQUEE = 46;
    public   static final int SCAN_MARQUEE_INTERVAL = 60*1000;
    public static final int STOP_DRIP=51;
    public static final int STOP_ALL_DRIP=52;
    public static final int CHECK_TIME=53;
    //显示门灯
    public static final int SHOW_DOOR_LIGHT=54;
    public static final int PUSHPOSITION = 55;
    //视频和图片目录
    private File mFileMovies= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    private File mFilePicture=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    private Gson  mGson=new Gson();

    /**
     * 门口屏信息主要界面
     * 显示医生、输液、患者、呼叫和跑马灯信息
     */
    private DoorFragment mDoorFragment;
    /**
     * 多媒体播放界面
     * 用于插播宣教视频
     */
    private MediaFragment mMediaFragment;
    /**
     * 使用自定义ViewPager管理门口屏页面和宣教视频页面
     *
     */
    private CustomViewPager mViewPager;


    /**
     * 宣教信息是否在播，此时有人呼叫换到门口屏，呼叫结束返回播放页面
     */
    private boolean isMediaPlaying =false;

    private boolean isMarqueePlaying =false;
    private boolean isCalling=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        initView();
        //创建视频和图片下载目录
        setLocalStorage();
        //多媒体素材更新监听
        NettyClient.getInstance().addListener(1, this);
        //注册广播
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,new IntentFilter(MEDIA_ACTION));

        //开启后台服务设置系统音量和开关屏
        startService(DoorService.newIntent(this,-1,""));
    }


    /**
     * 初始化主要页面，使用Viewpager 管理fragment
     * DoorFragment 显示输液 和 医生信息
     * VideoFragment 播放宣教视频
     *
     */
    private void initView() {
        mViewPager = (CustomViewPager) findViewById(R.id.viewpager);
        mMediaFragment = new MediaFragment();
        mDoorFragment = new DoorFragment();
        List<Fragment> fragmentList = new ArrayList<>();
        //不要改变添加顺序
        fragmentList.add(mDoorFragment);
        fragmentList.add(mMediaFragment);
        DoorInfoPagerAdapter doorInfoPagerAdapter = new DoorInfoPagerAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(doorInfoPagerAdapter);

    }

    private void setLocalStorage() {
//        mFileMovies = new File(MOVIE_PATH);
        if (!mFileMovies.exists()) {
            if (!mFileMovies.mkdirs()) {
                LogUtil.e(TAG,"fail to create dir movies");
            }
        }
//        mFilePicture = new File(PICTURE_PATH);
        if (!mFilePicture.exists()) {
            if (!mFilePicture.mkdirs()) {
                LogUtil.e(TAG,"fail to create dir picture");
            }
        }
    }

    /**
     *
     * @param type 监听类型，目前统一在MainActivity监听后台发来的信息 没有其他类型
     * @param json 从后台发来的数据
     */
    @Override
    public void onDataReceive(int type, String json) {
        switch (type) {
            case CHECK_TIME:
                //与后台时间校对后重新设置当前时间
                LogUtil.d(TAG, "本地时间有误,设置校对后日期时间");
                mDoorFragment.initializeTitle();
                break;
            //跑马灯信息
            case MARQUEE_UPDATE:
                PushMessage pushMessage = mGson.fromJson(json, PushMessage.class);
                if (pushMessage != null&&pushMessage.getId()>0) {
                    DoorScreenDataBase.getInstance(this).insertMarquee(pushMessage);
                    //通知后台重新检索
                    Intent intent = DoorService.newIntent(this, MARQUEE_UPDATE, "");
                    startService(intent);
                }
                break;
            //停止跑马灯信息
            case MARQUEE_STOP:
                //停止跑马灯目前更改数据库状态为-1
                StopMessage stopMessage = mGson.fromJson(json, StopMessage.class);
                if (stopMessage != null&&stopMessage.getId()>0) {
                    DoorScreenDataBase.getInstance(this).stopMarquee(stopMessage.getId());
                    //通知后台重新检索
                    Intent intent = DoorService.newIntent(this, MARQUEE_STOP, "");
                    startService(intent);
                }
                break;
            //删除跑马灯信息,后台删除之前必须停止，所以不需要更新界面
            case MARQUEE_DELETE:
                StopMessage delMessage = mGson.fromJson(json, StopMessage.class);
                DoorScreenDataBase.getInstance(this).deleteMarquee(delMessage.getId());
                break;
            //病室标题更新
            case DOOR_TITLE_UPDATE:
                    PushDoorTitle pushDoorTitle = mGson.fromJson(json, PushDoorTitle.class);
                    if (pushDoorTitle != null) {
                        //保存到本地
                        SharePreferenceUtil.saveTitle(this,pushDoorTitle.getDepartname());
                        mDoorFragment.updateTitle(pushDoorTitle);
                    }
                break;
            //下載宣教信息
            case MEDIA_DOWNLOAD:
                if (mFileMovies.exists() && mFilePicture.exists()) {
                    LogUtil.d(TAG,"begin to download media");
                    PushMission pushMission = mGson.fromJson(json, PushMission.class);
                    if (pushMission != null&&pushMission.getId()>0) {
                        updateLocalMedia(pushMission);
                    }
                }else{
                    LogUtil.e(TAG, "宣教信息目录不存在");
                }
                break;
            case MEDIA_STOP:
                StopMessage stopMediaMessage = mGson.fromJson(json, StopMessage.class);
                if (stopMediaMessage != null&&stopMediaMessage.getId()>0) {
                    LogUtil.d(TAG, "stop media:" + stopMediaMessage);
                    DoorScreenDataBase.getInstance(this).updateMediaStaus(stopMediaMessage.getId());
                    //通知后台重新检索
                    Intent intent = DoorService.newIntent(this, MEDIA_STOP, "");
                    startService(intent);
                }
                break;
            case MEDIA_DELETE:
                StopMessage deleteMediaMessage = mGson.fromJson(json, StopMessage.class);
                if (deleteMediaMessage != null&&deleteMediaMessage.getId()>0) {
                    LogUtil.d(TAG, "delete media:" + deleteMediaMessage);
                    DoorScreenDataBase.getInstance(this).deleteMedia(deleteMediaMessage.getId());
                    //通知后台重新检索
                    Intent intent = DoorService.newIntent(this, MEDIA_DELETE, "");
                    startService(intent);
                }
                break;
            case DRIP_UPDATE:
                DripInfo dripInfo=mGson.fromJson(json, DripInfo.class);
                mDoorFragment.startDrip(dripInfo);
                break;
            case STOP_DRIP:
                StopDrip stopDrip = mGson.fromJson(json, StopDrip.class);
                if (stopDrip != null) {
                    mDoorFragment.stopDrip(stopDrip.getBedno());
                }
                break;
            case DOCTOR_INFO:
                DoctorInfo doctorInfo=mGson.fromJson(json, DoctorInfo.class);
                mDoorFragment.updateDoctor(doctorInfo);
                break;
            case NURSOR_INFO:
                NurseInfo nurseInfo = mGson.fromJson(json, NurseInfo.class);
                mDoorFragment.updateNurse(nurseInfo);
                break;
            case CALL_INFO:
                CallInfo callInfo=mGson.fromJson(json, CallInfo.class);
                if (callInfo != null) {
                    List<CallInfo.CallMessage> callmessage = callInfo.getCallmessage();
                    if (callmessage != null && callmessage.size() > 0) {
                        LogUtil.d(TAG, "patient is calling switch to door fragment");
                        //有病人呼叫并且不再门口屏页面，切换
                        isCalling=true;
                        mViewPager.setCurrentItem(0);
                        mDoorFragment.patientCall(callmessage);
                        int call_type=0;
                        int client=0;
                        //判断呼叫优先级
                        for (CallInfo.CallMessage callMessage : callmessage) {
                            if (callMessage.getType() > call_type) {
                                call_type = callMessage.getType();
                                client=callMessage.getClient();
                            }
                        }
                        switch (call_type) {
                            case 0:
                                startService( DoorService.newIntent(this, SHOW_DOOR_LIGHT, getResources().getString(R.string.long_green)));
                                break;
                            case 2:
                                startService( DoorService.newIntent(this, SHOW_DOOR_LIGHT, getResources().getString(R.string.long_orange)));
                                break;
                            case 3:

                                break;
                            case 4:
                                if (client == 1) {
                                    startService( DoorService.newIntent(this, SHOW_DOOR_LIGHT, getResources().getString(R.string.long_red)));
                                } else if (client == 7) {
                                    startService( DoorService.newIntent(this, SHOW_DOOR_LIGHT, getResources().getString(R.string.long_blue)));
                                }
                                break;
                            case 5:
                                startService( DoorService.newIntent(this, SHOW_DOOR_LIGHT, getResources().getString(R.string.long_orange)));
                                break;
                        }
                    } else {
                        LogUtil.d(TAG, "end call");
                        startService( DoorService.newIntent(this, SHOW_DOOR_LIGHT, getResources().getString(R.string.light_off)));
                        isCalling=false;
                        mDoorFragment.patientCall(null);
                        //呼叫结束，如果有视频播放，切回播放页面
                        if (isMediaPlaying) {
                            LogUtil.d(TAG,"meida is playing switch to media fragment");
                            mViewPager.setCurrentItem(1);
                        }
                    }
                }

                break;
            case PUSHPOSITION:
                PushPosition pushPositionInfo=mGson.fromJson(json, PushPosition.class);
                if (pushPositionInfo != null) {
                    LogUtil.d(TAG, "pushPositionInfo:" + pushPositionInfo);
                    List<PushPosition.CallMessage> callmessage = pushPositionInfo.getCallmessage();
                    if (callmessage != null && callmessage.size() > 0) {
                        startService( DoorService.newIntent(this, SHOW_DOOR_LIGHT, getResources().getString(R.string.long_green)));
                    }else{
                        startService( DoorService.newIntent(this, SHOW_DOOR_LIGHT, getResources().getString(R.string.light_off)));
                    }
                }else{
                    LogUtil.d(TAG, "push p is null");
                }
                break;
            case PATIENT_INFO:
                PatientInfo patientInfo = mGson.fromJson(json, PatientInfo.class);
                mDoorFragment.updatePatient(patientInfo);
                break;
            case SCREEN_SWITCH:
                //开关屏
                startService(DoorService.newIntent(this,SCREEN_SWITCH,json));
                break;
            //设置音量
            case VOLUME_SWITCH:
                startService(DoorService.newIntent(this,VOLUME_SWITCH,json));
                break;
            //重启
            case REBOOT:
                startService(DoorService.newIntent(this,REBOOT,""));
                break;
        }
    }


    /**
     * 更新本地宣教信息
     * 根据宣教播放时间段不同存储不同字段
     * 存储的时间是决定播放的关键
     *
     */
    public void updateLocalMedia(@NonNull PushMission pushMission) {

        //插入播发时间段
        DoorScreenDataBase.getInstance(this).insertMediaTime(pushMission);

        //数据结构为以前多媒体结构，很多无用数据，
        List<PushMission.Templates> elementlist = pushMission.getTemplates();
        LogUtil.d(TAG, "elementlist.size():" + elementlist.size());
        if (elementlist.size() < 1) {
            return;
        }
        List<PushMission.Templates.Regions> regions = elementlist.get(0).getRegions();
        LogUtil.d(TAG, "regions.size():" + regions.size());
        if (regions.size() < 1) {
            return;
        }
        //这个就是最新宣教素材
        ArrayList<Elements> elements = regions.get(0).getElements();
        if (elements == null||elements.size()<1) {
            return;
        }
        LogUtil.d(TAG, "elementsToUpdate:" + elements);
        //本地关于服务器下载的配置文件
        IniReaderNoSection inir = new IniReaderNoSection(AppEntrance.ETHERNET_PATH);
        //需要下载文件的路径前缀，包括ftp地址，端口
        String mHeader = String.format("ftp://%s:%s", inir.getValue("ftpip"), inir.getValue("ftpport"));

        //设置下载元素内容
        for (Elements element : elements) {
            //设置下载的完整路径
            element.setSrc(String.format("%s%s",mHeader , element.getSrc()));
            //设置播单id
            element.setId(pushMission.getId());

            //分别设置图片和视频的下载路径
            if(element.getType()==1){
                element.setPath(String.format("%s/%s",mFileMovies.getAbsolutePath(),element.getName()));
            }else if(element.getType()==2){
                element.setPath(String.format("%s/%s",mFilePicture.getAbsolutePath(),element.getName()));
            }
        }

        if (elements.size() > 0) {
            //启动后台服务下载
            Intent intent = new Intent(this, DownLoadService.class);
            intent.putExtra("elements", elements);
            intent.putExtra("id", pushMission.getId());
            startService(intent);
        }

    }
    //宣教信息的接受監聽
    private BroadcastReceiver mReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新完成通知
            int action = intent.getIntExtra("flag",-1);
            LogUtil.d(TAG, "mReceiver " +action);
            switch (action) {
                //输液完成
                case DRIP_DONE:
                    //停止更新输液信息
                    mDoorFragment.terminateDrip();
                    break;
                case STOP_ALL_DRIP:
                    mDoorFragment.stopAllDrip();
                    break;
                //后台每分钟检索一次多媒体信息
                case SCAN_MEDIA:
                    String param = intent.getStringExtra("param");
                    if (TextUtils.isEmpty(param)) {
                        //非宣教视频阶段，切换到门口屏
                        if (isMediaPlaying) {
                            isMediaPlaying =false;
                            LogUtil.d(TAG,"switch from media to door framgent ");
                            mViewPager.setCurrentItem(0);
                        }

                    }else{
                        isMediaPlaying =true;
                        if (!isCalling) {
                            LogUtil.d(TAG,"is not calling ,switch to media fragment");
                            mViewPager.setCurrentItem(1);
                        }else{
                            LogUtil.d(TAG,"is  calling ,just to update media fragment");
                        }
                        mMediaFragment.updateMedia(param);
                    }
                    break;
                case SCAN_MARQUEE:
                    String paramMarquee = intent.getStringExtra("param");
                    if (TextUtils.isEmpty(paramMarquee)) {
                        if (isMarqueePlaying) {
                            isMarqueePlaying=false;
                            mDoorFragment.stopMarquee();
                        }
                    }else{
                        isMarqueePlaying=true;
                        mDoorFragment.updateMarquee(paramMarquee);
                    }
                    break;
            }
        }
    };

    public static Intent newIntent(int flag,String param) {
        Intent intent = new Intent(MEDIA_ACTION);
        intent.putExtra("flag",flag);
        intent.putExtra("param",param);
        return intent;
    }

    private static MainActivity mContext;
    public  static MainActivity getInstance() {
        return mContext;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出时确保移除所有监听
        NettyClient.getInstance().removeAllListener();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        stopService(DoorService.newIntent(this,-1,""));
        //当前退出时，仍然在发心跳，先简单杀死进程，
        // TODO: 2016/9/13 找到心跳不能正常停止原因 有可能是动画
        System.exit(0);
    }
}

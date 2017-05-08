package shine.com.doorscreen.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import shine.com.doorscreen.R;
import shine.com.doorscreen.adapter.DoctorAdapter;
import shine.com.doorscreen.adapter.DripAdapter;
import shine.com.doorscreen.adapter.InsetDecoration;
import shine.com.doorscreen.adapter.PatientAdapter;
import shine.com.doorscreen.adapter.RecycleViewDivider;
import shine.com.doorscreen.customview.MarqueeView;
import shine.com.doorscreen.database.DoorScreenDataBase;
import shine.com.doorscreen.entity.CallInfo;
import shine.com.doorscreen.entity.DoctorInfo;
import shine.com.doorscreen.entity.DripInfo;
import shine.com.doorscreen.entity.NurseInfo;
import shine.com.doorscreen.entity.PatientInfo;
import shine.com.doorscreen.entity.Person;
import shine.com.doorscreen.entity.PushDoorTitle;
import shine.com.doorscreen.util.Common;
import shine.com.doorscreen.util.LogUtil;
import shine.com.doorscreen.util.SharePreferenceUtil;

/**
 * A simple {@link Fragment} subclass.
 * 门口屏主页面 显示输液进制和医生信息，呼叫信息
 *
 *
 */
public class DoorFragment extends Fragment  {
    private static final String TAG = "DoorFragment";
    public static final int DRIP_UPDATE_INTERVAL = 60 * 1000;
    private static final int DRIP_DOCTOR_SWITCH = 779;
    private static final int DRIP_DOCTOR_SWITCH_INTERVAL = 10*1000;
    public static final int TIME_UPDATE_INTERVAL = 60 * 1000;
    private static final int KEEP_TIME = 0;
    private static final int DRIP_UPDATE = 1;
    private static final int UPDATE_MARQUEE = 6;
    //重新校对更新时间
    private static final int RE_KEEP_TIME = 7;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.tv_weekday)
    TextView mTvWeekday;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv_marquee)
    TextView mTvMarquee;
    @Bind(R.id.marqueeView)
    MarqueeView mMarqueeView;
    @Bind(R.id.tv_info)
    TextView mTvInfo;
    @Bind(R.id.rv_drip)
    RecyclerView mRvDrip;
    @Bind(R.id.rv_doctor)
    RecyclerView mRvDoctor;
    @Bind(R.id.rv_patient_info)
    RecyclerView mRvPatients;
    @Bind(R.id.viewSwitchDripAndDoctor)
    ViewSwitcher mViewSwitchDripAndDoctor;
    @Bind(R.id.ll_drip_doctor)
    LinearLayout mLlDripDoctor;
    @Bind(R.id.viewSwitchCallAndVisit)
    ViewSwitcher mViewSwitchCallAndVisit;
    @Bind(R.id.iv_logo)
    ImageView mIvLogo;
    @Bind(R.id.tv_visit_period_one)
    TextView mTvVisitPeriodOne;
    @Bind(R.id.tv_visit_period_two)
    TextView mTvVisitPeriodTwo;
    @Bind(R.id.tv_visit_period_three)
    TextView mTvVisitPeriodThree;
    @Bind(R.id.tv_patient_call)
    TextView mTvPatientCall;
    /**
     * 格式化当前时间，用于标题的时间和输液开始时间格式化
     */
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
    private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.CHINA);

    private DripAdapter mDripAdapter;
    private PatientAdapter mPatientAdapter;
    private DoctorAdapter mDoctorAdapter;
    private boolean isVisible=false;
    private boolean isPrepared=false;
    /**
     * 当前跑马灯信息
     */
    List<String> mMarquees=new ArrayList<>();


    @SuppressWarnings("handlerleak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case KEEP_TIME:
                    String date_week = dateFormat.format(System.currentTimeMillis());
                    //更新日期
                    mTvDate.setText(date_week.substring(0, date_week.length() - 3));
                    //更新星期
                    mTvWeekday.setText(date_week.substring(date_week.length() - 3));
                    //更新时间
                    String time = mSimpleDateFormat.format(System.currentTimeMillis());
                    mTvTime.setText(time);
                    //一分钟更新一次
                    sendEmptyMessageDelayed(KEEP_TIME, TIME_UPDATE_INTERVAL);
                    break;
                case DRIP_UPDATE:
                    mDripAdapter.update();
                    sendEmptyMessageDelayed(DRIP_UPDATE, DRIP_UPDATE_INTERVAL);
                    break;
                case DRIP_DOCTOR_SWITCH:
                    if (mViewSwitchDripAndDoctor.getCurrentView().getId() != R.id.rv_drip) {
                        mViewSwitchDripAndDoctor.showNext();
                        mTvInfo.setText(R.string.drip_info);
                    }
                    break;
                case RE_KEEP_TIME:
                    checkTime();
                    break;
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.d(TAG, "isVisibleToUser = [" + isVisibleToUser + "]");
        isVisible = isVisibleToUser;
        if (isVisibleToUser) {
            onVisible();
        }else{
            onInvisible();
        }
    }

    public void onInvisible() {
        if (isPrepared) {
            mMarqueeView.terminate();
        }
    }

    private void onVisible() {
        if (isPrepared) {
            if (mMarquees.size() > 0) {
                mMarqueeView.setContent(mMarquees);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView() called ");
        View view = inflater.inflate(R.layout.fragment_door64, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onViewCreated() called with");
        initializeTitle();
        initializeDripInfo();
        initializeDoctorInfo();
        initializePatientInfo();
        isPrepared=true;
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume() called");

    }
    /**
     * 初始化标题
     * 主要是当前日期
     */
    public void initializeTitle() {
//        mTvTitle.setText();
        setTitle(SharePreferenceUtil.getTitle(getActivity()));
        mHandler.obtainMessage(RE_KEEP_TIME).sendToTarget();
//        checkTime();
    }

    /**
     * 每个一分钟刷新一次时间，为了在整点同步会重新发送延迟消息
     */
    public void checkTime() {
        //格式化当前时间为 年 月 日  星期几，比如2016年8月10日 星期三
        Calendar calendar = Calendar.getInstance();
        long current=calendar.getTimeInMillis();
        String date_week = dateFormat.format(current);
        //android 这个格式化方法中间没有空格，不能split
        mTvDate.setText(date_week.substring(0, date_week.length() - 3));
        mTvWeekday.setText(date_week.substring(date_week.length() - 3));
        String time = mSimpleDateFormat.format(current);
        mTvTime.setText(time);
        int current_second=calendar.get(Calendar.SECOND);
        mHandler.removeMessages(KEEP_TIME);
        //整点更新时钟
        mHandler.sendEmptyMessageDelayed(KEEP_TIME,(60-current_second)*1000);
    }
    //设置标题
    public void updateTitle(@NonNull  PushDoorTitle pushDoorTitle) {
        setTitle(pushDoorTitle.getDepartname());
            PushDoorTitle.WatchTime watchtime = pushDoorTitle.getWatchtime();
            if (watchtime != null) {
                mTvVisitPeriodOne.setText(watchtime.getMorning());
                mTvVisitPeriodTwo.setText(watchtime.getNoon());
                mTvVisitPeriodThree.setText(watchtime.getNight());
            }

    }

    /**
     *
     * @param title 标题文字，区分中文和其他，显示的时候字体大小及边距区别处理
     */
    private void setTitle(String title) {
        if (null == title) {
            title = "";
        }
        boolean chineseCharacter = Common.isChineseCharacter(title);
        mTvTitle.setText(title);
        if (chineseCharacter) {
            LogUtil.d(TAG, "有中文");
            mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,76);
            mTvTitle.setIncludeFontPadding(true);
        }else{
            LogUtil.d(TAG, "没有中文");
            mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,88);
            //去除字体边距
            mTvTitle.setIncludeFontPadding(false);
        }

    }

    /**
     * 显示医生信息
     */
    private void initializeDoctorInfo() {
        mDoctorAdapter = new DoctorAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);
        mRvDoctor.addItemDecoration(new InsetDecoration(getContext()));
        mRvDoctor.setLayoutManager(gridLayoutManager);
        mRvDoctor.setAdapter(mDoctorAdapter);
    }

    //医生和护士的数据
    private List<Person> mPersonList=new CopyOnWriteArrayList<>();

    //医生和护士数据是分开的，每次都发，医生数据在前，护士在后，更新的逻辑是先清空，再添加
    public void updateDoctor(DoctorInfo doctorInfo) {
        //先移除所有医生
        LogUtil.d(TAG, "更新医生");
        removePerson(1);
        if (doctorInfo != null) {
            List<DoctorInfo.Doctor> doctorlist = doctorInfo.getDoctorlist();
            if (doctorlist != null&&doctorlist.size()>0) {
                if (mViewSwitchDripAndDoctor.getCurrentView().getId() != R.id.rv_doctor) {
                    mViewSwitchDripAndDoctor.showNext();
                    mTvInfo.setText(R.string.doctor_info);
                    //如果输液有信息，过段时间切换到输液信息
                    if (mDripAdapter!=null&&mDripAdapter.getItemCount()>0) {
                        mHandler.sendEmptyMessageDelayed(DRIP_DOCTOR_SWITCH, DRIP_DOCTOR_SWITCH_INTERVAL);
                    }
                }
                mPersonList.addAll(doctorlist);
//                mDoctorAdapter.onDataChange(mPersonList);
            }
        }
    }
    //收到更新，先清空护士信息，再添加
    public void updateNurse(NurseInfo nurseInfo ) {
        LogUtil.d(TAG, "更新护士");
       removePerson(2);
        if (nurseInfo != null) {
            List<NurseInfo.Nurse> nurselist = nurseInfo.getNurselist();
            if (nurselist != null&&nurselist.size()>0) {
                if (mViewSwitchDripAndDoctor.getCurrentView().getId() != R.id.rv_doctor) {
                    mViewSwitchDripAndDoctor.showNext();
                    mTvInfo.setText(R.string.doctor_info);
                    //如果输液有信息，过段时间切换到输液信息
                    if (mDripAdapter!=null&&mDripAdapter.getItemCount()>0) {
                        mHandler.sendEmptyMessageDelayed(DRIP_DOCTOR_SWITCH, DRIP_DOCTOR_SWITCH_INTERVAL);
                    }
                }
                mPersonList.addAll(nurselist);
            }
        }
//        LogUtil.d(TAG, mPersonList.toString());
        mDoctorAdapter.onDataChange(mPersonList);
    }

    //后台发送数据为空时，清空对应数据
    private void removePerson(int flag) {
        for (Person person : mPersonList) {
            if (person.getFlag() == flag) {
                mPersonList.remove(person);
            }
        }
    }
    /**
     * 初始化输液信息
     */
    private void initializeDripInfo() {
        mDripAdapter = new DripAdapter(getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);
        mRvDrip.addItemDecoration(new InsetDecoration(getContext()));
        mRvDrip.setLayoutManager(gridLayoutManager);
        mRvDrip.setAdapter(mDripAdapter);
        //数据更新时recyclerView 有默认动画，导致闪屏,所以取消掉
        ((SimpleItemAnimator) mRvDrip.getItemAnimator()).setSupportsChangeAnimations(false);

    }

    /**
     * 开始输液信息
     * 必须切换到可见
     */
    public void startDrip( DripInfo dripInfo) {
        if (dripInfo != null) {
            List<DripInfo.Infusionwarnings> dripInfos = dripInfo.getInfusionwarnings();
            if (dripInfos != null&&dripInfos.size()>0) {
                LogUtil.d(TAG, "update dripInfo:" + dripInfos);
                for (DripInfo.Infusionwarnings drip : dripInfos) {
                    drip.initilize(mSimpleDateFormat.format(drip.getStart()));
                    //此时剩余时间就是总时间，涉及输液袋的状态
                    drip.setTotal(drip.getLeft());
                }
                if (mViewSwitchDripAndDoctor.getCurrentView().getId() != R.id.rv_drip) {
                    mViewSwitchDripAndDoctor.showNext();
                    mTvInfo.setText(R.string.drip_info);
                }
                //先移除之前的消息
                mHandler.removeMessages(DRIP_UPDATE);
                mDripAdapter.onDateChange(dripInfos);
                mHandler.sendEmptyMessageDelayed(DRIP_UPDATE, DRIP_UPDATE_INTERVAL);
            }else{
                clearDrip();
                LogUtil.d(TAG, "没有输液信息 显示医生信息");
                if (mDoctorAdapter != null && mDoctorAdapter.getItemCount() > 0) {
                    if (mViewSwitchDripAndDoctor.getCurrentView().getId() != R.id.rv_doctor) {
                        LogUtil.d(TAG, "change to doctor view");
                        mViewSwitchDripAndDoctor.showNext();
                        mTvInfo.setText(R.string.doctor_info);
                    }
                }
            }
        }else{
           clearDrip();
        }
    }

    //清空输液信息
    public void clearDrip() {
        if (mDripAdapter != null) {
            mDripAdapter.clear();
        }
    }

    /**
     * 停止输液 移除对应床号的输液提醒
     * @param bedno
     */
    public void stopDrip(String bedno) {
        if (!TextUtils.isEmpty(bedno)) {
            mDripAdapter.onDateRemoved(bedno);
//            DoorScreenDataBase.getInstance(getActivity()).deleteDripInfo(bedno);
        }
    }
    //输液全部结束后移除消息
    public void terminateDrip() {
        mHandler.removeMessages(DRIP_UPDATE);
    }

    //所有的输液信息被移除
    public void stopAllDrip() {
        terminateDrip();
        if (mViewSwitchDripAndDoctor.getCurrentView().getId() == R.id.rv_drip) {
            if (mDoctorAdapter != null && mDoctorAdapter.getItemCount() > 0) {
                mViewSwitchDripAndDoctor.showNext();
                mTvInfo.setText(R.string.doctor_info);
            }
        }
    }
    /*
   * 病人信息*/
    private void initializePatientInfo() {
        mPatientAdapter = new PatientAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRvPatients.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL, R.drawable.divider_vertical));
        mRvPatients.setLayoutManager(linearLayoutManager);
        mRvPatients.setAdapter(mPatientAdapter);
    }

    public void updatePatient(PatientInfo patientInfo) {
        if (patientInfo != null) {
            List<PatientInfo.Patient> patientlist = patientInfo.getPatientlist();
            if (patientlist != null) {
                LogUtil.d(TAG, "patientInfo:" + patientlist);
                mPatientAdapter.onDateChange(patientlist);
            }
        }
    }


    public void updateMarquee(String paramMarquee) {
        List<String> content = DoorScreenDataBase.getInstance(getActivity()).queryMarquee(paramMarquee);
        mMarquees.clear();
        mMarquees.addAll(content);
        if (isVisible&&mMarquees.size()>0) {
            mMarqueeView.setContent(content);
        }
    }

    /**
     * 停止跑马灯
     * @param
     */
    public void stopMarquee() {
        if (isVisible) {
            mMarqueeView.terminate();
        }
    }

    /**
     * 呼叫 ，切换到此界面
     * @param
     */
    public void patientCall( List<CallInfo.CallMessage> callmessage) {
        if (callmessage != null && callmessage.size() > 0) {
            onPatientCall(callmessage);
        } else {
            onPatientEndCall();
        }
    }

    private void onPatientCall(@NonNull List<CallInfo.CallMessage> callmessage) {
        StringBuilder sb = new StringBuilder();
        List<String> bednos=new ArrayList<>();
        for (CallInfo.CallMessage callMessage : callmessage) {
            sb.append(callMessage.getMsg()).append("\n");
            bednos.add(callMessage.getBedno());
        }
        mTvPatientCall.setText(sb.toString());
        if ((mViewSwitchCallAndVisit.getCurrentView().getId()!= R.id.ll_call_info)) {
            mViewSwitchCallAndVisit.showNext();
        }
        mPatientAdapter.onPatientCall(bednos);
    }

    private void onPatientEndCall() {
        if ((mViewSwitchCallAndVisit.getCurrentView().getId()!= R.id.ll_visit_info)) {
            mViewSwitchCallAndVisit.showNext();
            mPatientAdapter.onPatientCall(null);
        }
    }


    public void moveToCallingPatient(int i) {
        mRvPatients.scrollToPosition(i);
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop() called");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
        ButterKnife.unbind(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }


}

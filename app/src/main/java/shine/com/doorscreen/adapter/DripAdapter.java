package shine.com.doorscreen.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import shine.com.doorscreen.R;
import shine.com.doorscreen.activity.MainActivity;
import shine.com.doorscreen.entity.DripInfo;

/**
 * Created by Administrator on 2016/8/16.
 * 输液情况适配器
 */
public class DripAdapter extends RecyclerView.Adapter<DripAdapter.DripHolder> {
    private static final String TAG = "DripAdapter";
    private CopyOnWriteArrayList<DripInfo.Infusionwarnings> mWarningList;
    private Context mContext;

    public DripAdapter(Context context) {
        mContext = context;
        mWarningList = new CopyOnWriteArrayList<>();
    }

    public void onDateChange(List<DripInfo.Infusionwarnings> warningList) {
        if (warningList != null && warningList.size() > 0) {
            mWarningList.clear();
            mWarningList.addAll(warningList);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        mWarningList.clear();
        notifyDataSetChanged();
    }
    public void onDateRemoved(@NonNull String bedno) {
        for (int i = 0; i < mWarningList.size(); i++) {
            if (bedno.equals(mWarningList.get(i).getBedno())) {
                mWarningList.remove(mWarningList.get(i));
                notifyItemRemoved(i);
            }
        }
        if (mWarningList.size() == 0) {
            LocalBroadcastManager.getInstance(mContext).
                    sendBroadcast(MainActivity.newIntent(MainActivity.STOP_ALL_DRIP, ""));

        }

    }

    public void update() {
        int count=0;
        for (int i = 0; i < mWarningList.size(); i++) {
            DripInfo.Infusionwarnings warningBean = mWarningList.get(i);
            if (warningBean.getLeft() > 0) {
                warningBean.setCurrentNumber();
                warningBean.countDown();
                notifyItemChanged(i);
            }else{
                count++;
            }
        }
        //表示输液全部结束
        if (count == mWarningList.size()) {
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(MainActivity.newIntent(MainActivity.DRIP_DONE,""));
        }
    }

    @Override
    public DripHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drip, parent, false);
        return new DripHolder(view,mContext);
    }

    @Override
    public void onBindViewHolder(DripHolder holder, int position) {
        DripInfo.Infusionwarnings warning = mWarningList.get(position);
        holder.bind(warning);
    }

    @Override
    public int getItemCount() {
        return mWarningList.size();
    }

    static class DripHolder extends RecyclerView.ViewHolder implements ViewSwitcher.ViewFactory {
        @Bind(R.id.tv_drip_title)
        TextView mTvDripTitle;
        @Bind(R.id.tv_time_left)
        TextView mTvTimeLeft;
        @Bind(R.id.textSwitchLeft)
        TextSwitcher mTextSwitchLeft;
        @Bind(R.id.textSwitchMiddle)
        TextSwitcher mTextSwitchMiddle;
        @Bind(R.id.textSwitchRight)
        TextSwitcher mTextSwitchRight;
        @Bind(R.id.ll_time_board)
        LinearLayout mLlTimeBoard;
        @Bind(R.id.iv_drip_package)
        ImageView mIvDripPackage;
        @Bind(R.id.iv_water_drip)
        ImageView mIvWaterDrip;
        @Bind(R.id.tv_drip_info)
        TextView mTvDripInfo;
        private Context context;

         DripHolder(View itemView,Context context) {
            super(itemView);
            this.context = context;
            ButterKnife.bind(this, itemView);
            mTextSwitchLeft.setFactory(this);
            mTextSwitchRight.setFactory(this);
            mTextSwitchMiddle.setFactory(this);
        }

        public void bind(DripInfo.Infusionwarnings warningBean) {
            if (warningBean.getLeft() <= 3) {
                mTvDripTitle.setSelected(true);
                mLlTimeBoard.setSelected(true);
                mTvTimeLeft.setSelected(true);
            }else{
                mTvDripTitle.setSelected(false);
                mLlTimeBoard.setSelected(false);
                mTvTimeLeft.setSelected(false);
            }
            mTvDripTitle.setText(warningBean.getBedno());
            mTvDripInfo.setText(String.format(Locale.CHINA,"开始时间\n%s\n%s滴/分钟",warningBean.getBegin(),
                    warningBean.getSpeed()));
            int dripPackageResourceId=warningBean.getCurrentDripPackage();
            if (dripPackageResourceId != -1) {
                mIvDripPackage.setImageDrawable(context.getResources().getDrawable(dripPackageResourceId));
            }

            if (warningBean.getCurrent_bai() == warningBean.getNext_bai()) {
                mTextSwitchLeft.setCurrentText(String.valueOf(warningBean.getCurrent_bai()));
            } else {
                mTextSwitchLeft.setText(String.valueOf(warningBean.getNext_bai()));
            }
            if (warningBean.getCurrent_shi()==warningBean.getNext_shi()) {
                mTextSwitchMiddle.setCurrentText(String.valueOf(warningBean.getCurrent_shi()));
            }else{
                mTextSwitchMiddle.setText(String.valueOf(warningBean.getNext_shi()));
            }
            //个位数的动画出现跳跃，显示的是x，出去的是X+1，进来的是x-1，所以先设置当前量，再切换
            mTextSwitchRight.setCurrentText(String.valueOf(warningBean.getCurrent_ge()));
            if (warningBean.getLeft() > 0) {
                mTextSwitchRight.setText(String.valueOf(warningBean.getNext_ge()));
                if (mIvWaterDrip.getAnimation() == null) {
                    mIvWaterDrip.setAnimation(getAnimation(1500));
                }
            }else{
                mTextSwitchRight.setText("0");
                mIvWaterDrip.clearAnimation();
                mIvWaterDrip.setVisibility(View.INVISIBLE);
            }

        }

         TranslateAnimation getAnimation(int duration) {
            TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 80);
            animation.setDuration(duration);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setFillAfter(false);
            animation.setRepeatCount(-1);
            return animation;
        }

        @Override
        public View makeView() {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new TextSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(22f);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextColor(context.getResources().getColor(android.R.color.white));
            return textView;
        }
    }



}

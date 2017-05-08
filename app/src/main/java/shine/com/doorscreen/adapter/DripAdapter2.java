package shine.com.doorscreen.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import shine.com.doorscreen.R;
import shine.com.doorscreen.entity.PushDoorInfo;

/**
 * Created by Administrator on 2016/8/16.
 * 输液情况适配器
 */
@Deprecated
public class DripAdapter2 extends RecyclerView.Adapter<DripAdapter2.DripHolder> {
    private static final String TAG = "DripAdapter";
    private List<PushDoorInfo.WarningBean> mWarningList;
    private Context mContext;

    public DripAdapter2(Context context) {
        mContext = context;
        mWarningList = new ArrayList<>();
    }

    public void onDateChange(List<PushDoorInfo.WarningBean> warningList) {
        if (warningList != null && warningList.size() > 0) {
            mWarningList.addAll(warningList);
            notifyDataSetChanged();
        }
    }

    public void update() {
        for (int i = 0; i < mWarningList.size(); i++) {
            PushDoorInfo.WarningBean warningBean = mWarningList.get(i);
            if (warningBean.getLeft() > 0) {
                warningBean.setCurrentNumber();
                warningBean.countDown();
                notifyItemChanged(i);
            }
        }
    }

    @Override
    public DripHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drip2, parent, false);
        return new DripHolder(view,mContext);
    }

    @Override
    public void onBindViewHolder(DripHolder holder, int position) {
        PushDoorInfo.WarningBean warning = mWarningList.get(position);
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

        public DripHolder(View itemView,Context context) {
            super(itemView);
            this.context = context;
            ButterKnife.bind(this, itemView);
            mTextSwitchLeft.setFactory(this);
            mTextSwitchRight.setFactory(this);
            mTextSwitchMiddle.setFactory(this);
        }

        public void bind(PushDoorInfo.WarningBean warningBean) {
            Log.d(TAG, warningBean.toString()+"positon"+getAdapterPosition());
            mTvDripTitle.setText(warningBean.getBedno());
            int dripPackageResourceId=warningBean.getCurrentDripPackage();
            if (dripPackageResourceId != -1) {
                mIvDripPackage.setImageDrawable(context.getResources().getDrawable(dripPackageResourceId));
            }
           /* TextView textView= (TextView) mTextSwitchLeft.getCurrentView();
            Log.d(TAG, "textView.mTextSwitchLeft():" + textView.getText());
             TextView textView2= (TextView) mTextSwitchMiddle.getCurrentView();
            Log.d(TAG, "textView.mTextSwitchMiddle():" + textView2.getText());
             TextView textView3= (TextView) mTextSwitchRight.getCurrentView();
            Log.d(TAG, "textView.mTextSwitchRight():" + textView3.getText());*/

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
                    mIvWaterDrip.setAnimation(getAnimation(1000+5 * warningBean.getLeft()));
                }
            }else{
                mTextSwitchRight.setText("0");
                mIvWaterDrip.clearAnimation();
                mIvWaterDrip.setVisibility(View.INVISIBLE);
            }

        }

        public TranslateAnimation getAnimation(int duration) {
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
            textView.setTextSize(28f);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextColor(context.getResources().getColor(android.R.color.white));
            return textView;
        }
    }



}

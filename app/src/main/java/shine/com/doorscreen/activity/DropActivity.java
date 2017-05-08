package shine.com.doorscreen.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import shine.com.doorscreen.R;
import shine.com.doorscreen.customview.DripView;

public class DropActivity extends AppCompatActivity {
    private static final String TAG = "DropActivity";
    @Bind(R.id.iv_drip_package)
    ImageView mIvDripPackage;
  /*  @Bind(R.id.view_water_drop)
    View mViewWaterDrop;*/
    @Bind(R.id.dripView)
    DripView mDripView;
    boolean stop=false;
    @SuppressWarnings("handlerleak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mDripView.form();
            if (!stop) {
                sendEmptyMessageDelayed(0, 1000);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop);
        ButterKnife.bind(this);
        mHandler.sendEmptyMessageDelayed(0, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop=true;
        mHandler.removeCallbacksAndMessages(null);
    }

    @OnClick(R.id.iv_drip_package)
    public void onClick() {
        finish();
    }
}

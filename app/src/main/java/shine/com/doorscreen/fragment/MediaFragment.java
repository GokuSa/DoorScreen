package shine.com.doorscreen.fragment;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.VideoView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import shine.com.doorscreen.R;
import shine.com.doorscreen.database.DoorScreenDataBase;
import shine.com.doorscreen.entity.Elements;
import shine.com.doorscreen.util.LogUtil;

/**
 * A simple {@link Fragment} subclass.
 *
 * 视频和图片轮播
 */
public class MediaFragment extends Fragment implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, ViewSwitcher.ViewFactory {
    private static final String TAG = "MediaFragment";
    private static final int NEXT_PICTURE = 0;
    @Bind(R.id.videoView)
    VideoView mVideoView;
    @Bind(R.id.imageSwitch)
    ImageSwitcher mImageSwitch;
    @Bind(R.id.viewSwitch)
    ViewSwitcher mViewSwitch;
    /**
     * 需要播放的视频文件
     */
    private List<File> mVideoPath = new ArrayList<>();
    /**
     * 展示的图片文件
     */
    private List<File> mImagePath=new ArrayList<>();

    /**
     * 当前播放视频文件的索引，用来循环播放
     */
    private int mVideoIndex = 0;
    /**
     * 当前展示图片的索引
     */
    private int mPictureIndex =0;

    //頁面是否可見
    private boolean isVisible=false;
    //控件是否初始化
    private boolean isPrepared=false;
    /**
     * 图片切换的间隙，后台有传数据，目前本地写死
     */
    private static final long INTERVAL = 30 * 1000;
    @SuppressWarnings("handlerleak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //播放下一张图片
                case NEXT_PICTURE:
                    //如果是最后一张
                    if (mPictureIndex + 1== mImagePath.size()) {
                        //并且有视频
                        if (mVideoPath.size() > 0) {
                            if (mViewSwitch.getCurrentView().getId() != R.id.videoView) {
                                LogUtil.d(TAG, "switch to video");
                                mViewSwitch.showNext();
                            }
                            mPictureIndex=0;
                            showVideo();
                        }else{
                            //没有视频，从头播放图片
                            mPictureIndex=0;
                            showPicture();
                        }
                    }else{
                        mPictureIndex++;
                        showPicture();
                    }
                    break;
            }
        }
    };
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.d(TAG, "setUserVisibleHint: "+isVisibleToUser);
        isVisible=isVisibleToUser;
        if (isVisibleToUser) {
           playMedia();
        }else{
            onInVisible();
        }
    }

    private void onInVisible() {
        if (isPrepared) {
            LogUtil.d(TAG,"停止多媒体的播放");
            mVideoView.stopPlayback();
            mHandler.removeMessages(NEXT_PICTURE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_media, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.d(TAG, "onViewCreated() called with: " );
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);
        mImageSwitch.setFactory(this);
        isPrepared=true;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.d(TAG, "onActivityCreated: ");
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
//        playMedia();
    }

    /**
     * 播放视频
     * 如果更新列表为空，此时本地文件都被删除，更新后的列表就为空
     * 所以先判断视频文件是否为空
     */
    private void showVideo() {
        if (mVideoPath.size() > 0) {
            File file = mVideoPath.get(mVideoIndex);
            LogUtil.d(TAG, "file:" + file.exists() + "----file:" + file.getPath());
            mVideoView.setVideoURI(Uri.fromFile(file));
            mVideoView.start();
        }
    }

    private void showPicture() {
        if (mImagePath.size() > 0) {
            File file = mImagePath.get(mPictureIndex);
            ImageView imageView = (ImageView) mImageSwitch.getNextView();
            LogUtil.d(TAG, file.getAbsolutePath());
            Glide.with(this).load(file).into(imageView);
            mImageSwitch.showNext();
            mHandler.sendEmptyMessageDelayed(NEXT_PICTURE, INTERVAL);
        }
    }

    /**
     * 1.后台每分钟检查一次多媒体素材播放时间有效性，并筛选出播单
     * 2.有变更会调用此方法 从数据库重新加载本地文件
     * 3.播放
     */
    public void updateMedia(String ids) {
        LogUtil.d(TAG, "updateMedia: ");
        mPictureIndex=0;
        mVideoIndex=0;
        if (isVisible && isPrepared) {
            //如果当前是视频，停止
            if (mViewSwitch.getCurrentView().getId()== R.id.videoView) {
                mVideoView.stopPlayback();
            }else{
                //如果是图片，停止切换
                mHandler.removeMessages(NEXT_PICTURE);
            }
        }
        getLocalMedia(ids);
        playMedia();
    }

    /**
     * @param ids 播单eg：3，4
     * 根据播单从数据库获取宣教信息
     */
    private void getLocalMedia(String ids) {
        List<Elements> elementsList = DoorScreenDataBase.getInstance(getActivity()).queryMedia(ids);
        mVideoPath.clear();
        mImagePath.clear();
        for (Elements elements : elementsList) {
            File file = new File(elements.getPath());
            if (elements.getType() == 1) {
                //播单可能会有重复的素材，不重复加
                if (file.exists() && !mVideoPath.contains(file)) {
                    mVideoPath.add(file);
                }
            } else if (elements.getType() == 2) {
                if (file.exists() && !mImagePath.contains(file)) {
                    mImagePath.add(file);
                }
            }
        }

    }
    public void playMedia() {
        if (isVisible && isPrepared) {
            LogUtil.d(TAG, "播放多媒体");
            //如果有视频文件就优先播
            if (mVideoPath.size() > 0) {
                //如果当前不是视频视图就先切换一下
                if (mViewSwitch.getCurrentView().getId()!= R.id.videoView) {
                    mViewSwitch.showNext();
                }
                showVideo();
            }else if (mImagePath.size() > 0) {
                if (mViewSwitch.getCurrentView().getId()!= R.id.imageSwitch) {
                    mViewSwitch.showNext();
                }
                showPicture();
            }
        }
    }

    /**
     * 播放完成，切换到下一个视频
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        LogUtil.d(TAG, "on completion");
        //如果是最后一个视频
        if (mVideoIndex +1==mVideoPath.size()) {
            //并且图片不为空
            if (mImagePath.size()>0) {
                //如果当前是视频视图，应该是视频视图，先判断一下
                if (mViewSwitch.getCurrentView().getId() != R.id.imageSwitch) {
                    LogUtil.d(TAG, "switch to image");
                    mVideoView.stopPlayback();
                    mViewSwitch.showNext();
                }
                mVideoIndex=0;
                showPicture();
            }else{
                //图片为空,从头继续播放视频
                mVideoIndex=0;
                showVideo();
            }
        }else{
            //播放下一个视频
            mVideoIndex++;
            showVideo();
        }

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
        LogUtil.d(TAG, "onDestroyView: ");
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        LogUtil.d(TAG, "onLowMemory: ");
        super.onLowMemory();
    }

    @Override
    public View makeView() {
        ImageView imageView = new ImageView(getActivity());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(Gallery.LayoutParams.MATCH_PARENT,
                Gallery.LayoutParams.MATCH_PARENT));
        return imageView;
    }

    /**
     * 播放错误处理
     * @return 返回true 表示不弹出错误提示对话框
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtil.d(TAG, "Play Error:::onError called");
        LogUtil.e(TAG, "what:" + what + "---extra:" + extra);
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                LogUtil.d(TAG, "mVideoPath.get(mVideoIndex):" + mVideoPath.get(mVideoIndex));
                LogUtil.e(TAG,"Play Error::: MEDIA_ERROR_SERVER_DIED");
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                LogUtil.e(TAG,"Play Error::: MEDIA_ERROR_UNKNOWN");
                break;
        }
        return true;
    }
}

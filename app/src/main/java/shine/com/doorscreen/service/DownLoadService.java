package shine.com.doorscreen.service;

import android.app.IntentService;
import android.content.Intent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.shine.curl.JniCurl;
import shine.com.doorscreen.activity.MainActivity;
import shine.com.doorscreen.database.DoorScreenDataBase;
import shine.com.doorscreen.entity.Elements;
import shine.com.doorscreen.util.LogUtil;


/**
 * Created by Administrator on 2016/6/29.
 * 远程多媒体文件下载
 * 67 ftp用户名或者密码错误
 * 7 FtpServer未启动或终端FTP服务器（Ip地址、端口号）设置错误
 * 78 元素找不到，如果只是远程文件不存在的原因
 * 23 如果是调用finish导致中断
 * -1 ftp已停止
 * -2 下载空间满的了
 * 正确下载路径ftp://10.0.1.64:21/video.wmv
 * <p>
 * 6 传入的curl 地址格式错误，shine_av_stream://@:11128(172.168.2.21/4360/0)
 * 9传入的curl 地址格式错误，如/shine_av_stream://@:11128(172.168.2.21/4360/0)
 * 等于28 有一种情况是下载速度变化 现在停止  所以需要重新下载
 * 下载空间满的了返回码是 -2
 * 当终端断电的时候，文件有可能返回23，这时候要删除掉该文件
 */
public class DownLoadService extends IntentService {
    private static final String TAG = "DownLoadService";
    public DownLoadService() {
        super("DownLoadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtil.d(TAG, "onHandleIntent() called with: " + "intent = [" + intent + "]");
        if (intent != null) {
            ArrayList<Elements> elementsToLoad=(ArrayList<Elements>) intent.getSerializableExtra("elements");
            int id = intent.getIntExtra("id", -1);
            //批量插入多媒体数据
            DoorScreenDataBase.getInstance(this).bulkInsertMedia(id,elementsToLoad);
           downLoad(elementsToLoad);

        }
    }

    //根据id从本地数据库获取已存在的文件集合，和后台消息比较，没有则更新，有则从a中移除记录，最后剩下的就是要删除的文件记录
    public int downLoad(List<Elements> elements) {
        int success = 1;
        try {
            // 初始化curl init 一次
            JniCurl  curl = new JniCurl();
            JniCurl.useEPSV = true;
            int init = curl.initOnce();
            if (init == 0) {
                // 初始化失败了。
                return 0;
            }
            curl.setOnlyHeader(0);
            for (Elements element : elements) {
                File file = new File(element.getPath());
                //如果本地存在就不用下载
                if (file.exists()) {
                    DoorScreenDataBase.getInstance(this).activateMediaStatus(element.getId());
                    continue;
                }
                curl.setUrl(element.getSrc());
                LogUtil.d(TAG, element.getSrc()+"--"+element.getPath());
                curl.setLocalPath(element.getPath());
                curl.setRange(0);
                int start = curl.start();
                LogUtil.d(TAG, "start:" + start);
                switch (start) {
                    case 0:
                        // 更改数据库状态与实际下载对应
                        DoorScreenDataBase.getInstance(this).activateMediaStatus(element.getId());
                        LogUtil.d(TAG, "下载成功");
                        break;
                    default:
                        if (file.exists()) {
                            file.delete();
                        }
                        break;
                }
            }
            curl.finishOnce();
            //通知后台重新检索
            startService(DoorService.newIntent(this, MainActivity.DOWNLOAD_DONE, ""));
        } catch (Exception e) {
            LogUtil.d(TAG, e.toString());
        }
        return success;
    }

}

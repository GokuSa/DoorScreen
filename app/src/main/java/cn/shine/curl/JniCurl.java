package cn.shine.curl;

import android.util.Log;

import shine.com.doorscreen.app.AppEntrance;
import shine.com.doorscreen.util.IniReaderNoSection;


public class JniCurl {

	private int mSize;
	public static boolean useEPSV = true;

	static {
		System.load("/system/lib/libcurl.so");
		System.load("/system/lib/libjni_curl.so");
	}

	// 初始化cURL，返回0失败，1成功
	native public int init();

	native public void setUrl(String url);

	// 设置用户名和密码，格式【用户名:密码】，如shine:shine
	native public void setUserPwd(String userPwd);

	// 设置最大下载速度
	native public void setMaxSpeed(int maxSpeed);

	// 设置下载字节偏移量
	native public void setRange(long range);

	// 设置是否获取远程文件头信息,flag：1获取，0不获取
	native public void setOnlyHeader(int flag);

	// 设置本地文件路径
	native public void setLocalPath(String path);

	// 设置从调用start()方法开始后的超时时间，单位：秒
	native public void setTimeOut(int second);

	// 设置一个长整形数，控制1s传送多少字节
	native public void setLowSpeedLimit(int count);

	// 设置一个长整形数，控制多少秒传送setLowSpeedLimit规定的字节数
	native public void setLowSpeedTime(int second);

	// 未知用途,预留接口
	native public void setFtpResponseTimeout(int second);

	// 设置连接服务器的超时时间,单位:秒
	native public void setConnectTimeout(int second);

	// 设置等待服务器连接回libcurl的主动FTP连接时使用的超時時間,单位:秒
	native public void setAcceptTimeout(int second);

	// 设置是否可以获取远程文件时间
	native public void setFileTimeable(boolean flag);

	// 设置自定义命令，如ftp获取目录列表命令“NLST”
	native public void setCustomQuest(String command);

	// 设置传输编码
	native public void setToNetEncode(String encode);

	// 设置传输编码
	native public void setFromNetEncode(String encode);

	// 设置是否使用EPSV
	native public void setUseEPSV(boolean flag);

	// 设置是否使用EPRT
	native public void setUseEPRT(boolean flag);

	// 设置是否使用主动模式
	native public void setFtpPort(String string);

	// ==================上传文件设置==================//

	// 设置是否要开启上传模式
	native public void setUploadMode(boolean flag);

	// 设置要上传的文件的本地路径
	native public void setUploadFile(String localPath);

	// 设置要上传的文件的大小
	native public void setUploadFileSize(long size);

	// 设置是否自动创建不存的目录
	native public void setAutoCreateDir(boolean flag);

	// ===============================================//

	// 获取远程文件大小
	native public long getRemoteFileSize();

	// 获取远程文件的修改时间
	native public int getRemoteFileTime();

	// 获取最后一次使用的url
	native public String getLastUsedUrl();

	// 开始下载，返回0成功，非0失败
	native public int start();

	// 暂停
	native public int pause();

	// 恢复暂停
	native public int restart();

	// 强制停止下载
	native public void stop();

	// 清除cURL设置的所有参数,使其和刚调用init()函数时一样
	native public void reset();

	// 清除cURL
	@Deprecated
	native public void clean();

	// 关闭cURL
	native public void finish();

	// 只允许关闭一次
	public void finishOnce() {
		Log.i("info", "调用了finish");
		this.finish();
	}

	private int timeout = 500;// 设置超时时间，500s
	public static int speed = 2 * 1024 * 1024;// 最大下载速度，2M
//  public static int speed = 30*1024*1024;
	/**
	 * 返回值非0,表示初始化成功
	 * 
	 * @return
	 */
	public int initOnce() {
		Log.i("info", "调用了init");
		int init = this.init();
		IniReaderNoSection inir = new IniReaderNoSection(AppEntrance.ETHERNET_PATH);
		String ftppasswd = inir.getValue("ftppasswd");
		String ftpusr = inir.getValue("ftpusr");
		if (0 != init) {
			String pwd = ftpusr+":"+ftppasswd;
			setUserPwd(pwd);
			setConnectTimeout(20);
			// setTimeOut(timeout); 注销此句，改进下载大文件下不完的错误
			// 这两句用来解决ftp长时间不传输数据的情况
			// 效果:20秒内最少要传输1*20字节的数据,否则结束下载
			setLowSpeedLimit(1);
			setLowSpeedTime(20);

			//设置最大传输速度
			setMaxSpeed(speed);

			//修复公网ip下载不了
			setUseEPRT(false);
			setUseEPSV(useEPSV);
			
		} else {
			Log.e("info", "初始化失败, code: " + init);
			// 初始化失败了，需要销毁资源吗？
		}
		return init;
	}
}

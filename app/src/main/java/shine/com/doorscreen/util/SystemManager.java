package shine.com.doorscreen.util;
import android.os.Build;
import android.util.Log;

import java.io.DataOutputStream;
public class SystemManager 
{
	//给USB读写权限
	public void chmodUSB(){
//	
//	if(uFile.exists()&&uFile.isDirectory()){
//		canW=uFile.canWrite();
//		if(canW){
//		File[] ff=uFile.listFiles();
//		if(ff!=null&&ff.length>0){
//			for (File file : ff) {
//				canW=file.canWrite();
//				if(!canW){
//					break;
//				}
//			}
//		}
//		}
//	}
//	Log.i("info", "USB权限1,canW:"+canW);
//	
//	// 超时时间 s;
//	if (!canW) {
//		
//	}
		RootCommand("chmod -R 777 /dev/bus/usb");

	Log.i("info", "加USB权限");
	}
    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     * @param command 命令：String apkRoot="chmod 777 "+getPackageCodePath(); RootCommand(apkRoot);
     * @return 应用程序是/否获取Root权限
     */
    public  boolean RootCommand(String command)
    {

        // 918上处理与6369,801上不同
        if (Build.VERSION.SDK_INT == 18) {
            SuClient client = new SuClient();
            client.init(null);
            boolean flg = client.execCMD(command);
            client.close();
            return flg;
        }

        Process process = null;
        DataOutputStream os = null;
        try
        {
            process = Runtime.getRuntime().exec("shinesu");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e)
        {
            Log.i("info", "ROOT REE" + e.getMessage());
            return false;
        } finally
        {
            try
            {
                if (os != null)
                {
                    os.close();
                }
                process.destroy();
            } catch (Exception e)
            {
            }
        }
        return true;
    }
}
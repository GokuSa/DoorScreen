package shine.com.doorscreen.util;

//~--- JDK imports ------------------------------------------------------------

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 918上使用
 * 通过su服务，向盒子发送命令
 * @date        15/04/13
 * @author         姜春雨
 */
public class SuClient
{
    private static final String IP             = "127.0.0.1";
    private static final int    PORT           = 4757;
    private static final String PWD            = "SHINE_USER";
    private static final String NO_ERROR       = "0";
    private static final String HEAD_EXEC      = "EXEC_CMD|";
    private static final String HEAD_MOVE_FILE = "MOVE_FILE|";
    private InputStream         is;
    private OutputStream        os;
    private DataInputStream     dis;
    private DataOutputStream    dos;
    private Socket              s;
    private OnResultListener    mListener;

    public boolean init(OnResultListener listener) {
        mListener = listener;

        try {
            s   = new Socket(IP, PORT);
            is  = s.getInputStream();
            os  = s.getOutputStream();
            dos = new DataOutputStream(os);
            dis = new DataInputStream(is);
            dos.writeUTF(PWD);

            return getResult();
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }

        return false;
    }

    public boolean execCMD(String cmd) {
        try {
            dos.writeUTF(HEAD_EXEC + cmd);

            return getResult();
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }

        return false;
    }

    public boolean execCMD(String cmd, OnResultListener listener) {
        mListener = listener;

        try {
            dos.writeUTF(HEAD_EXEC + cmd);

            return getResult();
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }

        return false;
    }

    public boolean mountRW(String path) {
        String cmd = "mount -o remount,rw " + path;

        return execCMD(cmd);
    }

    public boolean mountRO(String path) {
        String cmd = "mount -o remount,ro " + path;

        return execCMD(cmd);
    }

    private boolean mkdir(String tar) {
        String mkdir = "mkdir -p " + tar.substring(0, tar.lastIndexOf("/") + 1);

        return execCMD(mkdir);
    }

    public boolean moveFile(String src, String tar, int rwx) {
        mkdir(tar);

        String cmd = "busybox cp " + src + " " + tar;

        execCMD(cmd);

        return execCMD("busybox chmod " + rwx + " " + tar);
    }

    public boolean moveFile(String src, String tar) {
        mkdir(tar);

        try {
            dos.writeUTF(HEAD_MOVE_FILE + src + "|" + tar);

            return getResult();
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }

        return false;
    }

    public boolean moveDir(String src, String tar) {
        boolean flag  = true;
        File    dir   = new File(src);
        File[]  files = dir.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                flag &= moveDir(file.getAbsolutePath(), tar + "/" + file.getName());
            } else {
                flag &= moveFile(file.getAbsolutePath(), tar + "/" + file.getName());
            }
        }

        return flag;
    }

    public boolean deleteFile(String src) {
        String cmd = "busybox rm -rf " + src;

        return execCMD(cmd);
    }

    private boolean getResult() throws IOException {
        boolean flag;

        if (!dis.readUTF().equals(NO_ERROR)) {
            flag = false;
        } else {
            flag = true;
        }

        if (mListener != null) {
            mListener.onResult(this, flag);
        }

        return flag;
    }

    public void close() {
        close(is);
        close(dis);
        close(os);
        close(dos);
        close(s);
    }

    private void close(Socket s) {
        if (s != null) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String readResult() {
        String result = "";
        if (is != null) {
            byte[] b = new byte[1024 * 1024];
            try {
                is.read(b);
                return new String(b, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public interface OnResultListener
    {
        public void onResult(SuClient client, boolean success);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com

package shine.com.doorscreen.app;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 * 32* 1024
 * 
 */
public class UpToShowClientSyncInfoRunnable implements Runnable {
	private static final int BUFFER_SIZE = 32 * 1024;
	private byte[] buffer = new byte[BUFFER_SIZE];

	public UpToShowClientSyncInfoRunnable() {}

	@Override
	public void run() {
		Log.d("UpToShowClientSyncInfoR", "check time");
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < 32768; i++) {
			if (i == 1 || i == 3) {
				str.append(":");
			} else {
				str.append("0");
			}
		}
		socketMethod(str.toString());
	}

	/**
	 * socket���ݣ�udp
	 * 
	 * @param str
	 */
	private void socketMethod(String str) {
		DatagramSocket udpSocket = null;
		DatagramPacket dataPacket = null;
		try {
			dataPacket = new DatagramPacket(buffer, BUFFER_SIZE);
			byte[] out = str.getBytes();
			dataPacket.setData(out);
			dataPacket.setLength(BUFFER_SIZE);
			dataPacket.setPort(5002);
			InetAddress broadcastAddr = InetAddress.getLocalHost();
			dataPacket.setAddress(broadcastAddr);
			udpSocket = new DatagramSocket();
			Log.d("myinfo", "udpSocket==��" + udpSocket);
			udpSocket.send(dataPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

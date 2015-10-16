package com.ayit.threads;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import android.util.Log;

/**
 * 发送图片的线程
 * 
 * @author Administrator
 * 
 */
public class SendBitampThread extends Thread {
	Socket socket;
	String address;
	byte[] data = null;

	// 将服务器的地址作为参数传入
	public SendBitampThread(String address, byte[] data) {
		this.address = address;
		this.data = data;
	}

	@Override
	public void run() {
		OutputStream out = null;
		try {
			socket = new Socket(address, 12346);
		} catch (UnknownHostException e) {
			Log.d("aaa",
					"SendDataThread.init() has UnknownHostException"
							+ e.getMessage());
		} catch (IOException e) {
			Log.d("aaa", "SendDataThread.init().IOException:" + e.getMessage());
		}
		if (socket != null) {
			try {
				out = socket.getOutputStream();
				out.write(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}

	}

}

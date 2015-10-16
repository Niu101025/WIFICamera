package com.ayit.threads;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.R.bool;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class ListenYuLanBitmapThread extends Thread {
	ServerSocket socket = null;

	public ListenYuLanBitmapThread(int port, Handler handler) {
		try {
			port = 12347;
			socket = new ServerSocket(port);
			this.handler = handler;
		} catch (IOException e) {
			Log.d("aaa", "ListenThread ServerSocket init() has exception");
		}

	}

	Handler handler;

	@Override
	public void run() {
		// Socket soc = null;
		InputStream is = null;
		Socket soc = null;
		ByteArrayOutputStream output = null;
		while (true) {
			try {
				Message msg = new Message();

				if (socket != null) {
					soc = socket.accept();
				}
				if (soc != null) {
					is = soc.getInputStream();// 获取客户端的输入流对象
				}
				if (is != null) {
					output = new ByteArrayOutputStream();
					byte[] buffer = new byte[20 * 1024];
					int n = 0;
					while (-1 != (n = is.read(buffer))) {
						output.write(buffer, 0, n);
					}
					byte[] data = output.toByteArray();
					// 将获取的数据发给handler处理
					msg.what = 4;
					msg.obj = data;
					this.handler.sendMessage(msg);
				} else {
					Log.d("aaa", "数据是null");
				}
			} catch (IOException e) {
				Log.d("aaa",
						"ListenThread.run() -->final Socket soc=socket.accept();has exception");
			} finally {
				try {
					if (is != null) {
						is.close();
					}
					if (output != null) {
						output.close();
					}
					if (soc != null) {
						soc.close();
					}

				} catch (Exception e2) {
					e2.printStackTrace();
				}

			}

		}
	}
}

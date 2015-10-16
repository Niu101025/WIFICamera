package com.ayit.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 监听socket的连接
 * 
 * @author Administrator
 * 
 */
public class ListenThread extends Thread {
	// 定义一个socket服务器
	ServerSocket socket = null;
	Handler handler;

	public ListenThread(int port, Handler handler) {
		try {
			port = 12345;
			socket = new ServerSocket(port);
			this.handler = handler;
		} catch (IOException e) {
			Log.d("aaa", "ListenThread ServerSocket init() has exception");
		}

	}

	@Override
	public void run() {
		// 阻塞等待客户端的连接
		while (true) {
			InputStream is = null;
			BufferedReader reader = null;
			PrintWriter out = null;
			Socket soc = null;
			try {
				Message msg = new Message();
				// 获取客户端连接的socket对象
				soc = socket.accept();
				if (soc != null) {
					// 获取客户端的输入流
					is = soc.getInputStream();
				}
				if (is != null) {
					reader = new BufferedReader(new InputStreamReader(is,
							"UTF-8"));
					out = new PrintWriter(soc.getOutputStream());
					String str = "";
					// 获取请求的命令
					str = reader.readLine();
					if (str.equals("takephoto") || str.equals("opencamera")) {
						// 将命令发给handler，去处理
						msg.what = 1;
						msg.obj = str;
						this.handler.sendMessage(msg);
						soc.close();
					}
				} else {

				}
			} catch (Exception e) {
				Log.d("aaa",
						"ListenThread.run() -->final Socket soc=socket.accept();has exception");

			} finally {
				try {
					if (is != null) {
						is = null;
					}
					if (reader != null) {
						reader = null;
					}
					if (out != null) {
						out = null;
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

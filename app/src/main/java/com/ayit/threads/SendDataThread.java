package com.ayit.threads;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

public class SendDataThread extends Thread {
	Socket socket;
	
	public SendDataThread(String address, String msg) {
		this.address = address;
		this.msg = msg;
	}

	String address;
	String msg;
	@Override
	public void run() {
		try {
			socket = new Socket(address, 12345);// 
		} catch (UnknownHostException e) {
			Log.d("aaa",
					"SendDataThread.init() has UnknownHostException"
							+ e.getMessage());
		} catch (IOException e) {
			Log.d("aaa", "SendDataThread.init().IOException:" + e.getMessage());
		}
		if (socket != null) {
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				//将请求回写给客户端
				out.println(msg); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
}

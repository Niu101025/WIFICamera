package com.ayit.threads;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import android.util.Log;

public class SendYuLanBitampThread extends Thread {

	String address;
	ByteArrayOutputStream outstream = null;
	private OutputStream outsocket;
	private byte byteBuffer[] = new byte[1024];

	public SendYuLanBitampThread(String address, ByteArrayOutputStream outstream) {
		this.address = address;
		this.outstream = outstream;
	}

	@Override
	public void run() {

		try {
			Socket socket = null;
			socket = new Socket(address, 12347);
			outsocket = socket.getOutputStream();
			ByteArrayInputStream inputstream = new ByteArrayInputStream(
					outstream.toByteArray());
			int amount;
			while ((amount = inputstream.read(byteBuffer)) != -1) {
				outsocket.write(byteBuffer, 0, amount);
			}
			outstream.flush();
			outstream.close();
			if (socket != null) {
				socket.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

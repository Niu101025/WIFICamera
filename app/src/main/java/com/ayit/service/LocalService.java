package com.ayit.service;

import com.ayit.threads.ListenBitmapThread;
import com.ayit.threads.ListenThread;
import com.ayit.threads.ListenYuLanBitmapThread;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

/**
 * 创建一个服务
 * @author Administrator
 *
 */
public class LocalService extends Service {

	private static final String TAG = "LocalService";
	//获取了binder对象,binder相当于是自己写的服务
	private IBinder binder = new LocalService.LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// 写一个方法 一直等待socket被接入
	public void startWaitDataThread(Handler handler) {
		// 开始监听12345端口
		new ListenThread(12345, handler).start();
		// 开始监听12346端口
		new ListenBitmapThread(12346, handler).start();
		//开始监听12347端口
		new ListenYuLanBitmapThread(12347, handler).start();

	}

	// 创建一个类继承于Binder
	public class LocalBinder extends Binder {
		// 提供一个方法可以获取LocalService服务
		public LocalService getService() {
			return LocalService.this;
		}
	}
}

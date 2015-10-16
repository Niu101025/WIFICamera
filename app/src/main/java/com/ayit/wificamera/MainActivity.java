package com.ayit.wificamera;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ayit.service.LocalService;
import com.ayit.threads.SendDataThread;
import com.ayit.utils.DisplayUtil;
import com.ayit.utils.FileUtil;
import com.ayit.utils.ImageUtil;

public class MainActivity extends Activity {
    private ServiceConnection sc = null;
    private WifiManager wifi = null;
    private Button open = null;
    private Button gets = null;
    public  static  Button sends = null;
    private Button gallery;
    public static ImageButton takephoto = null;
    private long exitTime;
    boolean flag = false;
    boolean connFlag = true;
    // 声明本地服务
    LocalService myservice;
    Handler handler;
    public static ImageView iv_photo;

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        connFlag = true;
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取系统的WiFi管理器对象
        wifi = (WifiManager) getSystemService(WIFI_SERVICE);
        initViews();
      /* initHandler();
        // 获取服务连接对象
        sc = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub
            }

            *//**
         * 当服务连接的时候
         *//*
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LocalBinder binder = (LocalBinder) service;// 这里相当于服务的绑定对象
                myservice = binder.getService();// 获取服务
                myservice.startWaitDataThread(handler);// 开始监听端口，并将数据读传给handler
            }
        };*/
        /**
         * 打开WiFi热点
         */
        open.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                flag = !flag;
                setWifiApEnabled(flag);
            }
        });
        gets.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<String> connectedIP = getConnectedIP();
                StringBuilder resultList = new StringBuilder();

                for (String ip : connectedIP) {
                    resultList.append(ip);
                    resultList.append("\n");
                }
                Toast.makeText(getApplicationContext(),
                        "当前的连接有：" + resultList.toString(), Toast.LENGTH_LONG)
                        .show();
            }
        });

        sends.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<String> connectedIP = getConnectedIP();

                for (String ip : connectedIP) {
                    if (ip.contains(".")) {
                        if (sends.getText().toString().equals("打开相机")) {
                            new SendDataThread(ip, "opencamera").start();//
                        } else if (sends.getText().toString().equals("关闭相机")) {
                            new SendDataThread(ip, "close").start();//
                        }


                    }
                }
            }
        });

        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ArrayList<String> connectedIP = getConnectedIP();
                for (String ip : connectedIP) {
                    if (ip.contains(".")) {
                        new SendDataThread(ip, "takephoto").start();//
                    }
                }
            }
        });
        // connection();
    }

    private void initHandler() {

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        String str = msg.obj.toString();
                        if (str.equals("opencamera")) {
                            if (connFlag) {
                                HomeActivity.tabHost.setCurrentTab(1);
                                connFlag = false;
                            } else {
                                Toast.makeText(getApplicationContext(), "相机已经打开了",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (str.equals("close")) {
                            MyCameraActivity.close();
                        } else {
                            MyCameraActivity.kuaimen();
                        }

                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, msg.obj.toString(),
                                Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        try {
                            byte[] data = (byte[]) msg.obj;
                            Bitmap b = null;
                            if (null != data) {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = false;
                                options.inSampleSize = 4; //
                                b = BitmapFactory.decodeByteArray(data, 0,
                                        data.length, options);//
                                if (b.getHeight() < b.getWidth()) {
                                    Bitmap rotaBitmap = ImageUtil.getRotateBitmap(
                                            b, 90.0f);
                                    FileUtil.saveBitmap(rotaBitmap);
                                    iv_photo.setImageBitmap(rotaBitmap);
                                    takephoto.setVisibility(View.VISIBLE);
                                    rotaBitmap = null;
                                } else {
                                    iv_photo.setImageBitmap(b);
                                    LayoutParams p2 = takephoto.getLayoutParams();
                                    p2.width = DisplayUtil.dip2px(
                                            MainActivity.this, 80);
                                    p2.height = DisplayUtil.dip2px(
                                            MainActivity.this, 80);
                                    ;
                                    takephoto.setLayoutParams(p2);
                                    FileUtil.saveBitmap(b);
                                }
                                b = null;
                                data = null;
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        break;
                    case 4:
                        try {
                            byte[] data = (byte[]) msg.obj;
                            Bitmap b = null;
                            if (null != data) {
                                sends.setText("关闭相机");

                                b = BitmapFactory.decodeByteArray(data, 0,
                                        data.length);//
                                if (b.getHeight() < b.getWidth()) {
                                    Bitmap rotaBitmap = ImageUtil.getRotateBitmap(
                                            b, 90.0f);
                                    iv_photo.setImageBitmap(rotaBitmap);
                                    LayoutParams p2 = takephoto.getLayoutParams();
                                    p2.width = DisplayUtil.dip2px(
                                            MainActivity.this, 80);
                                    p2.height = DisplayUtil.dip2px(
                                            MainActivity.this, 80);
                                    takephoto.setLayoutParams(p2);
                                    rotaBitmap = null;
                                } else {
                                    iv_photo.setImageBitmap(b);
                                    LayoutParams p2 = takephoto.getLayoutParams();
                                    p2.width = DisplayUtil.dip2px(
                                            MainActivity.this, 80);
                                    p2.height = DisplayUtil.dip2px(
                                            MainActivity.this, 80);
                                    ;
                                    takephoto.setLayoutParams(p2);
                                }
                                b = null;
                                data = null;
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void initViews() {
        open = (Button) findViewById(R.id.openheat);
        gets = (Button) findViewById(R.id.bt_getphone);
        sends = (Button) findViewById(R.id.startsocket);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        takephoto = (ImageButton) findViewById(R.id.takephoto);
        gallery = (Button) findViewById(R.id.gallery);
        gallery.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),
                        GallaryActivity.class);
                startActivity(intent);
            }
        });
    }

    public static ArrayList<String> getConnectedIP() {// 获取连接在服务器上的所有设备
        ArrayList<String> connectedIP = new ArrayList<String>();
        // 读取Linux文件系统中的某个文件

        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectedIP;
    }

    /**
     * 使能WiFi热点
     *
     * @param enabled
     * @return
     */
    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) { // disable WiFi in any case
            wifi.setWifiEnabled(false);
        }
        try {
            // TelephonyManager tm = (TelephonyManager)
            // getSystemService(Context.TELEPHONY_SERVICE);

            // 获取WiFi的热点名
            String model = Build.MODEL;
            // 获取WiFi的设置对象
            WifiConfiguration apConfig = new WifiConfiguration();
            // 设置WiFi的ssid
            apConfig.SSID = model;
            String password = "12345678";
            // 设置密码
            apConfig.preSharedKey = password;
            apConfig.hiddenSSID = true;
            apConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            apConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            apConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            apConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            apConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            apConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            // 开启WiFi 这个是重点
            apConfig.status = WifiConfiguration.Status.ENABLED;
            // 通过反射，将自定义的WiFi配置设置上去
                Method method = wifi.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, Boolean.TYPE);
            Toast.makeText(MainActivity.this,
                    "WiFi名：" + apConfig.SSID + ",密码是：" + apConfig.preSharedKey,
                    Toast.LENGTH_LONG).show();
            return (Boolean) method.invoke(wifi, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }

    private void connection() {
        Intent intent = new Intent("com.deng.bindService");
        // 绑定服务 自动创建服务
        bindService(intent, sc, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        // 取消绑定服务
        //  unbindService(sc);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) // System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

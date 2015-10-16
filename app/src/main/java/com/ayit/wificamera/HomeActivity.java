package com.ayit.wificamera;

import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import com.ayit.service.LocalService;
import com.ayit.utils.DisplayUtil;
import com.ayit.utils.FileUtil;
import com.ayit.utils.ImageUtil;

/**
 * 项目名称：WIFICamera
 * 类描述：
 * 创建人：钮红宾
 * 创建时间：2015/10/11 17:10
 * 修改人：钮红宾
 * 修改时间：2015/10/11 17:10
 * 修改备注：
 */
public class HomeActivity extends TabActivity {

    public static TabHost tabHost;
    private Intent takePhoto;
    private Intent photos;
    private Intent setting;
    private Intent playing;
    public static TabWidget tabWidget;


    private ServiceConnection sc = null;
    private WifiManager wifi = null;
    private long exitTime;
    boolean flag = false;
    boolean connFlag = true;
    // 声明本地服务
    LocalService myservice;
    Handler handler;


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        connFlag = true;
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab);
        wifi = (WifiManager) getSystemService(WIFI_SERVICE);
        initHandler();
        // 获取服务连接对象
        sc = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub
            }

            /**
             * 当服务连接的时候
             */
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LocalService.LocalBinder binder = (LocalService.LocalBinder) service;// 这里相当于服务的绑定对象
                myservice = binder.getService();// 获取服务
                myservice.startWaitDataThread(handler);// 开始监听端口，并将数据读传给handler
            }
        };
        connection();


        tabHost = getTabHost();
        initIntent();
        addSpec();
        tabWidget = tabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getTabCount(); i++) {
          /*  LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabWidget.getLayoutParams();
            params.height = DisplayUtil.px2dip(getApplicationContext(), 60);*/


            if (i == 2) {
                tabWidget.getChildAt(i).setClickable(false);
                tabWidget.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            Toast.makeText(getApplicationContext(), "自己的相机，只能由别人打开哟", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
            }
        }
        tabHost.setCurrentTab(0);
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
                                HomeActivity.tabHost.setCurrentTab(2);
                                tabWidget.getChildAt(2).setClickable(true);
                                tabWidget.getChildAt(1).setClickable(false);
                                connFlag = false;
                            } else {
                                Toast.makeText(getApplicationContext(), "相机已经打开了",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (str.equals("close")) {
                            //关闭相机

                        } else {
                            MyCameraActivity.kuaimen();
                        }

                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), msg.obj.toString(),
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
                                    MainActivity.iv_photo.setImageBitmap(rotaBitmap);
                                    MainActivity.takephoto.setVisibility(View.VISIBLE);
                                    rotaBitmap = null;
                                } else {
                                    MainActivity.iv_photo.setImageBitmap(b);
                                    ViewGroup.LayoutParams p2 = MainActivity.takephoto.getLayoutParams();
                                    p2.width = DisplayUtil.dip2px(
                                            getApplicationContext(), 80);
                                    p2.height = DisplayUtil.dip2px(
                                            getApplicationContext(), 80);
                                    ;
                                    MainActivity.takephoto.setLayoutParams(p2);
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
                                //  MainActivity.sends.setText("关闭相机");
                                b = BitmapFactory.decodeByteArray(data, 0,
                                        data.length);//
                                if (b.getHeight() < b.getWidth()) {
                                    Bitmap rotaBitmap = ImageUtil.getRotateBitmap(
                                            b, 90.0f);
                                    MainActivity.iv_photo.setImageBitmap(rotaBitmap);
                                    ViewGroup.LayoutParams p2 = MainActivity.takephoto.getLayoutParams();
                                    p2.width = DisplayUtil.dip2px(
                                            getApplicationContext(), 80);
                                    p2.height = DisplayUtil.dip2px(
                                            getApplicationContext(), 80);
                                    MainActivity.takephoto.setLayoutParams(p2);
                                    rotaBitmap = null;
                                } else {
                                    MainActivity.iv_photo.setImageBitmap(b);
                                    ViewGroup.LayoutParams p2 = MainActivity.takephoto.getLayoutParams();
                                    p2.width = DisplayUtil.dip2px(
                                            getApplicationContext(), 80);
                                    p2.height = DisplayUtil.dip2px(
                                            getApplicationContext(), 80);
                                    ;
                                    MainActivity.takephoto.setLayoutParams(p2);
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


    /**
     * 初始化各个tab标签对应的intent
     */
    private void initIntent() {
        takePhoto = new Intent(this, MainActivity.class);
        photos = new Intent(this, GallaryActivity.class);
        setting = new Intent(this, SettingActivity.class);
        playing = new Intent(this, MyCameraActivity.class);
    }

    /**
     * 为tabHost添加各个标签项
     */
    private void addSpec() {

        tabHost.addTab(this.buildTagSpec("setting",
                R.string.setting, R.drawable.ic_menu_setting, setting));
        tabHost.addTab(this.buildTagSpec("takephoto",
                R.string.others, R.drawable.ic_menu_camera, takePhoto));
        tabHost.addTab(this.buildTagSpec("paying",
                R.string.yours, R.drawable.ic_menu_camera, playing));
        tabHost.addTab(this.buildTagSpec("photos",
                R.string.photo, R.drawable.ic_menu_gallery, photos));


    }

    /**
     * 自定义创建标签项的方法
     *
     * @param tagName  标签标识
     * @param tagLable 标签文字
     * @param icon     标签图标
     * @param content  标签对应的内容
     * @return
     */
    private TabHost.TabSpec buildTagSpec(String tagName, int tagLable,
                                         int icon, Intent content) {
        return tabHost
                .newTabSpec(tagName)
                .setIndicator(getResources().getString(tagLable),
                        getResources().getDrawable(icon)).setContent(content);
    }


    private void connection() {
        Intent intent = new Intent("com.deng.bindService");
        // 绑定服务 自动创建服务
        bindService(intent, sc, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        // 取消绑定服务
        unbindService(sc);
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



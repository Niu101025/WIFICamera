package com.ayit.wificamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * 项目名称：WIFICamera
 * 类描述：
 * 创建人：钮红宾
 * 创建时间：2015/10/11 18:52
 * 修改人：钮红宾
 * 修改时间：2015/10/11 18:52
 * 修改备注：
 */
public class SettingActivity extends Activity implements View.OnClickListener {
    private TextView tvOpenWifi;
    private TextView tvOpenCamera;
    private TextView tvLookConn;
    private TextView tvFileDir;
    private TextView tvAboutAuthor;
    private TextView tvConnInfo;


    boolean flag = false;
    private WifiManager wifi = null;
    // 获取系统的WiFi管理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        tvOpenWifi = (TextView) findViewById(R.id.tv_openwifi);
        tvOpenCamera = (TextView) findViewById(R.id.tv_opencamera);
        tvAboutAuthor = (TextView) findViewById(R.id.tv_about_author);
        tvFileDir = (TextView) findViewById(R.id.tv_file_dir);
        //tvFileDir.setText("文件路径：内存卡中的playCamera文件夹");
        tvLookConn = (TextView) findViewById(R.id.tv_look_conn);
        tvOpenWifi.setOnClickListener(this);
        tvOpenCamera.setOnClickListener(this);
        tvLookConn.setOnClickListener(this);
        tvLookConn.setVisibility(View.GONE);
        tvFileDir.setOnClickListener(this);
        tvAboutAuthor.setOnClickListener(this);
        tvConnInfo = (TextView) findViewById(R.id.tv_conn_info);
        wifi = (WifiManager) getSystemService(WIFI_SERVICE);

        if (wifi.WIFI_STATE_ENABLED == wifi.getWifiState()) {
            flag = true;
            tvOpenWifi.setText("关闭WiFi");
        } else {
            flag = false;
            tvOpenWifi.setText("打开WiFi");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_openwifi:
                flag = !flag;
                setWifiApEnabled(flag);
                break;
            case R.id.tv_opencamera:
                //打开相机
                HomeActivity.tabHost.setCurrentTab(1);
                break;
            case R.id.tv_about_author:
                getDialog("关于作者", "作者钮红宾，Android和嵌入式爱好者，喜欢钻研一些创新的东西，如果对Android感兴趣或者有好的idea可以和我联系。").show();
                break;
            case R.id.tv_file_dir:
                getDialog("图片位置", "图片储存在内存卡中的WIFICamera中").show();
                break;
            case R.id.tv_look_conn:
                break;

        }
    }

    private AlertDialog getDialog(String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(SettingActivity.this).create();
        dialog.setTitle(title);
        dialog.setMessage(message);

        return dialog;
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
            if (enabled) {
                Toast.makeText(getApplicationContext(), "热点已经打开", Toast.LENGTH_SHORT).show();
                tvConnInfo.setText("WiFi名：" + apConfig.SSID + "\n"
                        + "密码是：" + apConfig.preSharedKey);
                tvOpenWifi.setText("关闭WiFi");
                tvConnInfo.setVisibility(View.VISIBLE);
            } else {
                tvOpenWifi.setText("打开WiFi");
                Toast.makeText(getApplicationContext(), "热点已经关闭", Toast.LENGTH_SHORT).show();
                tvConnInfo.setText("");
                tvConnInfo.setVisibility(View.GONE);
            }
            //使用反射
            return (Boolean) method.invoke(wifi, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }

    }
}

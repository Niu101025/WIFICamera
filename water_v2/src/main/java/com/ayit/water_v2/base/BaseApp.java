package com.ayit.water_v2.base;

import android.app.Application;

/**
 * 项目名称：WIFICamera
 * 类描述：应用程序的出口
 * 创建人：钮红宾
 * 创建时间：2015/9/27 14:07
 * 修改人：钮红宾
 * 修改时间：2015/9/27 14:07
 * 修改备注：
 */
public class BaseApp extends Application {

    private BaseApp baseApp;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApp = this;

    }
}

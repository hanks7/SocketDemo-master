package com.yzq.socketdemo;

import android.app.Application;

/**
 * @author 侯建军 QQ:474664736
 * @time 2020/5/8 15:20
 * @class describe
 */
public class BaseApplication extends Application {
    public static BaseApplication APP;
    public String ip;
    public String port;
    @Override
    public void onCreate() {
        APP = this;
        super.onCreate();

    }
}

package com.yzq.socketdemo.utils;

import android.widget.Toast;

import static com.yzq.socketdemo.BaseApplication.APP;


/**
 * @author 侯建军
 * @data on 2018/1/4 11:03
 * @org www.hopshine.com
 * @function 请填写
 * @email 474664736@qq.com
 */
public class UToast {


    /**
     * 构造方法私有化 不允许new对象
     */
    private UToast() {
    }

    /**
     * Toast对象
     */
    private static Toast toast = null;

    /**
     * 显示Toast
     */
    public static void showText(Object strToast) {
        Ulog.i("toast", strToast);
        if (toast == null) {
            toast = Toast.makeText(APP, "", Toast.LENGTH_SHORT);
        }
        toast.setText(strToast + "");
        toast.show();
    }






}

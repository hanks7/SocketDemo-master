package com.yzq.socketdemo.utils;

import android.util.Log;

/**
 * @author 侯建军
 * @data on 2018/1/4 10:40
 * @org www.hopshine.com
 * @function 请填写
 * @email 474664736@qq.com
 */
public class Ulog {

    public static final String TAG = "cc-";
    private static final boolean IS_RELEASE = false ;

    public static void i(Object mess) {
        if (IS_RELEASE) return;

        Log.e(TAG + "", mess + "");
    }

    public static void i(Object tag, Object mess) {
        if (IS_RELEASE) return;

        Log.e(TAG + tag, mess + "");
    }

    public static void i(Object tag, Object... content) {

        if (IS_RELEASE) return;

        Log.e(TAG + tag, getString(content) + "");
    }

    public static void i1(Object tag, Object... content) {

        if (IS_RELEASE) return;

        Log.e(TAG + tag, getString(content) + getFunctionName());
    }


    public static void i1(Object mess) {
        if (IS_RELEASE) return;

        Log.e(TAG + "", mess + getFunctionName());
    }

    public static void i1(Object tag, Object mess) {
        if (IS_RELEASE) return;

        Log.e(TAG + tag, mess + getFunctionName());
    }


    /**
     * 获取相关数据:类名,方法名,行号等.用来定位行
     *
     * @return
     */
    public static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts != null) {
            for (StackTraceElement st : sts) {
                if (st.isNativeMethod()) {
                    continue;
                }
                if (st.getClassName().equals(Thread.class.getName())) {
                    continue;
                }
                if (st.getClassName().equals(Ulog.class.getName())) {
                    continue;
                }
                return "[ Thread:" + Thread.currentThread().getName() + " " + st.getMethodName()
                        + "(" + st.getFileName() + ":" + st.getLineNumber() + ")" + " ]";
            }
        }
        return null;
    }

    private static String getString(Object[] obArray) {
        String message = "";
        for (int i = 0; i < obArray.length; i++) {
            message += obArray[i] + (i == 1 || i == (obArray.length - 1) ? "" : "===");
        }

        return message;
    }

}

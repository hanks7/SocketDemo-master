package com.yzq.socketdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.yzq.socketdemo.BaseApplication;
import com.yzq.socketdemo.ReciveListener;
import com.yzq.socketdemo.SocketListener;
import com.yzq.socketdemo.common.Constants;
import com.yzq.socketdemo.common.EventMsg;
import com.yzq.socketdemo.utils.UToast;
import com.yzq.socketdemo.utils.Ulog;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by yzq on 2017/9/26.
 * <p>
 * socket连接服务
 */
public class SocketService extends Service {

    private Socket socket;
    private Thread connectThread;//连接线程
    private OutputStream outputStream;


    private Timer timer;
    private TimerTask task;


    /**
     * 默认重连
     */
    private boolean isReConnect = true;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    UToast.showText(msg.obj);
                    break;
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return new BinderControl();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSocket();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化socket
     */
    private void initSocket() {
        if (socket == null && connectThread == null) {
            connectThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    socket = new Socket();
                    try {
                        /*超时时间为2秒*/
                        socket.connect(new InetSocketAddress(BaseApplication.APP.ip, Integer.valueOf(BaseApplication.APP.port)), 2000);
                        /*连接成功的话  发送心跳包*/
                        if (socket!=null&&socket.isConnected()) {


                            /*因为Toast是要运行在主线程的  这里是子线程  所以需要到主线程哪里去显示toast*/
                            showToast("socket已连接");

                            /*发送连接成功的消息*/
                            EventMsg msg = new EventMsg();
                            msg.setTag(Constants.CONNET_SUCCESS);
                            EventBus.getDefault().post(msg);

                            sendBeatData();//发送心跳数据

                            getReceiveData();//接收数据

                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                        if (e instanceof SocketTimeoutException) {
                            showToast("连接超时，正在重连");

                            releaseSocket(); //释放资源
                            if (isReConnect) {
                                initSocket();//重新链接初始化socket
                            }

                        } else if (e instanceof NoRouteToHostException) {
                            showToast("该地址不存在，请检查");
                            stopSelf();

                        } else if (e instanceof ConnectException) {
                            showToast("连接异常或被拒绝，请检查");
                            stopSelf();

                        }


                    }

                }
            });

            /*启动连接线程*/
            connectThread.start();

        }


    }

    /**
     * 发送数据
     *
     * @param order
     */
    public void sendOrder(final String order) {
        if (socket != null && socket.isConnected()) {
            /*发送指令*/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream = socket.getOutputStream();
                        if (outputStream != null) {
                            outputStream.write((order).getBytes("gbk"));
                            outputStream.flush();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        } else {
            showToast("socket连接错误,请重试");
            releaseSocket(); //释放资源
            if (isReConnect) {
                initSocket();//重新链接初始化socket
            }
        }
    }

    /**
     * 接收数据
     *
     * @throws IOException
     */
    private void getReceiveData() throws IOException {
        while (true) {
            try {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
                if (bufferedInputStream.available() > 0) {
                    byte[] receive = new byte[12];
                    bufferedInputStream.read(receive);
                    Ulog.i("getReceiveData", getMess(receive));
                    if (mListener != null) {
                        mListener.getData(getMess(receive));
                    }
                } else {
                    Thread.sleep(50);
                }
            } catch (Exception e) {
                /*发送失败说明socket断开了或者出现了其他错误*/
                showToast("连接断开，正在重连");

                releaseSocket(); //释放资源
                if (isReConnect) {
                    initSocket();//重新链接初始化socket
                }
                e.printStackTrace();
            }
        }

    }

    private String getMess(byte[] result) throws UnsupportedEncodingException {
        String temp=new String(result);

//识别编码
        if(temp.contains("utf-8")){
            return new String(result,"utf-8");
        }else if(temp.contains("gb2312")){
            return new String(result,"gb2312");
        }else{
            return new String(result,"utf-8");
        }
    }

    /**
     * 发送心跳数据
     */
    private void sendBeatData() {
        if (timer == null) {
            timer = new Timer();
        }

        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        outputStream = socket.getOutputStream();

                        /*这里的编码方式根据你的需求去改*/
                        outputStream.write(("心跳包").getBytes("gbk"));
                        outputStream.flush();
                    } catch (Exception e) {
                        /*发送失败说明socket断开了或者出现了其他错误*/
                        showToast("连接断开，正在重连");

                        releaseSocket(); //释放资源
                        if (isReConnect) {
                            initSocket();//重新链接初始化socket
                        }
                        e.printStackTrace();


                    }
                }
            };
        }

        timer.schedule(task, 0, 12000);
    }


    /**
     * 释放资源
     */
    private void releaseSocket() {

        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }

        if (outputStream != null) {
            try {
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }

        if (socket != null) {
            try {
                socket.close();

            } catch (IOException e) {
            }
            socket = null;
        }

        if (connectThread != null) {
            connectThread = null;
        }

    }


    /**
     * @param strMsgContent
     */
    private void showToast(final String strMsgContent) {
        Message msg = new Message();
        msg.obj = strMsgContent;
        msg.what = 1;
        handler.sendMessage(msg);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("SocketService", "onDestroy");
        isReConnect = false;
        releaseSocket();//释放资源
    }

    ReciveListener mListener;

    class BinderControl extends Binder implements SocketListener {

        @Override
        public void sendMessage(String msg) {
            sendOrder(msg);
        }

        @Override
        public void setReceiveListener(ReciveListener listener) {
            mListener = listener;
        }

        @Override
        public void init() {
            initSocket();
        }
    }

}

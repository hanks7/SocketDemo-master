package com.yzq.socketdemo;

/**
 * @author 侯建军 QQ:474664736
 * @time 2020/5/8 14:30
 * @class describe
 */
public interface SocketListener {
    void sendMessage(String msg);
    void setReceiveListener(ReciveListener listener);
    void init();
}

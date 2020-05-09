package com.yzq.socketdemo.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.EditText;
import android.widget.TextView;

import com.yzq.socketdemo.R;
import com.yzq.socketdemo.ReciveListener;
import com.yzq.socketdemo.SocketListener;
import com.yzq.socketdemo.service.SocketService;
import com.yzq.socketdemo.utils.UToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by yzq on 2017/9/26.
 * <p>
 * mainActivity
 */
public class MainActivity extends BaseActivity {
    @BindView(R.id.contentEt)
    EditText contentEt;
    @BindView(R.id.receiveContent)
    TextView receiveContent;

    private ServiceConnection connection;
    private SocketListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindSocketService();
        ButterKnife.bind(this);
    }

    private void bindSocketService() {

        /*通过binder拿到service*/
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                listener = (SocketListener) iBinder;
                listener.setReceiveListener(reciveListener);


            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };


        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    ReciveListener reciveListener = new ReciveListener() {
        @Override
        public void getData(String content) {
            receiveContent.append(content+"\n");
        }
    };


    @OnClick(R.id.sendBtn)
    public void onViewClicked() {

        String data = contentEt.getText().toString().trim();
        if (listener != null) {
            listener.sendMessage(data);
        } else {
            UToast.showText("SocketListener为null");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);

    }
}

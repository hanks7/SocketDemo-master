package com.yzq.socketdemo.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.EditText;

import com.yzq.socketdemo.R;
import com.yzq.socketdemo.service.SocketService;

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

    private ServiceConnection connection;
    public SocketService socketService;

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
                SocketService.SocketBinder binder = (SocketService.SocketBinder) iBinder;
                socketService = binder.getService();

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };


        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @OnClick(R.id.sendBtn)
    public void onViewClicked() {

        String data = contentEt.getText().toString().trim();

        socketService.sendOrder(data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);

    }
}

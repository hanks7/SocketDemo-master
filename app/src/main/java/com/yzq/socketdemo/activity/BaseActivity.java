package com.yzq.socketdemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

/**
 * @author 侯建军 QQ:474664736
 * @time 2020/5/8 10:02
 * @class describe
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("cc-ui-activity", "(" + getClass().getSimpleName() + ".java:0)");
    }
}

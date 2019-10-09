package com.sinonet.chinaums;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.sinonet.chinaums.wxapi.WXEntryActivity;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("debug","MyApplication  onCreate");
    }

}

package com.example.wxandzfb;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * Content：
 * Actor：韩小呆 ヾ(✿ﾟ▽ﾟ)ノ
 * Time:  2019/01/20  10:20
 * Update:
 * Time:
 */
public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }

    public static Context getContext() {
        return context;
    }
}

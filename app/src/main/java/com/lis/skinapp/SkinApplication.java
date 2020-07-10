package com.lis.skinapp;

import android.app.Application;

import com.lis.skinlibrary.SkinManager;

/**
 * Created by lis on 2020/7/10.
 */
public class SkinApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.init(this);
    }
}

package com.lis.skinlibrary;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.LayoutInflaterCompat;

import com.lis.skinlibrary.utils.SkinThemeUtils;

import java.lang.reflect.Field;
import java.util.Observable;

/**
 * 对外接口
 * Created by lis on 2020/7/10.
 */
public class ApplicationActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    private Observable mObservable;
    private ArrayMap<Activity, SkinLayoutInflaterFactory> mLayoutInflaterFactories = new ArrayMap<>();

    public ApplicationActivityLifecycle(Observable observable) {
        mObservable = observable;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        /**
         * 更新状态栏
         */
        SkinThemeUtils.updateStatusBarColor(activity);
        /**
         * 更新布局视图
         */
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        try {
            Field mFactorySet = LayoutInflater.class.getDeclaredField("mFactorySet");
            mFactorySet.setAccessible(true);
            try {
                mFactorySet.setBoolean(layoutInflater, false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        //使用factory2,设置布局加载工程
        SkinLayoutInflaterFactory factory = new SkinLayoutInflaterFactory(activity);
        LayoutInflaterCompat.setFactory2(layoutInflater, factory);
        mLayoutInflaterFactories.put(activity, factory);
        mObservable.addObserver(factory);

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        SkinLayoutInflaterFactory remove = mLayoutInflaterFactories.remove(activity);
        SkinManager.getInstance().deleteObserver(remove);

    }
}

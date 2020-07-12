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
        boolean isHooked = true;
        try {
            Field mFactorySet = layoutInflater.getClass().getDeclaredField("mFactorySet");
            mFactorySet.setAccessible(true);
            try {
                mFactorySet.setBoolean(layoutInflater, false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            isHooked = false;
            e.printStackTrace();

        }

        //使用factory2,设置布局加载工程
        SkinLayoutInflaterFactory factory = new SkinLayoutInflaterFactory(activity);
        if (isHooked) {
            LayoutInflaterCompat.setFactory2(layoutInflater, factory);
        } else {
            //解决android Q上无法二次setFactroy的问题。
            Class<LayoutInflaterCompat> compatClass = LayoutInflaterCompat.class;
            Class<LayoutInflater> inflaterClass = LayoutInflater.class;
            try {
                Field sCheckedField = compatClass.getDeclaredField("sCheckedField");
                sCheckedField.setAccessible(true);
                sCheckedField.setBoolean(layoutInflater, false);
                Field mFactory2 = inflaterClass.getDeclaredField("mFactory2");
                mFactory2.setAccessible(true);
                mFactory2.set(layoutInflater, factory);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            mLayoutInflaterFactories.put(activity, factory);
            mObservable.addObserver(factory);
        }
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
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle
            outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        SkinLayoutInflaterFactory remove = mLayoutInflaterFactories.remove(activity);
        SkinManager.getInstance().deleteObserver(remove);

    }
}

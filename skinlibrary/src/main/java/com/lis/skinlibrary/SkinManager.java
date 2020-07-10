package com.lis.skinlibrary;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.lis.skinlibrary.utils.SkinResources;

import java.lang.reflect.Method;
import java.util.Observable;

/**
 * 皮肤管理，被观察者
 * Created by lis on 2020/7/10.
 */
public class SkinManager extends Observable {

    private volatile static SkinManager instance;

    private ApplicationActivityLifecycle mActivityLifecycle;
    private Application mContext;

    public SkinManager(Application application) {
        this.mContext = application;
        //共享首选项，用于记录当前使用的皮肤
        SkinPreference.init(application);
        //资源管理器，用于从app中加载资源
        SkinResources.init(application);
        //注册Activity生命周期，并设置被观察者
        mActivityLifecycle = new ApplicationActivityLifecycle(this);
        application.registerActivityLifecycleCallbacks(mActivityLifecycle);
        //加载上次使用保存的皮肤
        loadSkin(SkinPreference.getInstance().getSkin());
    }

    public void loadSkin(String skin) {
        if (TextUtils.isEmpty(skin)) {
            //使用默认
            SkinPreference.getInstance().reset();
            SkinResources.getInstance().reset();
        } else {
            try {
                //宿主app的resources
                Resources appResources = mContext.getResources();
                //反射创建AssetManager和Resources
                AssetManager assetManager = AssetManager.class.newInstance();
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                addAssetPath.invoke(assetManager, skin);

                //根据当前的设置显示器信息，与配置（横竖，语言等）创建Resources
                Resources resources = new Resources(assetManager, appResources.getDisplayMetrics(), appResources.getConfiguration());
                //获取外部apk包名
                PackageManager packageManager = mContext.getPackageManager();
                PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(skin, PackageManager.GET_ACTIVITIES);
                String packageName = packageArchiveInfo.packageName;
                SkinResources.getInstance().applySkin(resources, packageName);

                //记录
                SkinPreference.getInstance().setSkin(skin);//data/data/packagename/skin/skin.apk


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //通知采集的View更新皮肤
        //被观察者改变 通知所有观察者
        setChanged();
        notifyObservers(null);

    }

    /**
     * 初始化，必需在application中
     *
     * @param application
     * @return
     */
    public static void init(Application application) {
        if (instance == null) {
            synchronized (SkinManager.class) {
                if (instance == null) {
                    instance = new SkinManager(application);
                }
            }
        }
    }

    public static SkinManager getInstance() {
        return instance;
    }


}

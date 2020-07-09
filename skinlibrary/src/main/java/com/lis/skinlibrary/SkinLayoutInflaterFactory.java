package com.lis.skinlibrary;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * 用来接管系统的View的生产过程
 */
public class SkinLayoutInflaterFactory implements LayoutInflater.Factory2 {
    //记录对应View的构造函数
    private static final Class<?>[] mConstructorSignature = new Class[]{
            Context.class, AttributeSet.class};
    private static final HashMap<String, Constructor<? extends View>> mConstructorMap =
            new HashMap<String, Constructor<? extends View>>();

    private static final String[] mClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app.",
            "android.view."
    };
    //当选择新皮肤后需要替换View与之对应的属性
    //页面属性管理器
    private SkinAttribute mSkinAttribute;
    //用于获取窗口状态框的信息
    private Activity mActivity;

    SkinLayoutInflaterFactory(Activity activity) {
        mSkinAttribute = new SkinAttribute();
        mActivity = activity;
    }


    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        //换肤就是在需要时候替换View的属性（src,background等)
        //所以这里创建View，从而修改View属性
        View view = createSDkView(name, context, attrs);
        if (view == null) {
            view = createView(name, context, attrs);
        }
        //这就是我们加入的逻辑
        if (view != null) {
            //加载属性
            mSkinAttribute.look(view, attrs);
        }
        return view;
    }

    /**
     *
     */
    private View createSDkView(String name, Context context, AttributeSet attributeSet) {
        //如果包含了.则不是SDK中的View可能是自定义View包括support库中的View
        if (-1 != name.indexOf(".")) {
            return null;
        }
        for (int i = 0; i < mClassPrefixList.length; i++) {
            return createView(mClassPrefixList[i] + name, context, attributeSet);
        }
        return null;
    }

    private View createView(String name, Context context, AttributeSet attributeSet) {
        //抄源码
        Constructor<? extends View> constructor = findConstructor(context, name);
        try {
            return constructor.newInstance(context, attributeSet);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取构造方法
     *
     * @param context
     * @param name
     * @return
     */
    private Constructor<? extends View> findConstructor(Context context, String name) {
        Constructor<? extends View> constructor = mConstructorMap.get(name);
        if (constructor == null) {
            try {
                Class<? extends View> aClass = context.getClassLoader().loadClass(name).asSubclass(View.class);
                try {
                    constructor = aClass.getConstructor(mConstructorSignature);
                    mConstructorMap.put(name, constructor);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return constructor;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return null;
    }
}

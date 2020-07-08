package com.lis.skinlibrary;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SkinAttribute {

    //记录换肤操作的View与属性信息
    private List<SkinView> mSkinViews = new ArrayList<>();

    static class SkinView {
        View view;
        //这个View能被换肤的属性与它对应的id
        List<SkinPair> skinPairs;
    }

    static class SkinPair {
        //属性
        String attributeName;
        //对应的资源id
        int resId;

        public SkinPair(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }

    }
}

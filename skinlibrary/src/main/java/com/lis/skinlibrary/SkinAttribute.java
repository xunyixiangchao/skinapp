package com.lis.skinlibrary;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.lis.skinlibrary.utils.SkinResources;
import com.lis.skinlibrary.utils.SkinThemeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 这里面放了所有要换肤的view所对应的属性
 */
public class SkinAttribute {

    private static final List<String> mAttributes = new ArrayList<>();
    public static final String BACKGROUND = "background";
    public static final String SRC = "src";
    public static final String TEXT_COLOR = "textColor";
    public static final String DRAWABLE_LEFT = "drawableLeft";
    public static final String DRAWABLE_TOP = "drawableTop";
    public static final String DRAWABLE_RIGHT = "drawableRight";
    public static final String DRAWABLE_BOTTOM = "drawableBottom";

    static {
        mAttributes.add(BACKGROUND);
        mAttributes.add(SRC);
        mAttributes.add(TEXT_COLOR);
        mAttributes.add(DRAWABLE_LEFT);
        mAttributes.add(DRAWABLE_TOP);
        mAttributes.add(DRAWABLE_RIGHT);
        mAttributes.add(DRAWABLE_BOTTOM);
    }

    //记录换肤操作的View与属性信息
    private List<SkinView> mSkinViews = new ArrayList<>();

    /**
     * 记录下一个View身上，哪几个属性需要换肤（并没有换）
     * @param view
     * @param attrs
     */
    public void look(View view, AttributeSet attrs) {
        List<SkinPair> skinPairs = new ArrayList<>();

        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            //获得属性名 textColor/background
            String attributeName = attrs.getAttributeName(i);
            if (mAttributes.contains(attributeName)) {
                //#
                //?000fff
                //@ffffff
                String attributeValue = attrs.getAttributeValue(i);
                //color以#写死的颜色，不可以换肤
                if (attributeValue.startsWith("#")) {
                    continue;
                }
                int resId;
                //以？开头的是属性,系统私有的
                if (attributeValue.startsWith("?")) {
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    resId = SkinThemeUtils.getResId(view.getContext(), new int[]{attrId})[0];
                } else {
                    //正常以@开头
                    resId = Integer.parseInt(attributeValue.substring(1));
                }
                SkinPair skinPair = new SkinPair(attributeName, resId);
                skinPairs.add(skinPair);
            }
        }
        if (!skinPairs.isEmpty() || view instanceof SkinViewSupport) {
            SkinView skinView = new SkinView(view, skinPairs);
            //如果选择过皮肤，调用一次applySkin加载皮肤资源
            skinView.applySkin();
            mSkinViews.add(skinView);
        }

    }

    /**
     * 对所有的View中的所有属性进行皮肤修改
     */
    public void applySkin() {
        for (SkinView skinView : mSkinViews) {
            skinView.applySkin();
        }
    }

    static class SkinView {
        View view;
        //这个View能被换肤的属性与它对应的id
        List<SkinPair> skinPairs;

        SkinView(View mView, List<SkinPair> mSkinPairs) {
            this.view = mView;
            this.skinPairs = mSkinPairs;
        }

        /**
         * 对一个View中的所有的属性进行修改
         */
        public void applySkin() {
            applySkinSupport();
            for (SkinPair skinPair : skinPairs) {
                Drawable left = null, top = null, right = null, bottom = null;
                switch (skinPair.attributeName) {
                    case BACKGROUND:
                        Object background = SkinResources.getInstance().getBackground(skinPair.resId);
                        //背景可能是color，也有可能是drawable
                        if (background instanceof Integer) {
                            view.setBackgroundColor((int) background);
                        } else {
                            ViewCompat.setBackground(view, (Drawable) background);
                        }
                        break;
                    case SRC:
                        background = SkinResources.getInstance().getBackground(skinPair.resId);
                        if (background instanceof Integer) {
                            ((ImageView) view).setImageDrawable(new ColorDrawable((Integer) background));
                        } else {
                            ((ImageView) view).setImageDrawable((Drawable) background);
                        }
                        break;
                    case TEXT_COLOR:
                        ((TextView) view).setTextColor(SkinResources.getInstance().getColorStateList(skinPair.resId));
                        break;
                    case DRAWABLE_LEFT:
                        left = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case DRAWABLE_TOP:
                        top = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case DRAWABLE_RIGHT:
                        right = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case DRAWABLE_BOTTOM:
                        bottom = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                }
                if (left != null || right != null || top != null || bottom != null) {
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
                }
            }

        }

        /**
         * 支持自定义方法
         */
        private void applySkinSupport() {
            if (view instanceof SkinViewSupport) {
                ((SkinViewSupport) view).applySkin();
            }
        }
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

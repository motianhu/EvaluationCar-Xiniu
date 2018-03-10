package com.smona.app.evaluationcar.framework.imageloader;

import android.content.Context;
import android.widget.ImageView;

import com.smona.app.evaluationcar.framework.IProxy;

/**
 * Created by Moth on 2016/12/18.
 */

public class ImageLoaderProxy implements IProxy {
    public static void init(Context context) {
        ImageLoaderManager.getInstance().initImageLoader(context);
    }

    public static void loadImage(String url, ImageView image) {
        ImageLoaderManager.getInstance().loadImage(url, image);
    }

    public static void loadImageDesc(String url, ImageView image) {
        ImageLoaderManager.getInstance().loadImageDesc(url, image);
    }

    public static void loadImageWaterMark(String url, ImageView image) {
        ImageLoaderManager.getInstance().loadImageWaterMark(url, image);
    }

    public static void loadCornerImage(String url, ImageView image) {
        ImageLoaderManager.getInstance().loadCornerImage(url, image);
    }

    public static void loadUrl(String url) {
        ImageLoaderManager.getInstance().loadUrl(url);
    }
}

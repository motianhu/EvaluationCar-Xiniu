package com.smona.app.xiniu.framework.imageloader;

import android.content.Context;
import android.widget.ImageView;

import com.smona.app.xiniu.framework.IProxy;

/**
 * Created by Moth on 2016/12/18.
 */

public class ImageLoaderProxy implements IProxy {
    public static void init(Context context) {
        ImageLoaderManager.getInstance().initImageLoader(context);
    }

    public static void loadCornerImage(String url, ImageView image) {
        ImageLoaderManager.getInstance().loadCornerImage(url, image);
    }
}

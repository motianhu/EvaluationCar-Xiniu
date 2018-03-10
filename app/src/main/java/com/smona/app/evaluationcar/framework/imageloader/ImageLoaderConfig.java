package com.smona.app.evaluationcar.framework.imageloader;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.smona.app.evaluationcar.R;

public class ImageLoaderConfig {

    public static DisplayImageOptions getDefaultOption() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                // 设置图片在下载期间显示的图片
                .showImageOnLoading(R.drawable.ad_empty)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageForEmptyUri(R.drawable.ad_empty)
                // 设置图片加载/解码过程中错误时候显示的图片
                .showImageOnFail(R.drawable.ad_empty)
                // 设置下载的图片是否缓存在内存中
                .cacheInMemory(true)
                .cacheOnDisk(true)
                // 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.ARGB_8888).build();
        return options;
    }

    public static DisplayImageOptions getImageDesc() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                // 设置图片在下载期间显示的图片
                .showImageOnLoading(R.drawable.icon_no_car)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnLoading(R.drawable.icon_no_car)
                // 设置图片加载/解码过程中错误时候显示的图片
                .showImageOnLoading(R.drawable.icon_no_car)
                // 设置下载的图片是否缓存在内存中
                .cacheInMemory(true)
                .cacheOnDisk(true)
                // 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.ARGB_8888).build();
        return options;
    }

    public static DisplayImageOptions getImageWaterMark() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                // 设置图片在下载期间显示的图片
                .showImageOnLoading(R.drawable.transport)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnLoading(R.drawable.transport)
                // 设置图片加载/解码过程中错误时候显示的图片
                .showImageOnLoading(R.drawable.transport)
                // 设置下载的图片是否缓存在内存中
                .cacheInMemory(true)
                .cacheOnDisk(true)
                // 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.ARGB_8888).build();
        return options;
    }

    public static DisplayImageOptions getCornerImage() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                // 设置图片在下载期间显示的图片
                .showImageOnLoading(R.drawable.ad_empty)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageForEmptyUri(R.drawable.ad_empty)
                // 设置图片加载/解码过程中错误时候显示的图片
                .showImageOnFail(R.drawable.ad_empty)
                .displayer(new RoundedBitmapDisplayer(25))
                // 设置下载的图片是否缓存在内存中
                .cacheInMemory(true)
                .cacheOnDisk(true)
                // 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.ARGB_8888).build();
        return options;
    }

    /**
     * 异步图片加载ImageLoader的初始化操作，在Application中调用此方法
     *
     * @param context   上下文对象
     * @param cacheDisc 图片缓存到SDCard的目录，只需要传入SDCard根目录下的子目录即可，默认会建立在SDcard的根目录下
     */
    public static void initImageLoader(Context context, String cacheDisc) {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        // 使用最大可用内存值的1/8作为缓存的大小。
        int cacheSize = maxMemory / 8;
        // 实例化Builder
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                context);
        // 设置线程数量
        builder.threadPoolSize(5);
        // 设定线程等级比普通低一点
        builder.threadPriority(Thread.NORM_PRIORITY - 2);
        // 设定内存缓存为弱缓存
        builder.memoryCache(new LruMemoryCache(cacheSize));
        // builder.memoryCacheSizePercentage(60);
        // 设定内存图片缓存大小限制，不设置默认为屏幕的宽高
        // builder.memoryCacheExtraOptions(480, 800);
        // 设定只保存同一尺寸的图片在内存
        builder.denyCacheImageMultipleSizesInMemory();
        // 设定缓存的SDcard目录，UnlimitDiscCache速度最快
        // 设置ImageLoader的配置参数
        builder.defaultDisplayImageOptions(getDefaultOption());

        // 初始化ImageLoader
        ImageLoader.getInstance().init(builder.build());
        ImageLoader.getInstance().handleSlowNetwork(true);
    }
}

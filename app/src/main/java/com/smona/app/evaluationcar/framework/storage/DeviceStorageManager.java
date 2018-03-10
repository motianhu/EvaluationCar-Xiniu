package com.smona.app.evaluationcar.framework.storage;

import android.content.Context;
import android.os.Environment;

import com.smona.app.evaluationcar.util.FileUtils;
import com.smona.app.evaluationcar.util.FolderContants;

import java.io.File;

/**
 * Created by Moth on 2017/4/6.
 */

public class DeviceStorageManager {
    private static final String TAG = DeviceStorageManager.class.getSimpleName();
    private static final String TEST_FILE = "zhixintest0123456789";
    private volatile static DeviceStorageManager sInstance;
    private boolean mTestEvn = false;
    private Context mAppContext;
    private String mAppCachePath;
    private String mPrefixPath;
    private String mThumbnailPath;
    private String mMd5Path;
    private String mTempPath;


    private DeviceStorageManager() {
    }

    public static DeviceStorageManager getInstance() {
        if (sInstance == null) {
            sInstance = new DeviceStorageManager();
        }
        return sInstance;
    }

    private static boolean makeFolder(String folder) {
        return FileUtils.makeFolders(folder);
    }

    public void setContext(Context context) {
        mAppContext = context;
        mAppCachePath = context.getCacheDir().getAbsolutePath();
    }

    public void initPath() {
        mPrefixPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String rootPath = mPrefixPath + File.separator + FolderContants.ROOT;
        makeFolder(rootPath + File.separator);
        mMd5Path = rootPath + File.separator + FolderContants.MD5CACHE;
        boolean success = makeFolder(mMd5Path + File.separator);
        mThumbnailPath = rootPath + File.separator + FolderContants.THUMBNAIL;
        success = makeFolder(mThumbnailPath + File.separator);
        mTempPath = rootPath + File.separator + FolderContants.TEMP;
        success = makeFolder(mTempPath + File.separator);
        mTestEvn = new File(mPrefixPath + File.separator + TEST_FILE).exists();
    }

    public String getMd5Path() {
        return mMd5Path;
    }

    public String getThumbnailPath() {
        return mThumbnailPath;
    }

    public String getTemp() {
        return mTempPath;
    }

    public boolean isTestEvn() {
        return mTestEvn;
    }
}

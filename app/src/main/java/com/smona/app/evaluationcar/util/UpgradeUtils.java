package com.smona.app.evaluationcar.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.smona.app.evaluationcar.framework.storage.DeviceStorageManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Moth on 2017/4/13.
 */

public class UpgradeUtils {
    public static final String UPGRADE = "upgrade_service";
    public static final int UPGRADE_NORMAL = 0;
    public static final int UPGRADE_SETTING = 1;
    private static final String TAG = UpgradeUtils.class.getSimpleName();

    public static File downloadApk(String path, ProgressDialog pd) {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = null;
            try {
                url = new URL(path);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                //获取到文件的大小
                pd.setMax(conn.getContentLength());
                InputStream is = conn.getInputStream();
                File file = new File(DeviceStorageManager.getInstance().getTemp(), "temp.apk.bak");
                FileOutputStream fos = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024];
                int len;
                int total = 0;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    total += len;
                    pd.setProgress(total);
                }
                fos.close();
                bis.close();
                is.close();
                FileUtils.copyFile(file.getPath(), DeviceStorageManager.getInstance().getTemp() + "rhino.apk");
                return new File(DeviceStorageManager.getInstance().getTemp() + "rhino.apk");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void installApk(final Context context, File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static boolean compareVersion(String versionServer, String versionLocal) {
        String server = versionServer;
        String local = versionLocal;
        if (server == null || server.length() == 0 || local == null || local.length() == 0) {
            CarLog.d(TAG, "params valid! server=" + server + ",local=" + local);
            return false;
        }

        int index1 = 0;
        int index2 = 0;
        while (index1 < server.length() && index2 < local.length()) {
            int[] numberServer = getValue(server, index1);
            int[] numberLocal = getValue(local, index2);

            if (numberServer[0] < numberLocal[0]) {
                return false;
            } else if (numberServer[0] > numberLocal[0]) {
                return true;
            } else {
                index1 = numberServer[1] + 1;
                index2 = numberLocal[1] + 1;
            }
        }
        if (index1 == server.length() && index2 == local.length()) {
            return false;
        }
        if (index1 < server.length()) {
            return true;
        } else {
            return false;
        }
    }

    private static int[] getValue(String version, int index) {
        int[] value_index = new int[2];
        StringBuilder sb = new StringBuilder();
        while (index < version.length() && version.charAt(index) != '.') {
            sb.append(version.charAt(index));
            index++;
        }
        value_index[0] = Integer.parseInt(sb.toString());
        value_index[1] = index;

        return value_index;
    }
}

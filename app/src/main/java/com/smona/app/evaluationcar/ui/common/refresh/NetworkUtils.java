package com.smona.app.evaluationcar.ui.common.refresh;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class NetworkUtils {

    public static boolean isNetWorkOk(Context context, boolean toSetting) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null) {
            if (wifiInfo.getState() == NetworkInfo.State.CONNECTED ||
                    wifiInfo.getState() == NetworkInfo.State.CONNECTING) {
                return true;
            }
        }

        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileInfo != null) {
            if (mobileInfo.getState() == NetworkInfo.State.CONNECTED ||
                    mobileInfo.getState() == NetworkInfo.State.CONNECTING) {
                return true;
            }
        }

        NetworkInfo bluetoothInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);
        if (bluetoothInfo != null) {
            if (bluetoothInfo.getState() == NetworkInfo.State.CONNECTED ||
                    bluetoothInfo.getState() == NetworkInfo.State.CONNECTING) {
                return true;
            }
        }

        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            return info.isAvailable();
        }

        if (toSetting) {
            try {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null) {
            if (wifiInfo.getState() == NetworkInfo.State.CONNECTED ||
                    wifiInfo.getState() == NetworkInfo.State.CONNECTING) {
                return true;
            }
        }

        return false;
    }

    public static boolean isMobileConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type != null && type.equalsIgnoreCase("MOBILE")) {
                return true;
            }
        }
        return false;
    }
}

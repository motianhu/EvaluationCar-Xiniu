package com.smona.app.evaluationcar.util;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by Moth on 2017/8/27.
 */

public class AccountManager {
    private static final String TAG = AccountManager.class.getSimpleName();


    public static void saveLastAccount(Context context, String id, String pwd) {
        try {
            String userName = Base64Utils.encrypt(id);
            String password = Base64Utils.encrypt(pwd);
            CarLog.d(TAG, "saveLastAccount userName=" + userName + ", password=" + password);
            SPUtil.put(context, CacheContants.LOGIN_LAST_USERNAME, userName);
            SPUtil.put(context, CacheContants.LOGIN_LAST_PASSWORD, password);

        } catch (Exception e) {
            e.printStackTrace();
            CarLog.d(TAG, "saveLastAccount e=" + e);
        }
    }

    public static String[] readLastAccount(Context context) {
        String userName = (String) SPUtil.get(context, CacheContants.LOGIN_LAST_USERNAME, "");
        String password = (String) SPUtil.get(context, CacheContants.LOGIN_LAST_PASSWORD, "");

        String[] result = null;
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            return result;
        }
        try {
            userName = Base64Utils.decrypt(userName);
            password = Base64Utils.decrypt(password);
            result = new String[2];
            result[0] = userName;
            result[1] = password;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            CarLog.d(TAG, "readLastAccount e=" + e);
        }
        return result;
    }
}

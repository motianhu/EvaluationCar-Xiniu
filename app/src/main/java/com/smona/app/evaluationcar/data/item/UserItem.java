package com.smona.app.evaluationcar.data.item;

import android.content.Context;
import android.text.TextUtils;

import com.smona.app.evaluationcar.data.bean.UserInfoBean;
import com.smona.app.evaluationcar.util.Base64Utils;
import com.smona.app.evaluationcar.util.CacheContants;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.SPUtil;

public class UserItem {
    private static final String TAG = UserItem.class.getSimpleName();

    //login
    public String mId;
    public String mPwd;

    public UserInfoBean userBean;

    public void saveSelf(Context context, String id, String pwd) {
        try {
            String userName = Base64Utils.encrypt(id);
            String password = Base64Utils.encrypt(pwd);
            CarLog.d(TAG, "saveSelf userName=" + userName + ", password=" + password);
            SPUtil.put(context, CacheContants.LOGIN_USERNAME, userName);
            SPUtil.put(context, CacheContants.LOGIN_PASSWORD, password);

        } catch (Exception e) {
            e.printStackTrace();
            CarLog.d(TAG, "saveSelf e=" + e);
        }
    }

    public boolean readSelf(Context context) {
        String userName = (String) SPUtil.get(context, CacheContants.LOGIN_USERNAME, "");
        String password = (String) SPUtil.get(context, CacheContants.LOGIN_PASSWORD, "");

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            return false;
        }
        try {
            mId = Base64Utils.decrypt(userName);
            mPwd = Base64Utils.decrypt(password);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            CarLog.d(TAG, "readSelf e=" + e);
        }
        return false;
    }

    public void saveUserProp(Context context, UserInfoBean bean) {
        SPUtil.put(context, CacheContants.LOGIN_USERID, bean.userId);
        SPUtil.put(context, CacheContants.LOGIN_USERCOMPANY, bean.userCompany);
        SPUtil.put(context, CacheContants.LOGIN_USERSUPERCOMPONAY, bean.userSuperCompany);
        SPUtil.put(context, CacheContants.LOGIN_USERCHINESENAME, bean.userChineseName);
        SPUtil.put(context, CacheContants.LOGIN_COMPANYNAME, bean.companyName);
    }

    public void readUserProp(Context context) {
        userBean = new UserInfoBean();
        userBean.userId = (int) SPUtil.get(context, CacheContants.LOGIN_USERID, -1);
        userBean.userCompany = (int) SPUtil.get(context, CacheContants.LOGIN_USERCOMPANY, -1);
        userBean.userSuperCompany = (int) SPUtil.get(context, CacheContants.LOGIN_USERSUPERCOMPONAY, -1);
        userBean.userChineseName = (String) SPUtil.get(context, CacheContants.LOGIN_USERCHINESENAME, "");
        userBean.companyName = (String) SPUtil.get(context, CacheContants.LOGIN_COMPANYNAME, "");
    }
}

package com.smona.app.evaluationcar.ui.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.item.UserItem;
import com.smona.app.evaluationcar.framework.cache.CacheDelegator;
import com.smona.app.evaluationcar.framework.storage.DeviceStorageManager;
import com.smona.app.evaluationcar.ui.HomeActivity;
import com.smona.app.evaluationcar.ui.LoginActivity;
import com.smona.app.evaluationcar.ui.common.activity.BaseActivity;
import com.smona.app.evaluationcar.util.ActivityUtils;
import com.smona.app.evaluationcar.util.CacheContants;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.ToastUtils;
import com.smona.app.evaluationcar.util.UrlConstants;
import com.smona.app.evaluationcar.util.Utils;

/**
 * Created by Moth on 2016/12/18.
 */

public class MineLayer extends LinearLayout implements View.OnClickListener {
    private static final String TAG = MineLayer.class.getSimpleName();

    //升级
    private AlertDialog mUpgradeDialog;

    private UserItem mUserItem;


    public MineLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    public void init() {
        mUserItem = new UserItem();
        mUserItem.readSelf(getContext());
        mUserItem.readUserProp(getContext());

        TextView company = (TextView) findViewById(R.id.belong_company);
        String companyFormat = getResources().getString(R.string.fragment_mine_belong_company);
        companyFormat = String.format(companyFormat, mUserItem.userBean.companyName);
        company.setText(companyFormat);

        TextView chineseName = (TextView) findViewById(R.id.mine_name);
        String accountFormat = getResources().getString(R.string.fragment_mine_account);
        accountFormat = String.format(accountFormat, mUserItem.userBean.userChineseName);
        chineseName.setText(accountFormat);

        String versionName = Utils.getVersion(getContext());
        TextView version = (TextView) findViewById(R.id.version);
        if(DeviceStorageManager.getInstance().isTestEvn()) {
            versionName ="testevn " + versionName;
        }
        String versionFormat = getResources().getString(R.string.fragment_mine_account);
        versionFormat = String.format(versionFormat, versionName);
        version.setText(versionFormat);

        CarLog.d(TAG, "userBean: " + mUserItem.userBean);

        findViewById(R.id.setting_about).setOnClickListener(this);
        findViewById(R.id.setting_account).setOnClickListener(this);
        findViewById(R.id.setting_logout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.setting_about:
                ActivityUtils.jumpOnlyActivity(getContext(), SettingActivity.class);
                break;
            case R.id.setting_account:
                ActivityUtils.jumpOnlyActivity(getContext(), MineActivity.class);
                break;
            case R.id.setting_logout:
                //弹出对话框，退出
                showDialog();
                break;
        }
    }

    private void showDialog() {
        Context context = getContext();
        AlertDialog.Builder builer = new AlertDialog.Builder(context);
        builer.setTitle(R.string.quit_title);
        builer.setPositiveButton(R.string.upgrade_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String key = UrlConstants.getInterface(UrlConstants.CHECK_USER) + "?userName=" + mUserItem.mId;
                mUserItem.saveSelf(getContext(), "", "");
                CacheDelegator.getInstance().deleteCache(key);
                ActivityUtils.jumpOnlyActivity(getContext(), LoginActivity.class);
                ((BaseActivity) getContext()).finish();
            }
        });

        closeDialog();
        mUpgradeDialog = builer.create();
        mUpgradeDialog.show();
    }

    private void closeDialog() {
        if (mUpgradeDialog != null) {
            mUpgradeDialog.dismiss();
            mUpgradeDialog = null;
        }
    }

}

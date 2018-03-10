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

        findViewById(R.id.mine_image).setOnClickListener(this);
        TextView name = (TextView) findViewById(R.id.mine_name);
        name.setText(mUserItem.userBean.userChineseName);

        CarLog.d(TAG, "userBean: " + mUserItem.userBean);

        findViewById(R.id.setting_info).setOnClickListener(this);
        findViewById(R.id.setting_update).setOnClickListener(this);
        findViewById(R.id.setting_about).setOnClickListener(this);
        findViewById(R.id.setting_phone).setOnClickListener(this);
        findViewById(R.id.setting_logout).setOnClickListener(this);

        String version = Utils.getVersion(getContext());
        if(DeviceStorageManager.getInstance().isTestEvn()) {
            version ="testevn: " + version;
        }
        ((TextView) findViewById(R.id.version)).setText(version);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.setting_info:
            case R.id.mine_image:
                ActivityUtils.jumpOnlyActivity(getContext(), MineActivity.class);
                break;
            case R.id.setting_update:
                ((HomeActivity) getContext()).requestUpgradeInfo(true);
                ToastUtils.show(getContext(), R.string.upgrading);
                break;
            case R.id.setting_about:
                ActivityUtils.jumpOnlyActivity(getContext(), SettingActivity.class);
                break;
            case R.id.setting_phone:
                ActivityUtils.callPhone(getContext(), getContext().getString(R.string.mine_telephone));
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

package com.smona.app.xiniu.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.smona.app.xiniu.R;
import com.smona.app.xiniu.business.ResponseCallback;
import com.smona.app.xiniu.data.event.UpgradeEvent;
import com.smona.app.xiniu.data.event.background.UpgradeSubEvent;
import com.smona.app.xiniu.data.model.ResUpgradeApi;
import com.smona.app.xiniu.framework.cache.DataDelegator;
import com.smona.app.xiniu.framework.event.EventProxy;
import com.smona.app.xiniu.framework.json.JsonParse;
import com.smona.app.xiniu.ui.common.NoScrollViewPager;
import com.smona.app.xiniu.ui.common.activity.UserActivity;
import com.smona.app.xiniu.ui.home.fragment.HomeFragmentPagerAdapter;
import com.smona.app.xiniu.util.ActivityUtils;
import com.smona.app.xiniu.util.CarLog;
import com.smona.app.xiniu.util.ToastUtils;
import com.smona.app.xiniu.util.UpgradeUtils;
import com.smona.app.xiniu.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * Created by Moth on 2016/12/15.
 */

public class HomeActivity extends UserActivity implements RadioGroup.OnCheckedChangeListener {
    //几个代表页面的常量
    public static final int PAGE_LIST = 0;
    public static final int PAGE_SETTING = 1;
    private static final String TAG = HomeActivity.class.getSimpleName();
    //UI Objects
    private RadioGroup mRbGroup;
    private RadioButton[] mRadioFunc = new RadioButton[2];
    private NoScrollViewPager mViewPager;
    private HomeFragmentPagerAdapter mFragmentAdapter;

    //upgrade
    private AlertDialog mUpgradeDialog;
    private ProgressDialog mUpgradeProcess;
    private boolean mShowToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
        initDatas();
        startUploadService();
        EventProxy.register(this);
    }

    private void startUploadService() {
        ActivityUtils.startUpService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventProxy.unregister(this);
    }

    private void initViews() {
        mFragmentAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager = (NoScrollViewPager) findViewById(R.id.vp_home);
        mViewPager.setNoScroll(true);
        mViewPager.setAdapter(mFragmentAdapter);

        mRbGroup = (RadioGroup) findViewById(R.id.rg_home);
        mRbGroup.setOnCheckedChangeListener(this);

        mRadioFunc[0] = (RadioButton) findViewById(R.id.rb_list);
        mRadioFunc[1] = (RadioButton) findViewById(R.id.rb_setting);

        changeFragment(PAGE_LIST, R.string.home_fragment_evaluation);
    }

    private void initDatas() {
        requestUpgradeInfo(false);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_list:
                changeFragment(PAGE_LIST, R.string.home_fragment_list);
                break;
            case R.id.rb_setting:
                changeFragment(PAGE_SETTING, R.string.home_fragment_setting);
                break;
        }
    }

    private void changeFragment(int pageHome, int titleId) {
        mViewPager.setCurrentItem(pageHome, false);
        mRadioFunc[pageHome].setChecked(true);
    }

    /*************升级*********/
    public void requestUpgradeInfo(boolean showToast) {
        mShowToast = showToast;
        DataDelegator.getInstance().requestUpgradeInfo(mUpgradeCallback);
    }

    private ResponseCallback<String> mUpgradeCallback = new ResponseCallback<String>() {
        @Override
        public void onFailed(String error) {
            CarLog.d(TAG, "onFailed error=" + error);
        }

        @Override
        public void onSuccess(String content) {
            ResUpgradeApi newBaseApi = JsonParse.parseJson(content, ResUpgradeApi.class);
            CarLog.d(TAG, "mUpgradeCallback onSuccess result=" + content);
            if (newBaseApi != null) {
                if (UpgradeUtils.compareVersion(newBaseApi.versionName, Utils.getVersion(HomeActivity.this))) {
                    UpgradeEvent upgradeEvent = new UpgradeEvent();
                    upgradeEvent.mResBaseApi = newBaseApi;
                    upgradeEvent.action = UpgradeEvent.DIALOG;
                    EventProxy.post(upgradeEvent);
                } else {
                    UpgradeEvent upgradeEvent = new UpgradeEvent();
                    upgradeEvent.action = UpgradeEvent.TOAST;
                    EventProxy.post(upgradeEvent);
                }
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(UpgradeEvent event) {
        CarLog.d(TAG, "update");
        if (event.action == UpgradeEvent.DIALOG) {
            showUpdataDialog(this, event.mResBaseApi);
        } else {
            if (mShowToast) {
                ToastUtils.show(this, R.string.upgrade_new);
            }
        }
    }

    private void showUpdataDialog(final Context context, final ResUpgradeApi upgrade) {
        AlertDialog.Builder builer = new AlertDialog.Builder(context);
        builer.setTitle(R.string.upgrade_title);
        String content = context.getResources().getString(R.string.upgrade_desc);
        content = String.format(content, upgrade.versionName);
        builer.setMessage(content);
        final boolean isForce = ResUpgradeApi.UPDATE_TYPE_FORCE.equalsIgnoreCase(upgrade.updateType);
        builer.setPositiveButton(R.string.upgrade_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                CarLog.d(TAG, "下载apk,更新");
                downLoadApk(context, upgrade);
            }
        });
        builer.setNegativeButton(R.string.upgrade_cancle, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (!isForce) {
                    return;
                }
                System.exit(0);
            }
        });

        closeDialog();
        mUpgradeDialog = builer.create();
        if (isForce) {
            mUpgradeDialog.setOnKeyListener(mOnKeyListener);
        }
        mUpgradeDialog.setCanceledOnTouchOutside(!isForce);
        mUpgradeDialog.show();
    }

    private void downLoadApk(final Context context, final ResUpgradeApi upgrade) {
        closeProgress();
        final boolean isForce = ResUpgradeApi.UPDATE_TYPE_FORCE.equalsIgnoreCase(upgrade.updateType);
        mUpgradeProcess = new ProgressDialog(context);
        mUpgradeProcess.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mUpgradeProcess.setMessage("正在下载更新");
        if (isForce) {
            mUpgradeProcess.setOnKeyListener(mOnKeyListener);
        }
        mUpgradeProcess.setCanceledOnTouchOutside(!isForce);
        mUpgradeProcess.show();


        UpgradeSubEvent subEvent = new UpgradeSubEvent();
        subEvent.mResBaseApi = upgrade;
        EventProxy.post(subEvent);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void downloadApkSub(UpgradeSubEvent event) {
        File file = UpgradeUtils.downloadApk(event.mResBaseApi.apiURL, mUpgradeProcess);
        CarLog.d(TAG, "downloadApkSub " + file);
        UpgradeUtils.installApk(this, file);
        closeDialog();
        closeProgress();
    }

    private DialogInterface.OnKeyListener mOnKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return true;
            }
            return false;
        }
    };


    private void closeDialog() {
        if (mUpgradeDialog != null) {
            mUpgradeDialog.dismiss();
            mUpgradeDialog = null;
        }
    }

    private void closeProgress() {
        if (mUpgradeProcess != null) {
            mUpgradeProcess.dismiss();
            mUpgradeProcess = null;
        }
    }

}

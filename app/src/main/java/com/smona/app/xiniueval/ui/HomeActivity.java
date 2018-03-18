package com.smona.app.xiniueval.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.smona.app.xiniueval.R;
import com.smona.app.xiniueval.business.ResponseCallback;
import com.smona.app.xiniueval.data.bean.ImageMetaBean;
import com.smona.app.xiniueval.data.event.UpgradeEvent;
import com.smona.app.xiniueval.data.event.background.UpgradeSubEvent;
import com.smona.app.xiniueval.data.model.ResImageMetaArray;
import com.smona.app.xiniueval.data.model.ResUpgradeApi;
import com.smona.app.xiniueval.framework.cache.DataDelegator;
import com.smona.app.xiniueval.framework.event.EventProxy;
import com.smona.app.xiniueval.framework.imageloader.ImageLoaderProxy;
import com.smona.app.xiniueval.framework.json.JsonParse;
import com.smona.app.xiniueval.framework.provider.DBDelegator;
import com.smona.app.xiniueval.ui.common.NoScrollViewPager;
import com.smona.app.xiniueval.ui.common.activity.UserActivity;
import com.smona.app.xiniueval.ui.evaluation.EvaluationActivity;
import com.smona.app.xiniueval.ui.home.fragment.HomeFragmentPagerAdapter;
import com.smona.app.xiniueval.util.ActivityUtils;
import com.smona.app.xiniueval.util.CarLog;
import com.smona.app.xiniueval.util.StatusUtils;
import com.smona.app.xiniueval.util.ToastUtils;
import com.smona.app.xiniueval.util.UpgradeUtils;
import com.smona.app.xiniueval.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * Created by Moth on 2016/12/15.
 */

public class HomeActivity extends UserActivity implements RadioGroup.OnCheckedChangeListener {
    //几个代表页面的常量
    public static final int PAGE_NOSUBMIT = 0;
    public static final int PAGE_SUBMITED = 1;
    public static final int PAGE_KEFU = 2;
    public static final int PAGE_SETTING = 3;
    private static final String TAG = HomeActivity.class.getSimpleName();
    //UI Objects
    private RadioButton[] mRadioFunc = new RadioButton[4];
    private NoScrollViewPager mViewPager;

    //upgrade
    private AlertDialog mUpgradeDialog;
    private ProgressDialog mUpgradeProcess;
    private boolean mShowToast;
    private ResponseCallback<String> mImageMetaCallback = new ResponseCallback<String>() {
        @Override
        public void onFailed(String error) {
            CarLog.d(TAG, "onFailed error=" + error);
        }

        @Override
        public void onSuccess(String content) {
            ResImageMetaArray imageMetas = JsonParse.parseJson(content, ResImageMetaArray.class);
            if (imageMetas.data != null && imageMetas.data.size() > 0) {
                for (ImageMetaBean bean : imageMetas.data) {
                    boolean success = DBDelegator.getInstance().insertImageMeta(bean);
                    if (success) {
                        continue;
                    }
                    DBDelegator.getInstance().updateImageMeta(bean);
                    ImageLoaderProxy.loadUrl(bean.imageDesc);
                    ImageLoaderProxy.loadUrl(bean.waterMark);
                }
            }
        }
    };
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
    private DialogInterface.OnKeyListener mOnKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            return keyCode == KeyEvent.KEYCODE_BACK;
        }
    };

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
        HomeFragmentPagerAdapter fragmentAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager = (NoScrollViewPager) findViewById(R.id.vp_home);
        mViewPager.setNoScroll(true);
        mViewPager.setAdapter(fragmentAdapter);

        RadioGroup rbGroup = (RadioGroup) findViewById(R.id.rg_home);
        rbGroup.setOnCheckedChangeListener(this);

        mRadioFunc[0] = (RadioButton) findViewById(R.id.rb_nosubmit);
        mRadioFunc[1] = (RadioButton) findViewById(R.id.rb_submited);
        mRadioFunc[2] = (RadioButton) findViewById(R.id.rb_kefu);
        mRadioFunc[3] = (RadioButton) findViewById(R.id.rb_setting);

        findViewById(R.id.rb_evaluation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.jumpEvaluation(HomeActivity.this, StatusUtils.BILL_STATUS_NONE, "", 0, false, EvaluationActivity.class);
            }
        });

        changeFragment(PAGE_NOSUBMIT, R.string.home_fragment_nosubmit);
    }

    private void initDatas() {
        requestImageMetas();
        requestUpgradeInfo(false);
    }

    private void requestImageMetas() {
        DataDelegator.getInstance().requestImageMeta(mImageMetaCallback);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_nosubmit:
                changeFragment(PAGE_NOSUBMIT, R.string.home_fragment_nosubmit);
                break;
            case R.id.rb_submited:
                changeFragment(PAGE_SUBMITED, R.string.home_fragment_submited);
                break;
            case R.id.rb_kefu:
                changeFragment(PAGE_KEFU, R.string.home_fragment_evaluation);
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

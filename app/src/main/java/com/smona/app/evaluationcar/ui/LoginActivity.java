package com.smona.app.evaluationcar.ui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hyphenate.chat.ChatClient;
import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.business.param.UserParam;
import com.smona.app.evaluationcar.data.item.UserItem;
import com.smona.app.evaluationcar.data.model.ResUserModel;
import com.smona.app.evaluationcar.framework.EvaluationApp;
import com.smona.app.evaluationcar.framework.cache.DataDelegator;
import com.smona.app.evaluationcar.framework.chatclient.ChatClientProxy;
import com.smona.app.evaluationcar.framework.json.JsonParse;
import com.smona.app.evaluationcar.ui.common.activity.PermissionActivity;
import com.smona.app.evaluationcar.util.AccountManager;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.ToastUtils;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class LoginActivity extends PermissionActivity implements OnClickListener {
    protected static final String TAG = LoginActivity.class.getSimpleName();
    private LinearLayout mLoginLinearLayout; // 登录内容的容器
    private Animation mTranslate; // 位移动画
    private Dialog mLoginingDlg; // 显示正在登录的Dialog
    private EditText mIdEditText; // 登录ID编辑框
    private EditText mPwdEditText; // 登录密码编辑框
    private Button mLoginButton; // 登录按钮
    private String mIdString;
    private String mPwdString;
    private UserItem mUser; // 用户列表
    private View mRegisterView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initUser();
        super.onCreate(savedInstanceState);
        if (mUser == null) {
            prepareSkip();
        }
    }

    public void onPermissionOk() {
        if (mUser != null) {
            gotoStartup();
        }
    }

    private void prepareSkip() {
        setContentView(R.layout.activity_login);
        initView();
        setListener();
        initAnim();
    }

    private void initAnim() {
        mTranslate = AnimationUtils.loadAnimation(this, R.anim.my_translate); // 初始化动画对象
        mLoginLinearLayout.startAnimation(mTranslate); // Y轴水平移动
    }

    private void initUser() {
        mUser = new UserItem();
        mUser.readSelf(this);
        if (TextUtils.isEmpty(mUser.mId) || TextUtils.isEmpty(mUser.mPwd)) {
            mUser = null;
            return;
        }
    }

    private void setListener() {
        mIdEditText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mIdString = s.toString();
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        mPwdEditText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mPwdString = s.toString();
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        mLoginButton.setOnClickListener(this);
        mRegisterView.setOnClickListener(this);
    }

    private void initView() {
        mIdEditText = (EditText) findViewById(R.id.login_edtId);
        mPwdEditText = (EditText) findViewById(R.id.login_edtPwd);
        mLoginButton = (Button) findViewById(R.id.login_btnLogin);
        mLoginLinearLayout = (LinearLayout) findViewById(R.id.login_linearLayout);
        mRegisterView = findViewById(R.id.register);

        initLoginingDlg();
    }

    /* 初始化正在登录对话框 */
    private void initLoginingDlg() {

        mLoginingDlg = new Dialog(this, R.style.loginingDlg);
        mLoginingDlg.setContentView(R.layout.logining_dlg);

        Window window = mLoginingDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        // 获取和mLoginingDlg关联的当前窗口的属性，从而设置它在屏幕中显示的位置

        // 获取屏幕的高宽
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int cxScreen = dm.widthPixels;
        int cyScreen = dm.heightPixels;

        int height = (int) getResources().getDimension(
                R.dimen.loginingdlg_height);// 高42dp
        int lrMargin = (int) getResources().getDimension(
                R.dimen.loginingdlg_lr_margin); // 左右边沿10dp
        int topMargin = (int) getResources().getDimension(
                R.dimen.loginingdlg_top_margin); // 上沿20dp

        params.y = (-(cyScreen - height) / 2) + topMargin; // -199
        /* 对话框默认位置在屏幕中心,所以x,y表示此控件到"屏幕中心"的偏移量 */

        params.width = cxScreen;
        params.height = height;
        // width,height表示mLoginingDlg的实际大小

        mLoginingDlg.setCanceledOnTouchOutside(true); // 设置点击Dialog外部任意区域关闭Dialog
    }

    /* 显示正在登录对话框 */
    private void showLoginingDlg() {
        if (mLoginingDlg != null)
            mLoginingDlg.show();
    }

    /* 关闭正在登录对话框 */
    private void closeLoginingDlg() {
        if (mLoginingDlg != null && mLoginingDlg.isShowing())
            mLoginingDlg.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btnLogin:
                // 启动登录
                CarLog.d(TAG, mIdString + "  " + mPwdString + ", mUser: " + mUser);
                if (mIdString == null || mIdString.equals("")) { // 账号为空时
                    ToastUtils.show(LoginActivity.this, R.string.login_input_account_error);
                } else if (mPwdString == null || mPwdString.equals("")) {// 密码为空时
                    ToastUtils.show(LoginActivity.this, R.string.login_input_password_error);
                } else {// 账号和密码都不为空时

                    //如果上一次账号和此次账号不同，需要用户确认是否删除本地所有数据。
                    String[] lastAccount = AccountManager.readLastAccount(this);
                    CarLog.d(TAG, "lastAccount  " + lastAccount);
                    if (lastAccount != null) {
                        CarLog.d(TAG, "lastAccount[0]  " + lastAccount[0]);
                        if (!mIdString.equals(lastAccount[0])) {
                            showHintForClearData();
                            return;
                        }
                    }
                    requestLogin();
                }
                break;
            case R.id.register:
                gotoRegister();
                break;
            default:
                break;
        }

    }

    private void showHintForClearData() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setTitle(R.string.waring_title);
        builer.setMessage(R.string.waring_content);
        builer.setPositiveButton(R.string.upgrade_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((EvaluationApp)getApplicationContext()).clearAllTableData();
                requestLogin();
            }
        }).setNegativeButton(R.string.upgrade_cancle, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                requestLogin();
            }
        });

        AlertDialog clearDataDialog  = builer.create();
        clearDataDialog.show();
    }

    private void requestLogin() {
        showLoginingDlg(); // 显示"正在登录"对话框
        final UserParam param = new UserParam();
        param.userName = mIdString;
        param.password = mPwdString;

        DataDelegator.getInstance().checkUser(param, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResUserModel normal = JsonParse.parseJson(result, ResUserModel.class);
                CarLog.d(TAG, "onSuccess normal: " + normal);
                if (normal.success) {
                    registerThirdFunc();
                }
                runUI(normal);
            }

            @Override
            public void onFailed(String error) {
                CarLog.d(TAG, "onError ex: " + error);
                runUI(null);
            }
        });
    }

    private void runUI(final ResUserModel model) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeLoginingDlg();// 关闭对话框
                if (model != null && model.success) {
                    mUser = new UserItem();
                    mUser.saveSelf(LoginActivity.this, mIdString, mPwdString);
                    mUser.saveUserProp(LoginActivity.this, model.object);
                    AccountManager.saveLastAccount(LoginActivity.this, mIdString, mPwdString);
                    gotoStartup();
                } else {
                    ToastUtils.show(LoginActivity.this, R.string.login_error);
                }
            }
        });
    }

    private void gotoStartup() {
        Intent intent = new Intent();
        intent.setClass(this, StartupActivity.class);
        startActivity(intent);
        finish();
    }

    private void gotoRegister() {
        Intent intent = new Intent();
        intent.setClass(this, RegisterActivity.class);
        startActivity(intent);
    }


    private void registerThirdFunc() {
        //register jpush
        JPushInterface.setAlias(LoginActivity.this, mIdString, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                CarLog.d(TAG, "jpush register alias i=" + i);
            }
        });

        //register huanxin kefu
        //可以检测是否已经登录过环信，如果登录过则环信SDK会自动登录，不需要再次调用登录操作
        if (ChatClient.getInstance().isLoggedInBefore()) {

        } else {
            // 自动生成账号,此处每次都随机生成一个账号,为了演示.正式应从自己服务器获取账号
            final String account = ChatClientProxy.getInstance().getRandomAccount();
            final String userPwd = "123456";
            ChatClientProxy.getInstance().createChatAccount(account, userPwd);
        }
    }
}

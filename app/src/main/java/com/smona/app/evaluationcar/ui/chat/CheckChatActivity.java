package com.smona.app.evaluationcar.ui.chat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.Error;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.hyphenate.helpdesk.model.ContentFactory;
import com.hyphenate.helpdesk.model.QueueIdentityInfo;
import com.hyphenate.helpdesk.model.VisitorInfo;
import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.item.UserItem;
import com.smona.app.evaluationcar.framework.chatclient.ChatClientProxy;
import com.smona.app.evaluationcar.ui.common.activity.UserActivity;

/**
 * Created by motianhu on 5/8/17.
 */

public class CheckChatActivity extends UserActivity {

    private static final String TAG = "CheckChatActivity";

    private boolean mIsProgressShow;
    private ProgressDialog mProgressDialog;
    private UserItem mUserItem;

    public static VisitorInfo createVisitorInfo(String name) {
        VisitorInfo info = ContentFactory.createVisitorInfo(null);
        info.name(name).nickName(name);
        return info;
    }

    public static QueueIdentityInfo createQueueIdentity(String queueName) {
        if (TextUtils.isEmpty(queueName)) {
            return null;
        }
        QueueIdentityInfo info = ContentFactory.createQueueIdentityInfo(null);
        info.queueName(queueName);
        return info;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mUserItem = new UserItem();
        mUserItem.readSelf(this);
        //ChatClient.getInstance().isLoggedInBefore() 可以检测是否已经登录过环信，如果登录过则环信SDK会自动登录，不需要再次调用登录操作
        if (ChatClient.getInstance().isLoggedInBefore()) {
            mProgressDialog = getmProgressDialog();
            mProgressDialog.setMessage(getResources().getString(R.string.is_contact_customer));
            mProgressDialog.show();
            toChatActivity();
        } else {
            //随机创建一个用户并登录环信服务器
            createRandomAccountThenLoginChatServer();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        UIProvider.getInstance().pushActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UIProvider.getInstance().popActivity(this);
    }

    private void createRandomAccountThenLoginChatServer() {
        // 自动生成账号,此处每次都随机生成一个账号,为了演示.正式应从自己服务器获取账号
        final String account = ChatClientProxy.getInstance().getRandomAccount();
        final String userPwd = "123456";
        mProgressDialog = getmProgressDialog();
        mProgressDialog.setMessage(getString(R.string.system_is_regist));
        mProgressDialog.show();
        // createAccount to huanxin server
        // if you have a account, this step will ignore
        ChatClient.getInstance().createAccount(account, userPwd, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "demo register success");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //登录环信服务器
                        login(account, userPwd);
                    }
                });
            }

            @Override
            public void onError(final int errorCode, String error) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        if (errorCode == Error.NETWORK_ERROR) {
                            Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT).show();
                        } else if (errorCode == Error.USER_ALREADY_EXIST) {
                            Toast.makeText(getApplicationContext(), "用户已经存在", Toast.LENGTH_SHORT).show();
                        } else if (errorCode == Error.USER_AUTHENTICATION_FAILED) {
                            Toast.makeText(getApplicationContext(), "无开放注册权限", Toast.LENGTH_SHORT).show();
                        } else if (errorCode == Error.USER_ILLEGAL_ARGUMENT) {
                            Toast.makeText(getApplicationContext(), "用户名非法", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.register_user_fail), Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    private ProgressDialog getmProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(CheckChatActivity.this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mIsProgressShow = false;
                }
            });
        }
        return mProgressDialog;
    }

    private void login(final String uname, final String upwd) {
        mIsProgressShow = true;
        mProgressDialog = getmProgressDialog();
        mProgressDialog.setMessage(getResources().getString(R.string.is_contact_customer));
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        // login huanxin server
        ChatClient.getInstance().login(uname, upwd, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "demo login success!");
                if (!mIsProgressShow) {
                    return;
                }
                ChatClientProxy.getInstance().addMessageListener();
                toChatActivity();
            }

            @Override
            public void onError(int code, String error) {
                Log.e(TAG, "login fail,code:" + code + ",error:" + error);
                if (!mIsProgressShow) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        mProgressDialog.dismiss();
                        Toast.makeText(CheckChatActivity.this,
                                getResources().getString(R.string.is_contact_customer_failure_seconed),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    private void toChatActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!CheckChatActivity.this.isFinishing()) {
                    mProgressDialog.dismiss();
                }
                String name = mUserItem.mId;
                // 进入主页面
                Intent intent = new IntentBuilder(CheckChatActivity.this)
                        .setTargetClass(ChatActivity.class)
                        .setVisitorInfo(createVisitorInfo(name))
                        .setServiceIMNumber("kefuchannelimid_856946")
                        .setScheduleQueue(createQueueIdentity("shouqian"))
                        .setShowUserNick(true)
                        .build();
                startActivity(intent);
                finish();

            }
        });
    }
}

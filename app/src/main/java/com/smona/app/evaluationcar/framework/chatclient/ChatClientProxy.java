package com.smona.app.evaluationcar.framework.chatclient;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.Notifier;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.util.CommonUtils;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.hyphenate.helpdesk.easeui.util.UserUtil;
import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.framework.IProxy;
import com.smona.app.evaluationcar.ui.chat.ChatActivity;
import com.smona.app.evaluationcar.util.CarLog;

import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Moth on 2017/5/6.
 */

public class ChatClientProxy implements IProxy {
    private static String TAG = ChatClientProxy.class.getSimpleName();

    private static volatile ChatClientProxy sInstance;
    private UIProvider mChatUIProvider;

    private ChatClientProxy() {
    }

    public static ChatClientProxy getInstance() {
        if (sInstance == null) {
            sInstance = new ChatClientProxy();
        }
        return sInstance;
    }

    public void init(Context context) {
        ChatClient.Options options = new ChatClient.Options();
        options.setAppkey("1112170506115622#kefuchannelapp41042");
        options.setTenantId("41042");
        // Kefu SDK 初始化
        if (!ChatClient.getInstance().init(context, options)) {
            return;
        }
        // Kefu EaseUI的初始化
        UIProvider.getInstance().init(context);
        if (ChatClient.getInstance().init(context, options)) {

            //设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
            ChatClient.getInstance().setDebugMode(true);

            mChatUIProvider = UIProvider.getInstance();
            //初始化EaseUI
            mChatUIProvider.init(context);
            //调用easeui的api设置providers
            setEaseUIProvider(context);
            //设置全局监听
            //setGlobalListeners();
        }
    }

    private void setEaseUIProvider(final Context context) {
        //设置头像和昵称 某些控件可能没有头像和昵称，需要注意
        UIProvider.getInstance().setUserProfileProvider(new UIProvider.UserProfileProvider() {
            @Override
            public void setNickAndAvatar(Context context, Message message, ImageView userAvatarView, TextView usernickView) {
                if (message.direct() == Message.Direct.RECEIVE) {
                    //设置接收方的昵称和头像
                    UserUtil.setAgentNickAndAvatar(context, message, userAvatarView, usernickView);
                } else {
                    //此处设置当前登录用户的头像，
                    if (userAvatarView != null) {
                        userAvatarView.setImageResource(R.mipmap.ic_launcher);
                    }
                }
            }
        });


        //设置通知栏样式
        mChatUIProvider.getNotifier().setNotificationInfoProvider(new Notifier.NotificationInfoProvider() {
            @Override
            public String getTitle(Message message) {
                //修改标题,这里使用默认
                return null;
            }

            @Override
            public int getSmallIcon(Message message) {
                //设置小图标，这里为默认
                return 0;
            }

            @Override
            public String getDisplayedText(Message message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = CommonUtils.getMessageDigest(message, context);
                if (message.getType() == Message.Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                return message.getFrom() + ": " + ticker;
            }

            @Override
            public String getLatestText(Message message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "contacts send " + messageNum + "messages to you";
            }

            @Override
            public Intent getLaunchIntent(Message message) {
                Intent intent = new IntentBuilder(context)
                        .setTargetClass(ChatActivity.class)
                        .setServiceIMNumber(message.getFrom())
                        .setShowUserNick(true)
                        .build();

                return intent;
            }
        });
    }


    public void createChatAccount(final String account, final String userPwd) {
        ChatClient.getInstance().createAccount(account, userPwd, new Callback() {
            @Override
            public void onSuccess() {
                CarLog.d(TAG, "createAccount success!");
                loginChat(account, userPwd);
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    public void loginChat(String account, String userPwd) {
        ChatClient.getInstance().login(account, userPwd, new Callback() {
            @Override
            public void onSuccess() {
                CarLog.d(TAG, "createAccount success!");
                addMessageListener();
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    public void addMessageListener() {
        ChatClient.getInstance().chatManager().addMessageListener(new ChatManager.MessageListener() {
            @Override
            public void onMessage(List<Message> list) {
                //收到普通消息
            }

            @Override
            public void onCmdMessage(List<Message> list) {
                //收到命令消息，命令消息不存数据库，一般用来作为系统通知，例如留言评论更新，
                //会话被客服接入，被转接，被关闭提醒
            }

            @Override
            public void onMessageStatusUpdate() {
                //消息的状态修改，一般可以用来刷新列表，显示最新的状态

            }

            @Override
            public void onMessageSent() {
                //发送消息后，会调用，可以在此刷新列表，显示最新的消息
            }
        });
    }

    public String getRandomAccount() {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {// 字符串
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) {// 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val.toLowerCase(Locale.getDefault());
    }
}

package com.smona.app.evaluationcar.ui.chat;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.hyphenate.helpdesk.easeui.ui.ChatFragment;
import com.hyphenate.helpdesk.easeui.util.Config;
import com.smona.app.evaluationcar.R;

/**
 * Created by Moth on 2016/12/18.
 */

public class ChatActivity extends BaseChatActivity {
    String toChatUsername;
    private ChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat);
        //IM服务号
        toChatUsername = getIntent().getExtras().getString(Config.EXTRA_SERVICE_IM_NUMBER);
        //可以直接new ChatFragment使用
        chatFragment = new CustomChatFragment();
        //传入参数
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra(Config.EXTRA_SERVICE_IM_NUMBER);
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

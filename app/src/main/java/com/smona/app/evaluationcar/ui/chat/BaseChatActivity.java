package com.smona.app.evaluationcar.ui.chat;

import android.support.v4.app.FragmentActivity;

import com.hyphenate.helpdesk.easeui.UIProvider;

/**
 * Created by motianhu on 5/8/17.
 */

public class BaseChatActivity extends FragmentActivity {

    @Override
    protected void onResume() {
        super.onResume();
        UIProvider.getInstance().getNotifier().reset();
    }
}

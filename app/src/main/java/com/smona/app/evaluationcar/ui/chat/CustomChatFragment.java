package com.smona.app.evaluationcar.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hyphenate.chat.Message;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.helpdesk.easeui.provider.CustomChatRowProvider;
import com.hyphenate.helpdesk.easeui.ui.ChatFragment;
import com.hyphenate.helpdesk.easeui.widget.chatrow.ChatRow;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.smona.app.evaluationcar.R;

public class CustomChatFragment extends ChatFragment implements ChatFragment.EaseChatFragmentListener {

    public static final int REQUEST_CODE_CONTEXT_MENU = 13;
    //message type 需要从1开始
    public static final int MESSAGE_TYPE_SENT_MAP = 1;
    public static final int MESSAGE_TYPE_RECV_MAP = 2;
    public static final int MESSAGE_TYPE_SENT_ORDER = 3;
    public static final int MESSAGE_TYPE_RECV_ORDER = 4;
    public static final int MESSAGE_TYPE_SENT_EVAL = 5;
    public static final int MESSAGE_TYPE_RECV_EVAL = 6;
    public static final int MESSAGE_TYPE_SENT_TRACK = 7;
    public static final int MESSAGE_TYPE_RECV_TRACK = 8;
    public static final int MESSAGE_TYPE_SENT_FORM = 9;
    public static final int MESSAGE_TYPE_RECV_FORM = 10;
    //避免和基类定义的常量可能发生冲突,常量从11开始定义
    private static final int ITEM_MAP = 11;
    private static final int ITEM_SHORTCUT = 12;
    private static final int ITEM_VIDEO = 13;
    private static final int REQUEST_CODE_SELECT_MAP = 11;
    private static final int REQUEST_CODE_SHORTCUT = 12;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void setUpView() {
        //这是新添加的扩展点击事件
        setChatFragmentListener(this);
        super.setUpView();
        //可以在此处设置titleBar(标题栏)的属性
        titleBar.setBackgroundColor(getResources().getColor(R.color.camera_split_side));
        titleBar.setLeftImageResource(R.drawable.back);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }


    @Override
    public void onAvatarClick(String username) {
        //头像点击事情
//        startActivity(new Intent(getActivity(), ...class));
    }

    @Override
    public boolean onMessageBubbleClick(Message message) {
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(Message message) {
    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        return false;
    }

    @Override
    public CustomChatRowProvider onSetCustomChatRowProvider() {
        return new DemoCustomChatRowProvider();
    }

    @Override
    protected void registerExtendMenuItem() {
        super.registerExtendMenuItem();
    }

    @Override
    public void onMessageSent() {
        messageList.refreshSelectLast();
    }

    public boolean checkFormChatRow(Message message) {
        if (message.getStringAttribute("type", null) != null) {
            try {
                String type = message.getStringAttribute("type");
                if (type.equals("html/form")) {
                    return true;
                }
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }

        return false;
    }


    /**
     * chat row provider
     */
    private final class DemoCustomChatRowProvider implements CustomChatRowProvider {

        @Override
        public int getCustomChatRowTypeCount() {
            //地图 和 满意度 发送接收 共4种
            //订单 和 轨迹 发送接收共4种
            // form 发送接收2种
            return 11;
        }

        @Override
        public int getCustomChatRowType(Message message) {
            //此处内部有用到,必须写否则可能会出现错位
            if (message.getType() == Message.Type.LOCATION) {
                return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_MAP : MESSAGE_TYPE_SENT_MAP;
            } else if (message.getType() == Message.Type.TXT) {
                if (MessageHelper.getEvalRequest(message) != null) {
                    return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EVAL : MESSAGE_TYPE_SENT_EVAL;
                } else if (MessageHelper.getOrderInfo(message) != null) {
                    return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_ORDER : MESSAGE_TYPE_SENT_ORDER;
                } else if (MessageHelper.getVisitorTrack(message) != null) {
                    return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TRACK : MESSAGE_TYPE_SENT_TRACK;
                } else if (checkFormChatRow(message)) {
                    return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FORM : MESSAGE_TYPE_SENT_FORM;
                }
            }

            return -1;
        }

        @Override
        public ChatRow getCustomChatRow(Message message, int position, BaseAdapter adapter) {
            return null;
        }
    }


}

package com.smona.app.evaluationcar.ui.evaluation.attach;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.business.HttpDelegator;
import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.data.event.AttachEvent;
import com.smona.app.evaluationcar.data.item.Attachment;
import com.smona.app.evaluationcar.data.model.ResAttachmentPage;
import com.smona.app.evaluationcar.framework.event.EventProxy;
import com.smona.app.evaluationcar.framework.json.JsonParse;
import com.smona.app.evaluationcar.ui.common.activity.HeaderActivity;
import com.smona.app.evaluationcar.util.CacheContants;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.ToastUtils;
import com.smona.app.evaluationcar.util.ViewUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moth on 2017/9/4.
 */

public class AttachmentActivity extends HeaderActivity {
    private static final String TAG = AttachmentActivity.class.getSimpleName();

    private GridView mAttachGrid;
    private AttachAdapter mAttachAdapter;
    private View mNoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initData();
        EventProxy.register(this);
    }

    private void initViews() {
        mAttachGrid = (GridView)findViewById(R.id.attachments);
        mAttachAdapter = new AttachAdapter(this);
        mAttachGrid.setAdapter(mAttachAdapter);
        mNoContent = findViewById(R.id.no_content_layout);
    }

    private void initData() {
        String carBillId = getIntent().getStringExtra(CacheContants.ATTACH_CARBILLID);
        CarLog.d(TAG, "carBillId: " + carBillId);
        HttpDelegator.getInstance().getEvaluationNotPassAttach(mUserItem.mId, carBillId, new ResponseCallback<String>() {
            @Override
            public void onFailed(String error) {
                CarLog.d(TAG, "onFailed error: " + error);
                AttachEvent event = new AttachEvent();
                event.success = false;
                EventProxy.post(event);
            }

            @Override
            public void onSuccess(String content) {
                ResAttachmentPage page = JsonParse.parseJson(content, ResAttachmentPage.class);
                AttachEvent event = new AttachEvent();
                if(page != null && page.data !=null && page.data.size()>0) {
                    event.datas = page.data;
                    event.success = true;
                } else {
                    event.success = false;
                }
                EventProxy.post(event);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void actionEvent(AttachEvent actionEvent) {
        CarLog.d(TAG, "actionEvent actionEvent: " + actionEvent.success);
        if(actionEvent.success) {
            mAttachAdapter.updateData((List<Attachment>)actionEvent.datas);
            mAttachAdapter.notifyDataSetChanged();
        } else {
            ViewUtil.setViewVisible(findViewById(R.id.attachments_container), false);
            ViewUtil.setViewVisible(mNoContent, true);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_attachment;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.reason_attach_list;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventProxy.unregister(this);
    }
}
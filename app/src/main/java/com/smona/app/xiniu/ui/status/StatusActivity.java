package com.smona.app.xiniu.ui.status;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.smona.app.xiniu.R;
import com.smona.app.xiniu.data.bean.CarBillBean;
import com.smona.app.xiniu.ui.common.activity.HeaderActivity;
import com.smona.app.xiniu.util.CacheContants;
import com.smona.app.xiniu.util.StatusUtils;
import com.smona.app.xiniu.util.ViewUtil;

/**
 * Created by Moth on 2016/12/18.
 */

public class StatusActivity extends HeaderActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        CarBillBean bean = (CarBillBean) getIntent().getSerializableExtra(CacheContants.CARBILLBEAN);

        View parent = findViewById(R.id.carbill);
        TextView textKey = (TextView) parent.findViewById(R.id.key);
        TextView textValue = (TextView) parent.findViewById(R.id.value);
        textKey.setText(R.string.status_cabillid);
        textValue.setText(bean.carBillId);


        String status = StatusUtils.BILL_STATUS_MAP.get(bean.status);
        parent = findViewById(R.id.billstatus);
        textKey = (TextView) parent.findViewById(R.id.key);
        textValue = (TextView) parent.findViewById(R.id.value);
        textKey.setText(R.string.status_bill_status);
        textValue.setText(status);
        textValue.setTextColor(getResources().getColor(R.color.red));

        if("评估完成".equals(status) || "高评通过".equals(status)) {
            parent = findViewById(R.id.billprice);
            ViewUtil.setViewVisible(parent, true);
            textKey = (TextView) parent.findViewById(R.id.key);
            textValue = (TextView) parent.findViewById(R.id.value);
            textKey.setText(R.string.list_item_price);
            textValue.setText(bean.evaluatePrice + "");
            textValue.setTextColor(getResources().getColor(R.color.green));
        }

        parent = findViewById(R.id.billtime);
        textKey = (TextView) parent.findViewById(R.id.key);
        textValue = (TextView) parent.findViewById(R.id.value);
        textKey.setText(R.string.status_time);
        textValue.setText(bean.modifyTime);

        textValue = (TextView) findViewById(R.id.mark);
        textValue.setText(bean.mark);

        WebView webView = (WebView) findViewById(R.id.note);
        String str = "<html><head><title>欢迎你</title></head><body>"
                + bean.applyAllOpinion
                + "</body></html>";

        webView.loadDataWithBaseURL(null, str, "text/html", "utf-8", null);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_status;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.carbill_result;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

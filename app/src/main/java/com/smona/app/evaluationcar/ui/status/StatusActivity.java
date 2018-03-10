package com.smona.app.evaluationcar.ui.status;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.bean.CarBillBean;
import com.smona.app.evaluationcar.data.item.UserItem;
import com.smona.app.evaluationcar.ui.common.activity.HeaderActivity;
import com.smona.app.evaluationcar.util.CacheContants;
import com.smona.app.evaluationcar.util.StatusUtils;
import com.smona.app.evaluationcar.util.Utils;
import com.smona.app.evaluationcar.util.ViewUtil;

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

        if ("评估完成".equals(status) || "高评通过".equals(status)) {
            boolean notVisible = mUserItem.userBean.isRichanJinrong();
            parent = findViewById(R.id.billprice);
            ViewUtil.setViewVisible(parent, !notVisible);
            textKey = (TextView) parent.findViewById(R.id.key);
            textValue = (TextView) parent.findViewById(R.id.value);
            textKey.setText(R.string.list_item_price);
            textValue.setText(bean.evaluatePrice + "元");
            textValue.setTextColor(getResources().getColor(R.color.green));

            if(bean.leaseTerm != 0) {
                //租赁周期
                parent = findViewById(R.id.leaseTerm);
                ViewUtil.setViewVisible(parent, true);
                textKey = (TextView) parent.findViewById(R.id.key);
                textValue = (TextView) parent.findViewById(R.id.value);
                textKey.setText(R.string.list_item_leaseTerm);
                textValue.setText(bean.leaseTerm + "月");
                textValue.setTextColor(getResources().getColor(R.color.green));

                //残值价格
                parent = findViewById(R.id.residualPrice);
                ViewUtil.setViewVisible(parent, true);
                textKey = (TextView) parent.findViewById(R.id.key);
                textValue = (TextView) parent.findViewById(R.id.value);
                textKey.setText(R.string.list_item_residualPrice);
                textValue.setText(bean.residualPrice + "元");
                textValue.setTextColor(getResources().getColor(R.color.green));
            }
        }

        parent = findViewById(R.id.billtime);
        textKey = (TextView) parent.findViewById(R.id.key);
        textValue = (TextView) parent.findViewById(R.id.value);
        textKey.setText(R.string.status_time);
        textValue.setText(bean.modifyTime);

        textValue = (TextView) findViewById(R.id.mark);
        textValue.setText(bean.mark);

        String bodyHTML = bean.applyAllOpinion;
        WebView webView = (WebView) findViewById(R.id.note);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        webView.loadData(Utils.getHtmlData(bodyHTML), "text/html; charset=utf-8", "utf-8");
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

package com.smona.app.xiniueval.ui.common.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.smona.app.xiniueval.R;
import com.smona.app.xiniueval.business.HttpDelegator;
import com.smona.app.xiniueval.business.ResponseCallback;
import com.smona.app.xiniueval.data.bean.CarBillBean;
import com.smona.app.xiniueval.data.item.CarbillFullInfo;
import com.smona.app.xiniueval.framework.json.JsonParse;
import com.smona.app.xiniueval.util.CacheContants;
import com.smona.app.xiniueval.util.CarLog;

public class CarbillReportActivity extends HeaderActivity {

    private static final String TAG = CarbillReportActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        CarBillBean bean = (CarBillBean) getIntent().getSerializableExtra(CacheContants.CARBILLBEAN);
        //HttpDelegator.getInstance().queryCarBillForId("sh008001", "NS201605090015", mGetCarBillCallback);
        HttpDelegator.getInstance().queryCarBillForId(mUserItem.mId, bean.carBillId, mGetCarBillCallback);
    }

    private ResponseCallback<String> mGetCarBillCallback = new ResponseCallback<String>() {
        @Override
        public void onFailed(String error) {
            CarLog.d(TAG, "mGetCarBillCallback onFailed error: " + error);
        }

        @Override
        public void onSuccess(String content) {
            CarLog.d(TAG, "mGetCarBillCallback onSuccess: " + content);
            CarbillFullInfo bean = JsonParse.parseJson(content, CarbillFullInfo.class);
            bindViews(bean);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.carbill_report;
    }

    private void bindViews(CarbillFullInfo bean) {
        TextView carbill =(TextView)findViewById(R.id.carbillid);
        carbill.setText(bean.carBillId);

        carbill =(TextView)findViewById(R.id.pinpai);
        carbill.setText(bean.carBrandName);

        carbill =(TextView)findViewById(R.id.chexi);
        carbill.setText(bean.carSetName);

        carbill =(TextView)findViewById(R.id.chexing);
        carbill.setText(bean.carTypeName);

        carbill =(TextView)findViewById(R.id.gongli);
        carbill.setText(bean.runNum + "公里");

        carbill =(TextView)findViewById(R.id.dengji);
        carbill.setText(bean.regDate +"");

        carbill =(TextView)findViewById(R.id.xinchejia);
        carbill.setText("¥" + bean.newCarPrice + "");

        carbill =(TextView)findViewById(R.id.jiage);
        carbill.setText("¥" + bean.evaluatePrice +"");

        carbill =(TextView)findViewById(R.id.pingguriqi);
        carbill.setText(bean.evaluateDate +"");
    }
}

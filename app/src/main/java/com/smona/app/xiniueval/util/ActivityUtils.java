package com.smona.app.xiniueval.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.smona.app.xiniueval.data.bean.CarBillBean;
import com.smona.app.xiniueval.data.bean.CarImageBean;
import com.smona.app.xiniueval.service.UploadService;
import com.smona.app.xiniueval.ui.WebActivity;
import com.smona.app.xiniueval.ui.common.activity.PhotoRulesActivity;
import com.smona.app.xiniueval.ui.common.activity.RefuseRulesActivity;

/**
 * Created by motianhu on 2/27/17.
 */

public class ActivityUtils {

    public static final int ACTION_GALLERY = 1;
    public static final int ACTION_CAMERA = 2;

    public static void jumpWebActivity(Context context, int type, int id) {
        Intent intent = new Intent();
        intent.putExtra(CacheContants.WEB_ACTIVITY_TYPE, type);
        intent.putExtra(CacheContants.PAGE_ELEMENT_ID, id);
        intent.setClass(context, WebActivity.class);
        context.startActivity(intent);
    }

    public static void jumpEvaluation(Context context, int billStatus, String carBillId, int imageId, boolean isResidual, Class clazz) {
        Intent intent = new Intent();
        SPUtil.put(context, CacheContants.BILL_STATUS, billStatus);
        SPUtil.put(context, CacheContants.CARBILLID, TextUtils.isEmpty(carBillId) ? "" : carBillId);
        SPUtil.put(context, CacheContants.IMAGEID, imageId);
        SPUtil.put(context, CacheContants.ISResidualEVALUATION, isResidual);
        intent.setClass(context, clazz);
        context.startActivity(intent);
    }

    public static void jumpStatus(Context context, CarBillBean bean, Class clazz) {
        Intent intent = new Intent();
        intent.putExtra(CacheContants.CARBILLBEAN, bean);
        intent.setClass(context, clazz);
        context.startActivity(intent);
    }

    public static void callPhone(Context context, String number) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    //ImageId,CarBillId 来源于 EvaluationActivity
    public static void jumpCameraActivity(Context contet, CarImageBean bean, Class clazz) {
        Intent intent = new Intent();
        intent.setClass(contet, clazz);
        SPUtil.put(contet, CacheContants.IMAGECLASS, bean.imageClass);
        SPUtil.put(contet, CacheContants.IMAGESEQNUM, bean.imageSeqNum);
        contet.startActivity(intent);
    }

    public static void jumpOnlyActivity(Context context, Class clazz) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        context.startActivity(intent);
    }

    public static void startUpService(Context context) {
        Intent intent = new Intent(context, UploadService.class);
        context.startService(intent);
    }

    public static void jumpRefuseRules(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, RefuseRulesActivity.class);
        context.startActivity(intent);
    }

    public static void jumpTakePhotoRules(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, PhotoRulesActivity.class);
        context.startActivity(intent);
    }

}

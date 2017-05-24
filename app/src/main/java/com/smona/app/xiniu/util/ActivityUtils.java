package com.smona.app.xiniu.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.smona.app.xiniu.data.bean.CarBillBean;
import com.smona.app.xiniu.data.bean.CarImageBean;
import com.smona.app.xiniu.service.UploadService;

/**
 * Created by motianhu on 2/27/17.
 */

public class ActivityUtils {

    public static final int ACTION_GALLERY = 1;
    public static final int ACTION_CAMERA = 2;

    public static final int ACTION_CAR_BRAND = 1;
    public static final int ACTION_CAR_CITY = 2;
    public static final String ACTION_DATA_BRAND = "data_car_brand";
    public static final String ACTION_DATA_SET = "data_car_set";
    public static final String ACTION_DATA_TYPE = "data_car_type";
    public static final String ACTION_DATA_CITY = "data_city";

    public static void jumpEvaluation(Context context, int billStatus, String carBillId, int imageId, Class clazz) {
        Intent intent = new Intent();
        SPUtil.put(context, CacheContants.BILL_STATUS, billStatus);
        SPUtil.put(context, CacheContants.CARBILLID, carBillId);
        SPUtil.put(context, CacheContants.IMAGEID, imageId);
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

    public static void jumpResultActivity(Activity activity, Class clazz, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, clazz);
        activity.startActivityForResult(intent, requestCode);
    }
}

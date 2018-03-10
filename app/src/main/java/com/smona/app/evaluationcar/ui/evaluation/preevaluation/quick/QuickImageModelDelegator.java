package com.smona.app.evaluationcar.ui.evaluation.preevaluation.quick;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.bean.QuickPreCarImageBean;
import com.smona.app.evaluationcar.framework.provider.DBDelegator;
import com.smona.app.evaluationcar.util.CarLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by motianhu on 3/23/17.
 */

public class QuickImageModelDelegator {
    public static final int IMAGE_Registration = 0;
    public static final int IMAGE_DrivingLicense = 1;
    public static final int IMAGE_VehicleNameplate = 2;
    public static final int IMAGE_CarBody = 3;
    public static final int IMAGE_CarFrame = 4;
    public static final int IMAGE_VehicleInterior = 5;
    public static final int IMAGE_DifferenceSupplement = 6;
    public static final int IMAGE_OriginalCarInsurancet = 7;

    //quick pre evaluation
    public static final int QUICK_BASE = 0;
    public static final int QUICK_SUPPLEMENT = 1;

    private volatile static QuickImageModelDelegator sInstance;
    private String[] mImageClass = null;
    private List<String>[] mImageClassItems = null;
    private Map<String, Integer> mImageClassMap = null;

    private QuickImageModelDelegator() {
        mImageClassMap = new HashMap<String, Integer>();
    }

    public static QuickImageModelDelegator getInstance() {
        if (sInstance == null) {
            sInstance = new QuickImageModelDelegator();
        }
        return sInstance;
    }

    public void init(Context context) {
        Resources res = context.getResources();
        mImageClass = res.getStringArray(R.array.image_class);
        String[] array = res.getStringArray(R.array.quick_image_class_detail);
        mImageClassItems = getTwoDimensionalArray(array);

        mImageClassMap.put(mImageClass[IMAGE_Registration], IMAGE_Registration);
        mImageClassMap.put(mImageClass[IMAGE_DrivingLicense], IMAGE_DrivingLicense);
        mImageClassMap.put(mImageClass[IMAGE_VehicleNameplate], IMAGE_VehicleNameplate);
        mImageClassMap.put(mImageClass[IMAGE_CarBody], IMAGE_CarBody);
        mImageClassMap.put(mImageClass[IMAGE_CarFrame], IMAGE_CarFrame);
        mImageClassMap.put(mImageClass[IMAGE_VehicleInterior], IMAGE_VehicleInterior);
        mImageClassMap.put(mImageClass[IMAGE_DifferenceSupplement], IMAGE_DifferenceSupplement);
        mImageClassMap.put(mImageClass[IMAGE_OriginalCarInsurancet], IMAGE_OriginalCarInsurancet);
    }

    private List<String>[] getTwoDimensionalArray(String[] array) {
        List<String>[] twoDimensionalArray = new ArrayList[array.length];
        for (int i = 0; i < array.length; i++) {
            String[] tempArray = array[i].split(",");
            if (twoDimensionalArray[i] == null) {
                twoDimensionalArray[i] = new ArrayList<String>();
            }
            if (TextUtils.isEmpty(array[i])) {
                continue;
            }
            for (int j = 0; j < tempArray.length; j++) {
                twoDimensionalArray[i].add(tempArray[j]);
            }
        }
        return twoDimensionalArray;
    }

    private List<QuickPreCarImageBean> getDefaultModel(int type) {
        List<QuickPreCarImageBean> defaultList = new ArrayList<QuickPreCarImageBean>();

        for (int i = 0; i < mImageClassItems[type].size(); i++) {
            QuickPreCarImageBean bean = new QuickPreCarImageBean();
            bean.imageClass = mImageClass[type];
            bean.displayName = mImageClassItems[type].get(i);
            bean.imageSeqNum = i;
            defaultList.add(bean);
        }

        return defaultList;
    }

    private void composeModel(List<QuickPreCarImageBean> saveList, List<QuickPreCarImageBean> defaultList) {
        int size = defaultList.size();
        QuickPreCarImageBean tempCar,removeCar;
        for (QuickPreCarImageBean saveCar : saveList) {
            for (int i = 0; i < size; i++) {
                tempCar = defaultList.get(i);
                if (tempCar.imageClass.equals(saveCar.imageClass) &&
                        (tempCar.imageSeqNum == saveCar.imageSeqNum)) {
                    removeCar = defaultList.remove(i);
                    saveCar.displayName = removeCar.displayName;
                    defaultList.add(i, saveCar);
                    break;
                }
            }
        }
    }

    public String getImageClassForType(int type) {
        return mImageClass[type];
    }

    public String getDisplayName(int type, int seqNum) {
        if (type >= mImageClassItems.length) {
            return null;
        }
        if (seqNum >= mImageClassItems[type].size()) {
            return null;
        }
        return mImageClassItems[type].get(seqNum);
    }

    //quick pre evaluation
    public List<QuickPreCarImageBean> getSaveModel(int type, int imageId) {
        List<QuickPreCarImageBean> saveList = new ArrayList<>();
        List<QuickPreCarImageBean> defaultList = getQuickBaseModel(type);
        if (imageId > 0) {
            saveList = DBDelegator.getInstance().queryPreQuickImages(imageId);
        }
        CarLog.d("getSaveModel", imageId + "; saveList; " + saveList);
        composeModel(saveList, defaultList);
        return defaultList;
    }

    public List<QuickPreCarImageBean> getHttpModel(int type, String qiuckPreCarBillId) {
        List<QuickPreCarImageBean> saveList = DBDelegator.getInstance().queryPreQuickImages(qiuckPreCarBillId);
        List<QuickPreCarImageBean> defaultList = getQuickBaseModel(type);
        composeModel(saveList, defaultList);
        return defaultList;
    }

    public List<QuickPreCarImageBean> getQuickBaseModel(int type) {
        List<QuickPreCarImageBean> defaultList = new ArrayList<>();
        int index = 0;
        QuickPreCarImageBean bean = null;
        if (type == QUICK_BASE) {
            //登记证书首页
            bean = new QuickPreCarImageBean();
            bean.imageClass = getImageClassForType(IMAGE_Registration);
            bean.displayName = getDisplayName(IMAGE_Registration, index);
            bean.imageSeqNum = index;
            defaultList.add(bean);

            //中控台含排挡杆
            bean = new QuickPreCarImageBean();
            index = 2;
            bean.imageClass = getImageClassForType(IMAGE_VehicleInterior);
            bean.displayName = getDisplayName(IMAGE_VehicleInterior, index);
            bean.imageSeqNum = index;
            defaultList.add(bean);


            //车左前45度
            bean = new QuickPreCarImageBean();
            index = 0;
            bean.imageClass = getImageClassForType(IMAGE_CarBody);
            bean.displayName = getDisplayName(IMAGE_CarBody, index);
            bean.imageSeqNum = index;
            defaultList.add(bean);
        } else {
            //行驶证
            bean = new QuickPreCarImageBean();
            index = 0;
            bean.imageClass = getImageClassForType(IMAGE_DrivingLicense);
            bean.displayName = getDisplayName(IMAGE_DrivingLicense, index);
            bean.imageSeqNum = index;
            defaultList.add(bean);

            //左前门
            bean = new QuickPreCarImageBean();
            index = 5;
            bean.imageClass = getImageClassForType(IMAGE_CarFrame);
            bean.displayName = getDisplayName(IMAGE_CarFrame, index);
            bean.imageSeqNum = index;
            defaultList.add(bean);
        }
        return defaultList;
    }
}

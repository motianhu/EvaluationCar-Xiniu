package com.smona.app.evaluationcar.data.item;

import com.smona.app.evaluationcar.data.bean.BaseBean;

import java.util.List;

/**
 * Created by motianhu on 3/20/17.
 */

public class UploadItem extends BaseBean {
    private String carBillId;
    private List<CategoryItem> imageInfos;
    private int uploadCount;

    public String getCarBillId() {
        return carBillId;
    }

    public void setCarBillId(String carBillId) {
        this.carBillId = carBillId;
    }

    public List<CategoryItem> getImageInfos() {
        return imageInfos;
    }

    public void setImageInfos(List<CategoryItem> imageInfos) {
        this.imageInfos = imageInfos;
    }


}

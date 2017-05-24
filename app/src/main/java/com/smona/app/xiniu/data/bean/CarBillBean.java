package com.smona.app.xiniu.data.bean;

import com.smona.app.xiniu.util.StatusUtils;

/**
 * Created by motianhu on 2/28/17.
 */

public class CarBillBean extends BaseBean {
    public String carBillId;
    public int status;
    public String modifyTime;
    public String createTime;
    public String mark;
    public String applyAllOpinion;
    public double preSalePrice;
    public double evaluatePrice;
    public String imageThumbPath;
    public int imageId;
    public int uploadStatus = StatusUtils.BILL_UPLOAD_STATUS_NONE;

    public String toString() {
        return "carBillId=" + carBillId + ",status=" + status +
                ",imageThumbPath=" + imageThumbPath + ",imageId=" + imageId +
                ",preSalePrice=" + preSalePrice + ",uploadStatus=" + uploadStatus;
    }
}

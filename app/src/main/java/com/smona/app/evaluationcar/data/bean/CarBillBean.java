package com.smona.app.evaluationcar.data.bean;

import com.smona.app.evaluationcar.util.StatusUtils;

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
    //2.0
    public int leaseTerm = 0; //between 0,12,24,36
    public int residualPrice;

    public String toString() {
        return "carBillId=" + carBillId + ",status=" + status +
                ",imageThumbPath=" + imageThumbPath + ",imageId=" + imageId +
                ",preSalePrice=" + preSalePrice + ",uploadStatus=" + uploadStatus + ", leaseTerm:" + leaseTerm
                + ", residualPrice=" + residualPrice;
    }
}

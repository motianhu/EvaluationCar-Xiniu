package com.smona.app.evaluationcar.data.bean;

/**
 * Created by motianhu on 3/21/17.
 */

public class CarImageBean extends BaseBean {
    public int imageId;
    public String carBillId;
    public String imageClass; //分类名称
    public String displayName; //图片名称
    public int imageSeqNum;//图片序列
    public String imagePath; //remote url
    public String imageThumbPath; //remote url
    public String imageLocalUrl; //local url
    public int imageUpdate; //use in notpass
    public String createTime;
    public String updateTime;

    public String toString() {
        return "imageId=" + imageId + ", carBillId=" + carBillId + ", imageClass=" + imageClass + ",displayName=" +
                displayName + ", imageSeqNum=" + imageSeqNum + ", imageLocalUrl= " + imageLocalUrl + ", imageUpdate=" + imageUpdate
                + ", imagePath=" + imagePath;
    }
}

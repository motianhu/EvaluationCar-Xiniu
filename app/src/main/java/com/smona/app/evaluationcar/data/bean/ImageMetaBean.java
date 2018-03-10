package com.smona.app.evaluationcar.data.bean;

/**
 * Created by Moth on 2017/3/16.
 */

public class ImageMetaBean {
    public int imageSeqNum; //图片序号
    public String imageClass; //图片类型
    public String imageDesc; //拍照描述
    public String waterMark; //水印名称

    public String toString() {
        return "imageClass=" + imageClass + ",imageSeqNum=" + imageSeqNum + ",imageDesc=" + imageDesc + ", waterMark=" + waterMark;
    }
}

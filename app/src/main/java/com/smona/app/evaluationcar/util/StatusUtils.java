package com.smona.app.evaluationcar.util;

import java.util.HashMap;

/**
 * Created by motianhu on 3/22/17.
 */

public class StatusUtils {
    //local callbill
    public static final int BILL_STATUS_NONE = 0;
    public static final int BILL_STATUS_SAVE = 1;
    public static final int BILL_STATUS_RETURN = 3;

    public static final int IMAGE_DEFAULT = 0;
    public static final int IMAGE_UPDATE = 1;

    public static final int BILL_UPLOAD_STATUS_NONE = 0;
    public static final int BILL_UPLOAD_STATUS_UPLOADING = 1;


    public static final int MESSAGE_REQUEST_ERROR = 0x1;
    public static final int MESSAGE_REQUEST_PAGE_LAST = 0x10000;
    public static final int MESSAGE_REQUEST_PAGE_MORE = 0x10001;


    public static final HashMap<Integer, String> BILL_STATUS_MAP = new HashMap<Integer, String>();

    static {
        BILL_STATUS_MAP.put(21, "等待初审");
        BILL_STATUS_MAP.put(22, "初审中");
        BILL_STATUS_MAP.put(23, "初审驳回");
        BILL_STATUS_MAP.put(24, "初审通过");

        BILL_STATUS_MAP.put(31, "等待初评");
        BILL_STATUS_MAP.put(32, "初评中");
        BILL_STATUS_MAP.put(33, "初评驳回");
        BILL_STATUS_MAP.put(34, "初评通过");

        BILL_STATUS_MAP.put(41, "等待中评");
        BILL_STATUS_MAP.put(42, "中评中");
        BILL_STATUS_MAP.put(43, "中评驳回");
        BILL_STATUS_MAP.put(44, "中评通过");

        BILL_STATUS_MAP.put(51, "等待高评");
        BILL_STATUS_MAP.put(52, "高评中");
        BILL_STATUS_MAP.put(53, "高评驳回");
        BILL_STATUS_MAP.put(54, "高评通过");

        BILL_STATUS_MAP.put(80, "评估完成");
        BILL_STATUS_MAP.put(0, "提取图片");
    }

    public static boolean isNotPass(int status) {
        return status == 23 || status == 33 || status == 43 || status == 53;
    }


    //prevaluation
    public static final HashMap<Integer, String> PREBILL_STATUS_MAP = new HashMap<Integer, String>();
    static {
        PREBILL_STATUS_MAP.put(-1, "驳回");
        PREBILL_STATUS_MAP.put(0, "审核中");
        PREBILL_STATUS_MAP.put(1, "通过");
        PREBILL_STATUS_MAP.put(2, "已推送");
    }

    public static boolean isPrePass(int status) {
        return status == 1 || status == 2;
    }

    public static boolean isPreNotPass(int status) {
        return status == -1;
    }
}

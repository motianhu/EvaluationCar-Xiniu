package com.smona.app.xiniueval.data.item;

import com.smona.app.xiniueval.data.bean.BaseBean;

/**
 * Created by Moth on 2017/3/15.
 */

public class BillTotalItem extends BaseBean {
    public static final String ALLCOUNT = "allCount";
    public static final String PROCESSCOUNT = "processCount";
    public static final String FINISHCOUNT = "finishCount";
    public static final String REFUSECOUNT = "refuseCount";

    public String infoType;
    public int countInfo;
}

package com.smona.app.evaluationcar.business.param;

/**
 * Created by Moth on 2017/4/6.
 */

public class CarbillParam extends Params {
    public static final String USERNAME = "userName";
    public static final String STATUS = "status";
    public static final String CURPAGE = "curPage";
    public static final String PAGESIZE = "pageSize";

    public String userName;
    public String status;
    public int curPage;
    public int pageSize;
    public String type;

    public String toString() {
        return "userName=" + userName + ",status=" + status + ",curPage=" + curPage + ",pageSize=" + pageSize;
    }
}

package com.smona.app.evaluationcar.business.param;

/**
 * Created by Moth on 2017/4/6.
 */

public class PageParam extends Params {
    public static final String CURPAGE = "curPage";
    public static final String PAGESIZE = "pageSize";

    public int curPage;
    public int pageSize;

    public String toString() {
        return "curPage=" + curPage + ",pageSize=" + pageSize;
    }
}

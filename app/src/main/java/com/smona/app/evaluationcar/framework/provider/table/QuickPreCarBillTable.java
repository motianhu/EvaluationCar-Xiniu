package com.smona.app.evaluationcar.framework.provider.table;

/**
 * Created by motianhu on 3/8/17.
 */

public class QuickPreCarBillTable extends BaseTable {
    public static final String TABLE_NAME = "quickPreCarBill";

    public static final String CARBILLID = "carBillId";
    public static final String CREATETIME = "createTime";
    public static final String MODIFYTIME = "modifyTime";
    public static final String BILLSTATUS = "billStatus";
    public static final String PRESALEPRICE = "preSalePrice";
    public static final String THUMBUrl = "thumbUrl";
    public static final String MARK = "mark";
    public static final String LOCALSORT = "localSort";
    public static final String IMAGEID = "imageId";
    public static final String UPLOADStATUS = "uploadStatus";

    private static volatile QuickPreCarBillTable sInstance = null;

    private QuickPreCarBillTable() {
        super();
    }

    public synchronized static QuickPreCarBillTable getInstance() {
        if (sInstance == null) {
            sInstance = new QuickPreCarBillTable();
        }
        return sInstance;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String createTableSql() {
        return "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY, "
                + CARBILLID + " TEXT , "
                + CREATETIME + " TIMESTAMP default (datetime('now', 'localtime')), "
                + MODIFYTIME + " TIMESTAMP default (datetime('now', 'localtime')), "
                + BILLSTATUS + " INTEGER, "
                + MARK + " TEXT, "
                + THUMBUrl + " TEXT, "
                + IMAGEID + " INTEGER, "
                + LOCALSORT + " INTEGER default 0, "
                + UPLOADStATUS + " INTEGER default 0, "
                + PRESALEPRICE + " DOUBLE "
                + ")";
    }

    @Override
    public String updateTableSql() {
        return null;
    }
}

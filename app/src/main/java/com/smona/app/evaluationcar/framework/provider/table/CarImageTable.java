package com.smona.app.evaluationcar.framework.provider.table;

/**
 * Created by motianhu on 3/8/17.
 */

public class CarImageTable extends BaseTable {
    public static final String TABLE_NAME = "carimage";

    public static final String IMAGEID = "imageId";
    public static final String CARBILLID = "carBillId"; //对应单据ID
    public static final String IMAGESEQNUM = "imageSeqNum"; //汽车图片当前序号/wechat
    public static final String IMAGECLASS = "imageClass"; //图片分类/wechat
    public static final String IMAGEREMOTEURL = "imagePath";  //服务器地址,有值则代表已上传成功
    public static final String IMAGEREMOTETHUMBNAILURL = "imageThumbPath";  //服务器地址,有值则代表已上传成功
    public static final String IMAGELOCALURL = "imageLocalUrl"; //本机地址
    public static final String IMAGEUPDATE = "imageUpdate"; //0-not update; 1-update
    public static final String CREATETIME = "createTime"; //
    public static final String UPDATETIEM = "updateTime"; //图片分类

    private static volatile CarImageTable mInstance = null;

    private CarImageTable() {
        super();
    }

    public synchronized static CarImageTable getInstance() {
        if (mInstance == null) {
            mInstance = new CarImageTable();
        }
        return mInstance;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String createTableSql() {
        return "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY, "
                + IMAGEID + " INTEGER , "
                + CARBILLID + " TEXT, "
                + IMAGESEQNUM + " INTEGER, "
                + IMAGECLASS + " TEXT, "
                + IMAGEREMOTEURL + " TEXT, "
                + IMAGEREMOTETHUMBNAILURL + " TEXT, "
                + IMAGEUPDATE + " INTEGER default 0, "
                + IMAGELOCALURL + " TEXT,"
                + CREATETIME + " TIMESTAMP default (datetime('now', 'localtime')),"
                + UPDATETIEM + " TIMESTAMP default (datetime('now', 'localtime')) "
                + ")";
    }

    @Override
    public String updateTableSql() {
        return null;
    }
}

package com.smona.app.evaluationcar.framework.provider.table;

/**
 * Created by motianhu on 3/11/17.
 */

public class ImageMetaTable extends BaseTable {
    public static final String TABLE_NAME = "imagemeta";

    public static final String IMAGECLASS = "imageClass"; //图片分类
    public static final String IMAGESEQNUM = "imageSeqNum"; //图片序列
    public static final String IMAGEDES = "imageDesc"; //图片描述
    public static final String WATERMARK = "waterMark"; //水印名称

    private static volatile ImageMetaTable mInstance = null;

    private ImageMetaTable() {
        super();
    }

    public synchronized static ImageMetaTable getInstance() {
        if (mInstance == null) {
            mInstance = new ImageMetaTable();
        }
        return mInstance;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String createTableSql() {
        return "CREATE TABLE " + TABLE_NAME + "(" + _ID
                + " INTEGER PRIMARY KEY, "
                + IMAGECLASS + " TEXT, "
                + IMAGESEQNUM + " INTEGER, "
                + IMAGEDES + " TEXT, "
                + WATERMARK + " TEXT " + ")";
    }

    @Override
    public String updateTableSql() {
        return null;
    }
}

package com.smona.app.evaluationcar.framework.provider.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.smona.app.evaluationcar.data.bean.ImageMetaBean;
import com.smona.app.evaluationcar.framework.provider.table.ImageMetaTable;

import java.util.List;

/**
 * Created by motianhu on 3/21/17.
 */

public class ImageMetaDao extends BaseDao<ImageMetaBean> {

    public ImageMetaDao(Context context) {
        super(context);
    }

    @Override
    public void initTable() {
        mTable = ImageMetaTable.getInstance();
    }

    @Override
    public void deleteList(List<ImageMetaBean> itemInfoList) {

    }

    @Override
    public void deleteItem(ImageMetaBean itemInfo) {

    }

    @Override
    public void updateList(List<ImageMetaBean> itemInfoList) {
    }

    @Override
    public boolean updateItem(ImageMetaBean itemInfo) {
        String where = ImageMetaTable.IMAGECLASS + "=? and " + ImageMetaTable.IMAGESEQNUM + "=?";
        String[] whereArgs = new String[]{itemInfo.imageClass, itemInfo.imageSeqNum + ""};
        return mContentResolver.update(mTable.mContentUriNoNotify,
                modelToContentValues(itemInfo), where, whereArgs) > 0;
    }

    @Override
    public ImageMetaBean cursorToModel(Cursor cursor) {
        ImageMetaBean item = new ImageMetaBean();
        item.imageClass = getString(cursor, ImageMetaTable.IMAGECLASS);
        item.imageDesc = getString(cursor, ImageMetaTable.IMAGEDES);
        item.imageSeqNum = getInt(cursor, ImageMetaTable.IMAGESEQNUM);
        item.waterMark = getString(cursor, ImageMetaTable.WATERMARK);
        return item;
    }

    @Override
    public ContentValues modelToContentValues(ImageMetaBean item) {
        ContentValues values = new ContentValues();
        values.put(ImageMetaTable.IMAGECLASS, item.imageClass);
        values.put(ImageMetaTable.IMAGEDES, item.imageDesc);
        values.put(ImageMetaTable.IMAGESEQNUM, item.imageSeqNum);
        values.put(ImageMetaTable.WATERMARK, item.waterMark);
        return values;
    }
}

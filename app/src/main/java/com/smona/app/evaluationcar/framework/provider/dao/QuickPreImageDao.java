package com.smona.app.evaluationcar.framework.provider.dao;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.smona.app.evaluationcar.data.bean.QuickPreCarImageBean;
import com.smona.app.evaluationcar.framework.provider.DBConstants;
import com.smona.app.evaluationcar.framework.provider.table.QuickPreCarImageTable;
import com.smona.app.evaluationcar.util.CarLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by motianhu on 3/21/17.
 */

public class QuickPreImageDao extends BaseDao<QuickPreCarImageBean> {
    private static final String TAG = QuickPreImageDao.class.getSimpleName();

    public QuickPreImageDao(Context context) {
        super(context);
    }

    @Override
    public void initTable() {
        mTable = QuickPreCarImageTable.getInstance();
    }

    @Override
    public void deleteList(List<QuickPreCarImageBean> itemInfoList) {

    }

    @Override
    public void deleteItem(QuickPreCarImageBean itemInfo) {

    }

    @Override
    public void updateList(List<QuickPreCarImageBean> itemInfoList) {
        Uri uri = mTable.mContentUriNoNotify;

        ArrayList<ContentProviderOperation> arrayList = new ArrayList<ContentProviderOperation>();
        for (QuickPreCarImageBean carImage : itemInfoList) {
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newUpdate(uri);
            builder.withSelection(
                    QuickPreCarImageTable.CARBILLID + "=?",
                    new String[]{
                            carImage.carBillId
                    });
            builder.withValues(modelToContentValues(carImage));
            ContentProviderOperation contentProviderOperation = builder.build();
            arrayList.add(contentProviderOperation);
        }

        try {
            ContentProviderResult[] results = mContentResolver.applyBatch(DBConstants.AUTHORITY, arrayList);
            CarLog.d(TAG, results != null ? results.length + "" : 0 + "");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean updateItem(QuickPreCarImageBean carImage) {
        boolean isSave = carImage.imageId > 0;
        String where = null;
        String[] whereArgs = null;
        if (isSave) {
            where = QuickPreCarImageTable.IMAGEID + "=? and " +
                    QuickPreCarImageTable.IMAGESEQNUM + "=? and " + QuickPreCarImageTable.IMAGECLASS + "=?";
            whereArgs = new String[]{carImage.imageId + "", carImage.imageSeqNum + "", carImage.imageClass};
        } else {
            where = QuickPreCarImageTable.CARBILLID + "=? and " +
                    QuickPreCarImageTable.IMAGESEQNUM + "=? and " + QuickPreCarImageTable.IMAGECLASS + "=?";
            whereArgs = new String[]{carImage.carBillId + "", carImage.imageSeqNum + "", carImage.imageClass};
        }
        return mContentResolver.update(mTable.mContentUriNoNotify,
                modelToContentValues(carImage), where, whereArgs) > 0;
    }

    @Override
    public QuickPreCarImageBean cursorToModel(Cursor cursor) {
        QuickPreCarImageBean item = new QuickPreCarImageBean();
        item.imageId = getInt(cursor, QuickPreCarImageTable.IMAGEID);
        item.carBillId = getString(cursor, QuickPreCarImageTable.CARBILLID);
        item.imageClass = getString(cursor, QuickPreCarImageTable.IMAGECLASS);
        item.imageSeqNum = getInt(cursor, QuickPreCarImageTable.IMAGESEQNUM);
        item.imageLocalUrl = getString(cursor, QuickPreCarImageTable.IMAGELOCALURL);
        item.imagePath = getString(cursor, QuickPreCarImageTable.IMAGEREMOTEURL);
        item.imageThumbPath = getString(cursor, QuickPreCarImageTable.IMAGEREMOTETHUMBNAILURL);
        item.imageUpdate = getInt(cursor, QuickPreCarImageTable.IMAGEUPDATE);
        item.createTime = getString(cursor, QuickPreCarImageTable.CREATETIME);
        item.updateTime = getString(cursor, QuickPreCarImageTable.UPDATETIEM);
        item.id = getString(cursor, QuickPreCarImageTable.SERVER_ID);
        item.normalCarBillId = getString(cursor, QuickPreCarImageTable.NORMAL_CARBILLID);
        return item;
    }

    @Override
    public ContentValues modelToContentValues(QuickPreCarImageBean item) {
        ContentValues values = new ContentValues();
        values.put(QuickPreCarImageTable.IMAGEID, item.imageId);
        values.put(QuickPreCarImageTable.CARBILLID, item.carBillId);
        values.put(QuickPreCarImageTable.IMAGECLASS, item.imageClass);
        values.put(QuickPreCarImageTable.IMAGESEQNUM, item.imageSeqNum);
        values.put(QuickPreCarImageTable.IMAGELOCALURL, item.imageLocalUrl);
        values.put(QuickPreCarImageTable.IMAGEREMOTEURL, item.imagePath);
        values.put(QuickPreCarImageTable.IMAGEREMOTETHUMBNAILURL, item.imageThumbPath);
        values.put(QuickPreCarImageTable.IMAGEUPDATE, item.imageUpdate);
        values.put(QuickPreCarImageTable.CREATETIME, item.createTime);
        values.put(QuickPreCarImageTable.UPDATETIEM, item.updateTime);
        values.put(QuickPreCarImageTable.SERVER_ID, item.id);
        values.put(QuickPreCarImageTable.NORMAL_CARBILLID, item.normalCarBillId);
        return values;
    }
}

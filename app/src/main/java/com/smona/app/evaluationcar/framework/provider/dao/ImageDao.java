package com.smona.app.evaluationcar.framework.provider.dao;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.smona.app.evaluationcar.data.bean.CarImageBean;
import com.smona.app.evaluationcar.framework.provider.DBConstants;
import com.smona.app.evaluationcar.framework.provider.table.CarImageTable;
import com.smona.app.evaluationcar.util.CarLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by motianhu on 3/21/17.
 */

public class ImageDao extends BaseDao<CarImageBean> {
    private static final String TAG = ImageDao.class.getSimpleName();

    public ImageDao(Context context) {
        super(context);
    }

    @Override
    public void initTable() {
        mTable = CarImageTable.getInstance();
    }

    @Override
    public void deleteList(List<CarImageBean> itemInfoList) {

    }

    @Override
    public void deleteItem(CarImageBean itemInfo) {

    }

    @Override
    public void updateList(List<CarImageBean> itemInfoList) {
        Uri uri = mTable.mContentUriNoNotify;

        ArrayList<ContentProviderOperation> arrayList = new ArrayList<ContentProviderOperation>();
        for (CarImageBean carImage : itemInfoList) {
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newUpdate(uri);
            builder.withSelection(
                    CarImageTable.CARBILLID + "=?",
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
    public boolean updateItem(CarImageBean carImage) {
        boolean isSave = carImage.imageId > 0;
        String where = null;
        String[] whereArgs = null;
        if (isSave) {
            where = CarImageTable.IMAGEID + "=? and " +
                    CarImageTable.IMAGESEQNUM + "=? and " + CarImageTable.IMAGECLASS + "=?";
            whereArgs = new String[]{carImage.imageId + "", carImage.imageSeqNum + "", carImage.imageClass};
        } else {
            where = CarImageTable.CARBILLID + "=? and " +
                    CarImageTable.IMAGESEQNUM + "=? and " + CarImageTable.IMAGECLASS + "=?";
            whereArgs = new String[]{carImage.carBillId + "", carImage.imageSeqNum + "", carImage.imageClass};
        }
        return mContentResolver.update(mTable.mContentUriNoNotify,
                modelToContentValues(carImage), where, whereArgs) > 0;
    }

    @Override
    public CarImageBean cursorToModel(Cursor cursor) {
        CarImageBean item = new CarImageBean();
        item.imageId = getInt(cursor, CarImageTable.IMAGEID);
        item.carBillId = getString(cursor, CarImageTable.CARBILLID);
        item.imageClass = getString(cursor, CarImageTable.IMAGECLASS);
        item.imageSeqNum = getInt(cursor, CarImageTable.IMAGESEQNUM);
        item.imageLocalUrl = getString(cursor, CarImageTable.IMAGELOCALURL);
        item.imagePath = getString(cursor, CarImageTable.IMAGEREMOTEURL);
        item.imageThumbPath = getString(cursor, CarImageTable.IMAGEREMOTETHUMBNAILURL);
        item.imageUpdate = getInt(cursor, CarImageTable.IMAGEUPDATE);
        item.createTime = getString(cursor, CarImageTable.CREATETIME);
        item.updateTime = getString(cursor, CarImageTable.UPDATETIEM);
        return item;
    }

    @Override
    public ContentValues modelToContentValues(CarImageBean item) {
        ContentValues values = new ContentValues();
        values.put(CarImageTable.IMAGEID, item.imageId);
        values.put(CarImageTable.CARBILLID, item.carBillId);
        values.put(CarImageTable.IMAGECLASS, item.imageClass);
        values.put(CarImageTable.IMAGESEQNUM, item.imageSeqNum);
        values.put(CarImageTable.IMAGELOCALURL, item.imageLocalUrl);
        values.put(CarImageTable.IMAGEREMOTEURL, item.imagePath);
        values.put(CarImageTable.IMAGEREMOTETHUMBNAILURL, item.imageThumbPath);
        values.put(CarImageTable.IMAGEUPDATE, item.imageUpdate);
        values.put(CarImageTable.CREATETIME, item.createTime);
        values.put(CarImageTable.UPDATETIEM, item.updateTime);
        return values;
    }
}

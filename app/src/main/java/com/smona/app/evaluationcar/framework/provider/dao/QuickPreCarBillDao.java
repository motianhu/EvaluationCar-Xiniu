package com.smona.app.evaluationcar.framework.provider.dao;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.smona.app.evaluationcar.data.bean.QuickPreCarBillBean;
import com.smona.app.evaluationcar.framework.provider.DBConstants;
import com.smona.app.evaluationcar.framework.provider.table.QuickPreCarBillTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by motianhu on 6/11/17.
 */

public class QuickPreCarBillDao extends BaseDao<QuickPreCarBillBean> {

    private static final String TAG = QuickPreCarBillDao.class.getSimpleName();

    public QuickPreCarBillDao(Context context) {
        super(context);
    }


    @Override
    public void initTable() {
        mTable = QuickPreCarBillTable.getInstance();
    }

    @Override
    public void deleteList(List<QuickPreCarBillBean> itemInfoList) {

    }

    @Override
    public void deleteItem(QuickPreCarBillBean carBill) {
        String where = QuickPreCarBillTable.CARBILLID + "=?";
        String[] whereArgs = new String[]{
                carBill.carBillId
        };
        mContentResolver.delete(mTable.mContentUriNoNotify, where, whereArgs);
    }

    @Override
    public void updateList(List<QuickPreCarBillBean> itemInfoList) {
        Uri uri = mTable.mContentUriNoNotify;

        ArrayList<ContentProviderOperation> arrayList = new ArrayList<ContentProviderOperation>();
        for (QuickPreCarBillBean carBill : itemInfoList) {
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newUpdate(uri);
            builder.withSelection(
                    QuickPreCarBillTable.CARBILLID + "=?",
                    new String[]{
                            carBill.carBillId
                    });
            builder.withValues(modelToContentValues(carBill));
            ContentProviderOperation contentProviderOperation = builder.build();
            arrayList.add(contentProviderOperation);
        }

        try {
            mContentResolver.applyBatch(DBConstants.AUTHORITY, arrayList);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean updateItem(QuickPreCarBillBean carBill) {
        String where = null;
        String[] whereArgs = null;
        if (carBill.imageId > 0) {
            where = QuickPreCarBillTable.IMAGEID + "=?";
            whereArgs = new String[]{
                    carBill.imageId + ""
            };
        } else {
            where = QuickPreCarBillTable.CARBILLID + "=?";
            whereArgs = new String[]{
                    carBill.carBillId
            };
        }
        int count = mContentResolver.update(mTable.mContentUriNoNotify,
                modelToContentValues(carBill), where, whereArgs);
        return count > 0;
    }

    @Override
    public QuickPreCarBillBean cursorToModel(Cursor cursor) {
        QuickPreCarBillBean item = new QuickPreCarBillBean();
        item.carBillId = getString(cursor, QuickPreCarBillTable.CARBILLID);
        item.status = getInt(cursor, QuickPreCarBillTable.BILLSTATUS);
        item.createTime = getString(cursor, QuickPreCarBillTable.CREATETIME);
        item.modifyTime = getString(cursor, QuickPreCarBillTable.MODIFYTIME);
        item.preSalePrice = getDouble(cursor, QuickPreCarBillTable.PRESALEPRICE);
        item.imageThumbPath = getString(cursor, QuickPreCarBillTable.THUMBUrl);
        item.mark = getString(cursor, QuickPreCarBillTable.MARK);
        item.imageId = getInt(cursor, QuickPreCarBillTable.IMAGEID);
        item.uploadStatus = getInt(cursor, QuickPreCarBillTable.UPLOADStATUS);
        return item;
    }

    @Override
    public ContentValues modelToContentValues(QuickPreCarBillBean item) {
        ContentValues values = new ContentValues();
        values.put(QuickPreCarBillTable.CARBILLID, item.carBillId);
        values.put(QuickPreCarBillTable.BILLSTATUS, item.status);
        values.put(QuickPreCarBillTable.CREATETIME, item.createTime);
        values.put(QuickPreCarBillTable.MODIFYTIME, item.modifyTime);
        values.put(QuickPreCarBillTable.PRESALEPRICE, item.preSalePrice);
        values.put(QuickPreCarBillTable.THUMBUrl, item.imageThumbPath);
        values.put(QuickPreCarBillTable.MARK, item.mark);
        values.put(QuickPreCarBillTable.IMAGEID, item.imageId);
        values.put(QuickPreCarBillTable.UPLOADStATUS, item.uploadStatus);
        return values;
    }
}

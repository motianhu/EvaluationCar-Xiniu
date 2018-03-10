package com.smona.app.evaluationcar.framework.provider.dao;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.smona.app.evaluationcar.data.bean.CarBillBean;
import com.smona.app.evaluationcar.framework.provider.DBConstants;
import com.smona.app.evaluationcar.framework.provider.table.CarBillTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by motianhu on 3/21/17.
 */

public class CarBillDao extends BaseDao<CarBillBean> {
    private static final String TAG = CarBillDao.class.getSimpleName();

    public CarBillDao(Context context) {
        super(context);
    }

    @Override
    public void initTable() {
        mTable = CarBillTable.getInstance();
    }

    @Override
    public void deleteList(List<CarBillBean> itemInfoList) {

    }

    @Override
    public void deleteItem(CarBillBean carBill) {
        String where = CarBillTable.CARBILLID + "=?";
        String[] whereArgs = new String[]{
                carBill.carBillId
        };
        mContentResolver.delete(mTable.mContentUriNoNotify, where, whereArgs);
    }

    @Override
    public void updateList(List<CarBillBean> itemInfoList) {
        Uri uri = mTable.mContentUriNoNotify;

        ArrayList<ContentProviderOperation> arrayList = new ArrayList<ContentProviderOperation>();
        for (CarBillBean carBill : itemInfoList) {
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newUpdate(uri);
            builder.withSelection(
                    CarBillTable.CARBILLID + "=?",
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
    public boolean updateItem(CarBillBean carBill) {
        String where = null;
        String[] whereArgs = null;
        if (carBill.imageId > 0) {
            where = CarBillTable.IMAGEID + "=?";
            whereArgs = new String[]{
                    carBill.imageId + ""
            };
        } else {
            where = CarBillTable.CARBILLID + "=?";
            whereArgs = new String[]{
                    carBill.carBillId
            };
        }
        int count = mContentResolver.update(mTable.mContentUriNoNotify,
                modelToContentValues(carBill), where, whereArgs);
        return count > 0;
    }

    @Override
    public CarBillBean cursorToModel(Cursor cursor) {
        CarBillBean item = new CarBillBean();
        item.carBillId = getString(cursor, CarBillTable.CARBILLID);
        item.status = getInt(cursor, CarBillTable.BILLSTATUS);
        item.createTime = getString(cursor, CarBillTable.CREATETIME);
        item.modifyTime = getString(cursor, CarBillTable.MODIFYTIME);
        item.preSalePrice = getDouble(cursor, CarBillTable.PRESALEPRICE);
        item.evaluatePrice = getDouble(cursor, CarBillTable.EVALUATEPRICE);
        item.imageThumbPath = getString(cursor, CarBillTable.THUMBUrl);
        item.mark = getString(cursor, CarBillTable.MARK);
        item.applyAllOpinion = getString(cursor, CarBillTable.APPLYALLOPINION);
        item.imageId = getInt(cursor, CarBillTable.IMAGEID);
        item.uploadStatus = getInt(cursor, CarBillTable.UPLOADStATUS);
        //2.0
        item.leaseTerm = getInt(cursor, CarBillTable.LEASETERM);
        return item;
    }

    @Override
    public ContentValues modelToContentValues(CarBillBean item) {
        ContentValues values = new ContentValues();
        values.put(CarBillTable.CARBILLID, item.carBillId);
        values.put(CarBillTable.BILLSTATUS, item.status);
        values.put(CarBillTable.CREATETIME, item.createTime);
        values.put(CarBillTable.MODIFYTIME, item.modifyTime);
        values.put(CarBillTable.PRESALEPRICE, item.preSalePrice);
        values.put(CarBillTable.EVALUATEPRICE, item.evaluatePrice);
        values.put(CarBillTable.THUMBUrl, item.imageThumbPath);
        values.put(CarBillTable.MARK, item.mark);
        values.put(CarBillTable.APPLYALLOPINION, item.applyAllOpinion);
        values.put(CarBillTable.IMAGEID, item.imageId);
        values.put(CarBillTable.UPLOADStATUS, item.uploadStatus);
        //2.0
        values.put(CarBillTable.LEASETERM, item.leaseTerm);
        return values;
    }
}

package com.smona.app.evaluationcar.framework.provider.dao;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.smona.app.evaluationcar.framework.provider.DBConstants;
import com.smona.app.evaluationcar.framework.provider.table.BaseTable;
import com.smona.app.evaluationcar.util.CarLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by motianhu on 3/21/17.
 */

public abstract class BaseDao<T> {
    public static final int TYPE_INSERT = 0x1;
    public static final int TYPE_DELETE = 0x2;
    public static final int TYPE_UPDATE = 0x3;
    public static final int SINGLE_AFFECT_DATA = 0x1;
    public static final int LIST_AFFECT_DATA = 0x2;
    private static final String TAG = BaseDao.class.getSimpleName();
    protected BaseTable mTable;
    protected Context mContext;
    protected ContentResolver mContentResolver;

    public BaseDao(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
        initTable();
    }

    public List<T> getResult(String selection, String[] args, String order) {

        List<T> infoList = new ArrayList<T>();
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(
                    mTable.mContentUriNoNotify,
                    null, selection,
                    args, order);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    T info = cursorToModel(cursor);
                    infoList.add(info);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            CarLog.d(TAG, "getResult error=" + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return infoList;

    }

    public T getItem(String identifier, String idColumnName) {
        T info = null;
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(
                    mTable.mContentUriNoNotify,
                    null,
                    idColumnName + "=?",
                    new String[]{
                            identifier
                    }, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                info = cursorToModel(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return info;
    }

    public void insertList(List<T> itemInfoList) {

        if (itemInfoList == null || itemInfoList.size() == 0) {
            return;
        }

        Uri uri = mTable.mContentUriNoNotify;
        ArrayList<ContentProviderOperation> arrayList = new ArrayList<ContentProviderOperation>();
        for (T itemInfo : itemInfoList) {
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newInsert(uri);
            builder.withValues(modelToContentValues(itemInfo));
            ContentProviderOperation contentProviderOperation = builder.build();
            arrayList.add(contentProviderOperation);
        }

        try {
            mContentResolver.applyBatch(DBConstants.AUTHORITY, arrayList);
        } catch (RemoteException e) {
            e.printStackTrace();
            CarLog.d(TAG, "e " + e);
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            CarLog.d(TAG, "e " + e);
        }
    }

    public boolean insertItem(T itemInfo) {
        if (itemInfo == null) {
            return false;
        }
        return mContentResolver.insert(mTable.mContentUriNoNotify, modelToContentValues(itemInfo)) != null;
    }

    public void clear() {
        mContentResolver.delete(mTable.mContentUriNoNotify,
                null, null);
    }

    public void deleteBatch(String where) {
        mContentResolver.delete(mTable.mContentUriNoNotify,
                where, null);
    }

    protected String getString(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return cursor.getString(index);
    }

    protected int getInt(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return cursor.getInt(index);
    }

    protected double getDouble(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return cursor.getDouble(index);
    }


    public abstract void initTable();

    public abstract void deleteList(List<T> itemInfoList);

    public abstract void deleteItem(T itemInfo);

    public abstract void updateList(List<T> itemInfoList);

    public abstract boolean updateItem(T itemInfo);

    public abstract T cursorToModel(Cursor cursor);

    public abstract ContentValues modelToContentValues(T data);
}

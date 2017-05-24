package com.smona.app.xiniu.framework.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.smona.app.xiniu.framework.provider.table.CarBillTable;
import com.smona.app.xiniu.framework.provider.table.CarImageTable;
import com.smona.app.xiniu.util.CarLog;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Moth on 2016/12/18.
 */

public class EvaluationProvider extends ContentProvider {
    private final static String TAG = EvaluationProvider.class.getSimpleName();
    private static final String PARAMETER_NOTIFY = "notify";
    private static final int CODE_BASE = 0;
    private static final int CODE_CARBILL = CODE_BASE + 1;
    private static final int CODE_CARIMAGE = CODE_BASE + 2;
    private static final int CODE_IMAGEMETA = CODE_BASE + 3;
    private static final UriMatcher URI_MATCH = new UriMatcher(
            UriMatcher.NO_MATCH);
    private static HashMap<Integer, String> TABLE_MATCH = new HashMap<Integer, String>();

    static {
        URI_MATCH.addURI(DBConstants.AUTHORITY,
                CarBillTable.TABLE_NAME, CODE_CARBILL);
        URI_MATCH.addURI(DBConstants.AUTHORITY,
                CarImageTable.TABLE_NAME, CODE_CARIMAGE);
        TABLE_MATCH.put(CODE_CARBILL, CarBillTable.TABLE_NAME);
        TABLE_MATCH.put(CODE_CARIMAGE, CarImageTable.TABLE_NAME);
    }

    private DatabaseHelper mDataHelper;

    @Override
    public boolean onCreate() {
        CarLog.d(TAG, "onCreate");
        mDataHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        int match = URI_MATCH.match(uri);
        String tableName = TABLE_MATCH.get(match);
        qb.setTables(tableName);

        SQLiteDatabase db = mDataHelper.getReadableDatabase();
        return qb.query(db, projection, selection, selectionArgs, null, null,
                sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCH.match(uri);
        String tableName = TABLE_MATCH.get(match);
        CarLog.d(TAG, "getType uri: " + uri + ";tableName: " + tableName);
        if (tableName != null) {
            return "vnd.android.cursor.dir/wallpaper";
        } else {
            throw new IllegalArgumentException("Unknown URL");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = URI_MATCH.match(uri);
        SQLiteDatabase db = mDataHelper.getWritableDatabase();
        String tableName = TABLE_MATCH.get(match);
        long rowId = db.insert(tableName, null, values);
        Uri newUrl = null;
        if (rowId != -1) {
            newUrl = ContentUris.withAppendedId(uri, rowId);
            sendNotify(uri);
        }
        return newUrl;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDataHelper.getWritableDatabase();

        int count = -1;
        int matchValue = URI_MATCH.match(uri);
        String tableName = TABLE_MATCH.get(matchValue);
        count = db.delete(tableName, selection, selectionArgs);
        sendNotify(uri);

        return count;
    }

    private void sendNotify(Uri uri) {
        String notify = uri.getQueryParameter(PARAMETER_NOTIFY);
        if (notify == null || "true".equals(notify)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mDataHelper.getWritableDatabase();

        int count = -1;
        int match = URI_MATCH.match(uri);
        String tableName = TABLE_MATCH.get(match);
        count = db.update(tableName, values, selection, selectionArgs);

        sendNotify(uri);
        return count;
    }

    class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "evaluation.db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            CarLog.d(TAG, "DatabaseHelper DATABASE_VERSION= " + DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            CarLog.d(TAG, "DatabaseHelper onCreate");
            ArrayList<String> sqlList = getCreateTableSqlList();
            for (String sql : sqlList) {
                db.execSQL(sql);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            CarLog.d(TAG, "DatabaseHelper onUpgrade oldVersion=" + oldVersion + "; newVersion=" + newVersion);
        }


        private ArrayList<String> getCreateTableSqlList() {
            ArrayList<String> sqlList = new ArrayList<String>();
            String carbills = CarBillTable.getInstance().createTableSql();
            String carimage = CarImageTable.getInstance().createTableSql();

            sqlList.add(carbills);
            sqlList.add(carimage);
            return sqlList;
        }
    }
}

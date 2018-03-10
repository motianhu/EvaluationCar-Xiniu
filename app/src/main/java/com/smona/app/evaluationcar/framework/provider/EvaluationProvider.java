package com.smona.app.evaluationcar.framework.provider;

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

import com.smona.app.evaluationcar.framework.provider.table.CarBillTable;
import com.smona.app.evaluationcar.framework.provider.table.CarImageTable;
import com.smona.app.evaluationcar.framework.provider.table.ImageMetaTable;
import com.smona.app.evaluationcar.framework.provider.table.QuickPreCarBillTable;
import com.smona.app.evaluationcar.framework.provider.table.QuickPreCarImageTable;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.framework.EvaluationApp;

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
    private static final int CODE_QUICKPRECARBILL = CODE_BASE + 4;
    private static final int CODE_QUICKPREIMAGE = CODE_BASE + 5;
    private static final UriMatcher URI_MATCH = new UriMatcher(
            UriMatcher.NO_MATCH);
    private static HashMap<Integer, String> TABLE_MATCH = new HashMap<Integer, String>();

    static {
        URI_MATCH.addURI(DBConstants.AUTHORITY,
                CarBillTable.TABLE_NAME, CODE_CARBILL);
        URI_MATCH.addURI(DBConstants.AUTHORITY,
                CarImageTable.TABLE_NAME, CODE_CARIMAGE);
        URI_MATCH.addURI(DBConstants.AUTHORITY,
                ImageMetaTable.TABLE_NAME, CODE_IMAGEMETA);
        URI_MATCH.addURI(DBConstants.AUTHORITY,
                QuickPreCarBillTable.TABLE_NAME, CODE_QUICKPRECARBILL);
        URI_MATCH.addURI(DBConstants.AUTHORITY,
                QuickPreCarImageTable.TABLE_NAME, CODE_QUICKPREIMAGE);

        TABLE_MATCH.put(CODE_CARBILL, CarBillTable.TABLE_NAME);
        TABLE_MATCH.put(CODE_CARIMAGE, CarImageTable.TABLE_NAME);
        TABLE_MATCH.put(CODE_IMAGEMETA, ImageMetaTable.TABLE_NAME);
        TABLE_MATCH.put(CODE_QUICKPRECARBILL, QuickPreCarBillTable.TABLE_NAME);
        TABLE_MATCH.put(CODE_QUICKPREIMAGE, QuickPreCarImageTable.TABLE_NAME);
    }

    private DatabaseHelper mDataHelper;

    @Override
    public boolean onCreate() {
        CarLog.d(TAG, "onCreate");
        mDataHelper = new DatabaseHelper(getContext());
        ((EvaluationApp)getContext().getApplicationContext()).setProvider(this);
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

    public void clearAllTableData() {
        SQLiteDatabase db = mDataHelper.getWritableDatabase();
        ArrayList<String> sqlList = mDataHelper.getDeleteTableSqlList();
        for(String sql: sqlList) {
            db.execSQL(sql);
        }
    }

    class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "evaluation.db";
        private static final int DATABASE_VERSION = 4;

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
            if(oldVersion == 1) {
                String carbills = CarBillTable.getInstance().dropTableSql();
                db.execSQL(carbills);
                carbills = CarBillTable.getInstance().createTableSql();
                db.execSQL(carbills);
            } else if(oldVersion == 2) {
                String quickprecarbills = QuickPreCarBillTable.getInstance().createTableSql();
                db.execSQL(quickprecarbills);
                String quickprecarimage = QuickPreCarImageTable.getInstance().createTableSql();
                db.execSQL(quickprecarimage);
            }

            if(newVersion == 4) {
                String dropPreImage = QuickPreCarImageTable.getInstance().dropTableSql();
                db.execSQL(dropPreImage);
                String createPreImage = QuickPreCarImageTable.getInstance().createTableSql();
                db.execSQL(createPreImage);
            }
        }


        private ArrayList<String> getCreateTableSqlList() {
            ArrayList<String> sqlList = new ArrayList<String>();
            String carbills = CarBillTable.getInstance().createTableSql();
            String carimage = CarImageTable.getInstance().createTableSql();
            String imagemeta = ImageMetaTable.getInstance().createTableSql();
            String quickprecarbills = QuickPreCarBillTable.getInstance().createTableSql();
            String quickprecarimage = QuickPreCarImageTable.getInstance().createTableSql();

            sqlList.add(carbills);
            sqlList.add(carimage);
            sqlList.add(imagemeta);
            sqlList.add(quickprecarbills);
            sqlList.add(quickprecarimage);
            return sqlList;
        }

        public ArrayList<String> getDeleteTableSqlList() {
            ArrayList<String> sqlList = new ArrayList<String>();
            String carbills = CarBillTable.getInstance().deleteTableSql();
            String carimage = CarImageTable.getInstance().deleteTableSql();
            String imagemeta = ImageMetaTable.getInstance().deleteTableSql();
            String quickprecarbills = QuickPreCarBillTable.getInstance().deleteTableSql();
            String quickprecarimage = QuickPreCarImageTable.getInstance().deleteTableSql();

            sqlList.add(carbills);
            sqlList.add(carimage);
            sqlList.add(imagemeta);
            sqlList.add(quickprecarbills);
            sqlList.add(quickprecarimage);
            return sqlList;
        }
    }
}

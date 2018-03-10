package com.smona.app.evaluationcar.framework.provider.dao;

import android.content.Context;

/**
 * Created by motianhu on 3/21/17.
 */

public class DaoFactory {
    public static final int TYPE_CARBILL = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_IMAGEMETA = 3;
    public static final int TYPE_UPLOADTASK = 4;

    public static final int TYPE_QUICKIMAGE = 5;
    public static final int TYPE_QUICKPRECARBILL = 6;

    public static BaseDao buildDaoEntry(Context context, int type) {
        BaseDao dao = null;
        if (type == TYPE_CARBILL) {
            dao = new CarBillDao(context);
        } else if (type == TYPE_IMAGE) {
            dao = new ImageDao(context);
        } else if (type == TYPE_IMAGEMETA) {
            dao = new ImageMetaDao(context);
        } else if (type == TYPE_QUICKIMAGE) {
            dao = new QuickPreImageDao(context);
        } else if (type == TYPE_QUICKPRECARBILL) {
            dao = new QuickPreCarBillDao(context);
        }
        return dao;
    }
}

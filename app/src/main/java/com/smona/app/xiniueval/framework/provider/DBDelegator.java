package com.smona.app.xiniueval.framework.provider;

import android.content.Context;

import com.smona.app.xiniueval.data.bean.CarBillBean;
import com.smona.app.xiniueval.data.bean.CarImageBean;
import com.smona.app.xiniueval.data.bean.ImageMetaBean;
import com.smona.app.xiniueval.framework.provider.dao.BaseDao;
import com.smona.app.xiniueval.framework.provider.dao.DaoFactory;
import com.smona.app.xiniueval.framework.provider.table.CarBillTable;
import com.smona.app.xiniueval.framework.provider.table.CarImageTable;
import com.smona.app.xiniueval.framework.provider.table.ImageMetaTable;
import com.smona.app.xiniueval.util.StatusUtils;

import java.util.List;

/**
 * Created by motianhu on 3/20/17.
 */

public class DBDelegator {
    private static volatile DBDelegator sInstance;
    private Context mAppContext;

    private DBDelegator() {
    }

    public static DBDelegator getInstance() {
        if (sInstance == null) {
            sInstance = new DBDelegator();
        }
        return sInstance;
    }

    public void init(Context context) {
        mAppContext = context;
    }


    //Car Image
    public List<CarImageBean> queryHttpImages(String carBillId) {
        BaseDao<CarImageBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_IMAGE);
        String select = CarImageTable.CARBILLID + "=?";
        List<CarImageBean> list = dao.getResult(select, new String[]{carBillId}, CarImageTable.IMAGESEQNUM + " asc ");
        return list;
    }

    public List<CarImageBean> queryImages(int imageId) {
        BaseDao<CarImageBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_IMAGE);
        String select = CarImageTable.IMAGEID + "=?";
        List<CarImageBean> list = dao.getResult(select, new String[]{imageId + ""}, CarImageTable.IMAGESEQNUM + " asc ");
        return list;
    }

    public List<CarImageBean> queryUpdateImages(String carBillId) {
        BaseDao<CarImageBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_IMAGE);
        String select = CarImageTable.CARBILLID + " =? and " + CarImageTable.IMAGEUPDATE + " =? ";
        List<CarImageBean> list = dao.getResult(select, new String[]{carBillId, StatusUtils.IMAGE_UPDATE + ""}, CarImageTable.IMAGESEQNUM + " asc ");
        return list;
    }


    public List<CarImageBean> queryImages(String imageClass, int imageId) {
        BaseDao<CarImageBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_IMAGE);
        String select = CarImageTable.IMAGEID + "=? and " + CarImageTable.IMAGECLASS + "=?";
        List<CarImageBean> list = dao.getResult(select, new String[]{imageId + "", imageClass}, CarImageTable.IMAGESEQNUM + " asc ");
        return list;
    }

    public List<CarImageBean> queryImages(String imageClass, String carBillId) {
        BaseDao<CarImageBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_IMAGE);
        String select = CarImageTable.CARBILLID + "=? and " + CarImageTable.IMAGECLASS + "=? ";
        List<CarImageBean> list = dao.getResult(select, new String[]{carBillId, imageClass}, CarImageTable.IMAGESEQNUM + " asc ");
        return list;
    }

    public CarImageBean queryImageClassForImageId(int imageId, String imageClass, int imageSeqNum) {
        BaseDao<CarImageBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_IMAGE);
        String select = CarImageTable.IMAGEID + "=? and " + CarImageTable.IMAGECLASS + "=? and " + CarImageTable.IMAGESEQNUM + "=?";
        List<CarImageBean> list = dao.getResult(select, new String[]{imageId + "", imageClass, imageSeqNum + ""}, CarImageTable.IMAGESEQNUM + " asc ");
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public CarImageBean queryImageClassForCarBillId(String carBillId, String imageClass, int imageSeqNum) {
        BaseDao<CarImageBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_IMAGE);
        String select = CarImageTable.CARBILLID + "=? and " + CarImageTable.IMAGECLASS + "=? and " + CarImageTable.IMAGESEQNUM + "=?";
        List<CarImageBean> list = dao.getResult(select, new String[]{carBillId, imageClass, imageSeqNum + ""}, CarImageTable.IMAGESEQNUM + " asc ");
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public boolean insertCarImage(CarImageBean bean) {
        BaseDao<CarImageBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_IMAGE);
        return dao.insertItem(bean);
    }

    public boolean updateCarImage(CarImageBean carImage) {
        BaseDao<CarImageBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_IMAGE);
        return dao.updateItem(carImage);
    }

    //ImageMeta
    public ImageMetaBean queryImageMeta(String imageClass, int imageSeqNum) {
        BaseDao<ImageMetaBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_IMAGEMETA);
        String where = ImageMetaTable.IMAGECLASS + "=? and " + ImageMetaTable.IMAGESEQNUM + "=?";
        String[] whereArgs = new String[]{imageClass, imageSeqNum + ""};
        List<ImageMetaBean> list = dao.getResult(where, whereArgs, CarImageTable.IMAGESEQNUM + " asc ");
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    //CarBill
    public CarBillBean queryCarBill(String carBillId) {
        BaseDao<CarBillBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_CARBILL);
        String where = CarBillTable.CARBILLID + "=?";
        String[] whereArgs = new String[]{carBillId};
        List<CarBillBean> list = dao.getResult(where, whereArgs, null);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    //queryType=0--all;queryType=1--local;queryType=2--inject
    public List<CarBillBean> queryNoSubmitCarBill(int queryType, int curPage, int pageSize) {
        BaseDao<CarBillBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_CARBILL);
        String localSelect = "(" + CarBillTable.BILLSTATUS + " =0 and " + CarBillTable.IMAGEID + " >0 )";
        String injectSelect = "(" + CarBillTable.BILLSTATUS + " in (23,33,43,53))";
        String select =  localSelect + " or " + injectSelect;
        if(queryType == 1) {
            select = localSelect;
        } else if (queryType == 2) {
            select = injectSelect;
        }
        String order = CarBillTable.CREATETIME + " desc limit " + (curPage - 1) * pageSize + "," + pageSize;
        List<CarBillBean> list = dao.getResult(select, null, order);
        return list;
    }

    public CarBillBean queryLocalCarbill(int imageId) {
        BaseDao<CarBillBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_CARBILL);
        String select = CarBillTable.IMAGEID + "=" + imageId;
        List<CarBillBean> list = dao.getResult(select, null, null);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void updateCarBill(CarBillBean carBill) {
        BaseDao<CarBillBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_CARBILL);
        dao.updateItem(carBill);
    }

    public boolean insertCarBill(CarBillBean bean) {
        BaseDao<CarBillBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_CARBILL);
        return dao.insertItem(bean);
    }

    public List<CarBillBean> queryCarBillInUpload() {
        BaseDao<CarBillBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_CARBILL);
        String where = CarBillTable.UPLOADStATUS + "=" + StatusUtils.BILL_UPLOAD_STATUS_UPLOADING;
        String orderBy = CarBillTable.MODIFYTIME + " desc ";
        List<CarBillBean> list = dao.getResult(where, null, orderBy);
        return list;
    }

    public void deleteCarbill(CarBillBean carBill) {
        BaseDao<CarBillBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_CARBILL);
        dao.deleteItem(carBill);
    }

    public void deleteBatchCarImages(int imageId) {
        BaseDao<CarBillBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_IMAGE);
        String where = CarImageTable.IMAGEID + " = " + imageId;
        dao.deleteBatch(where);
    }

    //Image Meta
    public boolean insertImageMeta(ImageMetaBean bean) {
        BaseDao<ImageMetaBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_IMAGEMETA);
        return dao.insertItem(bean);
    }

    public void updateImageMeta(ImageMetaBean bean) {
        BaseDao<ImageMetaBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_IMAGEMETA);
        dao.updateItem(bean);
    }


    //AUTO MAX ID
    public int getDBMaxId() {
        BaseDao<CarImageBean> dao = DaoFactory.buildDaoEntry(mAppContext, DaoFactory.TYPE_IMAGE);
        List<CarImageBean> list = dao.getResult(null, null, " imageId desc ");
        if (list != null && list.size() > 1) {
            return list.get(0).imageId;
        }
        return 0;
    }
}

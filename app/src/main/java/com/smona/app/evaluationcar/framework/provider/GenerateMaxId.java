package com.smona.app.evaluationcar.framework.provider;

/**
 * Created by motianhu on 3/22/17.
 */

public class GenerateMaxId {
    private static volatile GenerateMaxId sInstance;
    private int mMaxId = 0;

    private GenerateMaxId() {
    }

    public static GenerateMaxId getInstance() {
        if (sInstance == null) {
            sInstance = new GenerateMaxId();
        }
        return sInstance;
    }

    public void initMaxId() {
        mMaxId = DBDelegator.getInstance().getDBMaxId();
    }

    public int generateNewId() {
        if (mMaxId <= 0) {
            throw new RuntimeException("Error: max id was not initialized");
        }
        mMaxId += 1;
        return mMaxId;
    }
}

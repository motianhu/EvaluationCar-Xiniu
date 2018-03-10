package com.smona.app.evaluationcar.data.model;

/**
 * Created by motianhu on 4/11/17.
 */

public class ResUpgradeApi extends ResBaseApi {
    public static final String UPDATE_TYPE_FORCE = "force";
    public String id;
    public String clientName;
    public String versionName;
    public String publishTime;
    public String updateType;
    public String apiURL;
}

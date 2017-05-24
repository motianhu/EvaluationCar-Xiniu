package com.smona.app.xiniu.data.bean;

/**
 * Created by Moth on 2017/3/29.
 */

public class UserInfoBean extends BaseBean {
    public int userId;
    public int userCompany;
    public int userSuperCompany;
    public String superCompanyName;
    public String userChineseName;
    public String companyName;
    public String userLoginName;

    public String toString() {
        return "userId=" + userId + ", userCompany=" + userCompany + ", userSuperCompany=" + userSuperCompany
                + ",superCompanyName=" + superCompanyName + ", userChineseName=" + userChineseName
                + ", companyName=" + companyName + ", userLoginName=" + userLoginName;
    }

    public boolean isXianfeng() {
        return userSuperCompany == 9 || userCompany == 9;
    }
}

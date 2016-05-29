package com.lcw.myapplication.util;


import android.text.TextUtils;

import com.lcw.myapplication.MyApplication;

/**
 *
 * @author 刘春旺
 *
 */
public class UserInfo {

    private static final String PHONE = "phone";
    private static final String USERNAME = "username";
    private static final String UID = "uid";
    private static final String PASSWORD = "password";

    /**
     * 获取手机号
     */
    public static String getPhone(){
        return MyApplication.cfg.getStringShareData(PHONE);
    }

    /**
     * 获取用户名
     */
    public static String getUsername(){
        return MyApplication.cfg.getStringShareData(USERNAME);
    }

    /**
     * 获取用户UID
     */
    public static String getUid(){
        return MyApplication.cfg.getStringShareData(UID);
    }

    /**
     * 获取用户密码
     */
    public static String getPassword(){
        return MyApplication.cfg.getStringShareData(PASSWORD);
    }

    /**
     * 保存用户基本信息
     */
    public static void setUserInfo(String phone, String username, String uid, String password){
        if (phone != null) {
            MyApplication.cfg.storeShareDataWithCommit(PHONE, phone);
        }
        if (username != null){
            MyApplication.cfg.storeShareDataWithCommit(USERNAME, username);
        }
        if (uid != null){
            MyApplication.cfg.storeShareDataWithCommit(UID, uid);
        }
        if (password != null){
            MyApplication.cfg.storeShareDataWithCommit(PASSWORD, password);
        }
        MyApplication.cfg.commit();
    }

    /**
     * 退出登录
     */
    public static void exitLogin(){
        MyApplication.cfg.storeShareDataWithCommit(UID, "");
        MyApplication.cfg.storeShareDataWithCommit(PASSWORD, "");

        MyApplication.cfg.storeBooleanShareData(ConfigUtil.IS_NEED_SCREEN_PASS, false);
        MyApplication.cfg.commit();
    }

    /**
     * 判断是否登录状态
     */
    public static boolean isLogin(){
        if (!TextUtils.isEmpty(MyApplication.cfg.getStringShareData(UID))&&
                !TextUtils.isEmpty(MyApplication.cfg.getStringShareData(PASSWORD))){
            return true;
        }
        return false;
    }
}

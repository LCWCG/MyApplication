package com.lcw.myapplication;


import android.app.Application;

import com.lcw.myapplication.util.ConfigUtil;
import com.lcw.myapplication.util.ServerApi;

/**
 *
 * @author 刘春旺
 *
 */
public class MyApplication extends Application{

    private static MyApplication mApplication;

    public static MyApplication getApplication(){
        return mApplication;
    }

    public static ConfigUtil cfg = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        ServerApi.init(mApplication);

        if (cfg == null) {
            cfg = ConfigUtil.ins(this);
        }
    }
}

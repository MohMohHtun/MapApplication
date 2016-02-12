package com.example.mohmohhtun.mapapplication;

import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by mohmohhtun on 2/2/16.
 */
public class Application  extends android.app.Application{
    private static Application instance = null;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = new Application();
    }

    public static Application getInstance() {
        if (instance == null)
            instance = new Application();
        return instance;
    }
    protected void attachBaseContext(Context base){
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

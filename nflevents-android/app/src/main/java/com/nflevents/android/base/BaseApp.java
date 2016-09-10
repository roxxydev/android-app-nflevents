package com.nflevents.android.base;

import android.app.Application;
import com.nflevents.android.core.AppConfiguration;

/**
 * Base Application context of the app
 */
public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppConfiguration.ENABLE_LOG = true;
    }

}

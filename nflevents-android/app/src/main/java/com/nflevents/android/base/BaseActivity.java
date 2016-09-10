package com.nflevents.android.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.nflevents.android.core.http.NflApi;
import com.nflevents.android.core.http.rest.RestManager;

public class BaseActivity extends ActionBarActivity {

    private BaseApp mAppCtx;
    private NflApi nflApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppCtx = (BaseApp) getApplicationContext();
        nflApi = new NflApi();
        nflApi.setUpNflApi(this);
    }

    @Override
    protected void onDestroy() {
        if(nflApi != null) {
            nflApi.stopApiService();
        }
        super.onDestroy();
    }

    /** Get the Application context */
    public BaseApp getApp() {
        return mAppCtx;
    }

    /** Get the RestManager for performing API or http calls. */
    public RestManager getRestManager() {
        return nflApi.getRestManager();
    }

}

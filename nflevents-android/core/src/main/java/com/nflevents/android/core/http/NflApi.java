package com.nflevents.android.core.http;

import android.content.Context;

import com.nflevents.android.core.http.rest.RestManager;
import com.nflevents.android.core.http.rest.RestService;
import com.octo.android.robospice.SpiceManager;

public class NflApi {

    private final String TAG = NflApi.class.getSimpleName();

    private SpiceManager mSpiceManager = new SpiceManager(RestService.class);

    private RestManager mRestManager;

    /** Get the default SpiceManager */
    public SpiceManager getSpiceManager() {
        return this.mSpiceManager;
    }

    /** Get the default RestManager */
    public RestManager getRestManager() {
        return this.mRestManager;
    }

    /**
     * Prepare RestManager of http calls. This should be called
     * before any usage or http call using the resgMAnager.
     * @param ctx The activity or application context.
     */
    public void setUpNflApi(Context ctx) {
        mSpiceManager.start(ctx);
        mRestManager = new RestManager(ctx, mSpiceManager);
    }

    /**
     * Stop any pending or current http request being made to avoid
     * memory leaks or avoid view errors thrown on request response.
     * This should be called onDestroy of Activity using HarleyAndroidApiService
     * right before super.onDestroy();
     */
    public void stopApiService() {
        mSpiceManager.cancelAllRequests();
        mSpiceManager.dontNotifyAnyRequestListeners();
        mSpiceManager.shouldStop();
    }
}

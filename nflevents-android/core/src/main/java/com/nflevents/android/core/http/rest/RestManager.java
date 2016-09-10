package com.nflevents.android.core.http.rest;

import android.content.Context;

import com.nflevents.android.core.http.ApiServiceType;
import com.nflevents.android.core.http.params.HttpParams;
import com.octo.android.robospice.SpiceManager;

/** Manage http request execution. */
public class RestManager {

    private Context ctx;
    private SpiceManager spiceManager;

    private RestCallback restCallback;

    /**
     * RestManager constructor.
     * @param ctx The Activity Context.
     * @param spiceManager The SpiceManager for RestManger to use.
     */
    public RestManager(Context ctx, SpiceManager spiceManager) {
        this.ctx = ctx;
        this.spiceManager = spiceManager;
    }

    /** Get the SpiceManager */
    public SpiceManager getSpiceManager() {
        return this.spiceManager;
    }

    /** Set the RequestCallback of the http request. */
    public void setRestCallback(RestCallback callback) {
        this.restCallback = callback;
    }

    /**
     * Create instance of RestSpiceRequest with default retry policy.
     * @param apiServiceType The {@link com.nflevents.android.core.http.ApiServiceType} of the request.
     * @param restCallback The {@link RestCallback} to be used creating RestSpiceRequestListener,
     *                     this will be the callback of the http request.
     * @param httpParams The HttpParams which will contain the needed data performing the  HttpRequest.
     * @return instance of {@link RestRequest}
     */
    public RestRequest createRequest(ApiServiceType apiServiceType, RestCallback restCallback,
                                     HttpParams httpParams) {
        setRestCallback(restCallback);
        RestRequest restSpiceRequest = new RestRequest(ctx, apiServiceType, restCallback, httpParams);
        return restSpiceRequest;
    }

    /**
     * Execute the http request base on the TransactionType passed. Before calling this, createRequest()
     * must be called first to instantiate {@link RestRequestListener}
     * and {@link RestRequest} which will be used by this RestManager
     * to performing the HttpRequest.
     * @param restSpiceRequest The RestRequest to execute.
     */
    public void performRequest(RestRequest restSpiceRequest) {
        restCallback.onRequestStart();
        RestRequestListener requestListener = new RestRequestListener(restSpiceRequest, restCallback);
        spiceManager.execute(restSpiceRequest, requestListener);
    }

    /**
     * Cancel specific running RestSpiceRequest.
     * @param restSpiceRequest The RestSpiceRequest to be cancelled by SpiceManager.
     */
    public void cancelRequest(RestRequest restSpiceRequest) {
        if(spiceManager != null && restSpiceRequest != null) {
            if(restCallback != null) {
                restCallback.onRequestCancelled();
            }
            spiceManager.cancel(restSpiceRequest);
            spiceManager.dontNotifyRequestListenersForRequest(restSpiceRequest);
        }
    }

    /** Cancel all running or pending http request */
    public void cancelAllRequests() {
        if(spiceManager != null) {
            spiceManager.dontNotifyAnyRequestListeners();
            spiceManager.cancelAllRequests();
        }
    }

    /** Unbind SpiceService */
    public void unbindSpiceService() {
        if(spiceManager != null) {
            spiceManager.shouldStop();
        }
    }
}

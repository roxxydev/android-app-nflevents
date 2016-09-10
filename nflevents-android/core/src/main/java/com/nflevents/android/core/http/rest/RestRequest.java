package com.nflevents.android.core.http.rest;

import android.content.Context;

import com.nflevents.android.core.http.*;
import com.nflevents.android.core.http.params.HttpParams;
import com.nflevents.android.core.utils.LogMe;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.retry.RetryPolicy;

public class RestRequest extends SpiceRequest<RestResponse> {

    private final String TAG = RestRequest.class.getSimpleName();

    private Context mCtx;
    private ApiServiceType apiServiceType;
    private HttpRequest httpRequest;
    private RestCallback restCallback;
    private HttpParams httpParams;
    private int timeout = HttpRequest.TIMEOUT;

    public RestRequest(Context context,
                       ApiServiceType apiServiceType,
                       RestCallback restCallback,
                       HttpParams httpParams) {
        super(RestResponse.class);
        this.mCtx = context;
        this.apiServiceType = apiServiceType;
        this.restCallback = restCallback;
        this.httpParams = httpParams;
        this.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getRetryCount() {
                return 0;
            }

            @Override
            public void retry(SpiceException e) {}

            @Override
            public long getDelayBeforeRetry() {
                return 0;
            }
        });
    }

    /**
     * Set the timeout for the http request. This must be called before RestManager
     * calls performRequest which executes this RestRequest.
     * @param timeout The timeout if it is greater than 10 seconds.
     *                Timeout should not be less than 10 seconds.
     */
    public void setRequestTimeout(int timeout) {
        if(timeout > this.timeout) {
            this.timeout = timeout;
        }
    }

    /**
     * Get the timeout of http request in millisecond. Default is 10000ms.
     * @return The millisecond timeout value of http request which will be used for retry policy also.
     */
    public int getRequestTimeout() {
        return this.timeout;
    }

    /**
     * Get the HttpRequest object to be able to get the RestResponse onFailure.
     * @return The HttpRequest object that have the RestResponse.
     */
    public HttpRequest getHttpRequest() {
        return this.httpRequest;
    }

    @Override
    public RestResponse loadDataFromNetwork() {
        LogMe.d(TAG, "loadDataFromNetwork called");
        httpRequest = new HttpRequest(apiServiceType);
        httpRequest.setRequestTimeout(timeout);
        httpRequest.executeHttpRequest(httpParams);
        return httpRequest.getRestResponse();
    }

    @Override
    protected void publishProgress() {
        super.publishProgress();
        restCallback.onRequestProgress();
    }

    @Override
    protected void publishProgress(float progress) {
        super.publishProgress(progress);
        restCallback.onRequestProgress(progress);
    }
}

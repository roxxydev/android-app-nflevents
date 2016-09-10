package com.nflevents.android.core.http.rest;

public abstract class RestCallback {

    /** Event when http request start */
    public void onRequestStart() {};

    /** Event when http request cancelled. */
    public void onRequestCancelled() {};

    /** Event when http request failed. */
    public void onRequestFailure(RestResponse restResponse) {};

    /** Event when http request is in progress. */
    public void onRequestProgress() {};

    /** Event when http request is in progress with percentage. */
    public void onRequestProgress(float progress) {};

    /** Event when http request has response */
    public void onRequestResponse(RestResponse restResponse) {};
}

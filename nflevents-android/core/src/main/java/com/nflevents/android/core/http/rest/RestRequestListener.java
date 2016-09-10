package com.nflevents.android.core.http.rest;

import com.nflevents.android.core.http.ApiStatusCode;
import com.nflevents.android.core.http.HttpResponse;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class RestRequestListener implements RequestListener<RestResponse> {

    private RestRequest restRequest;
    private RestCallback restCallback;

    public RestRequestListener(RestRequest restRequest, RestCallback restCallback) {
        this.restRequest = restRequest;
        this.restCallback = restCallback;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        if(restRequest.getHttpRequest() != null &&
                restRequest.getHttpRequest().getRestResponse() != null) {
            restCallback.onRequestFailure(restRequest.getHttpRequest().getRestResponse());
        } else {
            restCallback.onRequestFailure(new HttpResponse(ApiStatusCode.SOCKET_CONNECTION_TIMEOUT));
        }
    }

    @Override
    public void onRequestSuccess(RestResponse httpResponse) {
        restCallback.onRequestResponse(httpResponse);
    }
}
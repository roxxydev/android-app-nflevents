package com.nflevents.android.core.http;

import com.nflevents.android.core.http.rest.RestResponse;
import com.nflevents.android.core.json.JsonFieldName;
import com.nflevents.android.core.utils.LogMe;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.client.Response;

/**
 * Holds the http status code of all http request called and contains the
 * API Server Status Message if the response codeMessage of http request has the
 * JSON format of it.
 */
public class HttpResponse implements RestResponse {

    private final String TAG = HttpResponse.class.getSimpleName();

    private int code;
    private String codeName;
    private String codeMessage;
    private String responseBody;
    private boolean isReqSuccess = true;

    // Generic message to be thrown to HttpResponse for unexpected error or connection timeout.
    private final String MSG_NETWORK_TIMEOUT = "Could not connect to server. Please check your connection.";
    private final String MSG_UNEXPECTED_ERROR = "Oops! Something went wrong. Please try again after a few minutes.";

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getCodeName() {
        return this.codeName;
    }

    @Override
    public void setCodeName(String name) {
        this.codeName = name;
    }

    @Override
    public String getCodeMessage() {
        return codeMessage;
    }

    @Override
    public void setCodeMessage(String codeMessage) {
        this.codeMessage = codeMessage;
    }

    @Override
    public String getResponseBody() {
        return this.responseBody;
    }

    @Override
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public boolean isReqSuccess() {
        return isReqSuccess;
    }

    @Override
    public void setReqSuccess(boolean isReqSuccess) {
        this.isReqSuccess = isReqSuccess;
    }

    @Override
    public void initHttpResponse(Response response) {
        if(response != null) {
            boolean hasCatchResponseCode = false;
            code = response.getStatus();
            for(ApiStatusCode apiStatusCode : ApiStatusCode.values()) {
                if(code == apiStatusCode.getCode()) {
                    isReqSuccess = apiStatusCode.isSuccess();
                    hasCatchResponseCode = true;
                }
            }
            if(!hasCatchResponseCode) {
                LogMe.w(TAG, "initHttpResponse hasCatchResponseCode: " + hasCatchResponseCode);
                // didn't recognize response or response is unexpected
                ApiStatusCode genericError = ApiStatusCode.UNEXPECTED_ERROR;
                code = genericError.getCode();
                codeMessage = MSG_UNEXPECTED_ERROR;
                isReqSuccess = false;
            }
        }
    }

    public HttpResponse() {
        // allow empty initialization
    }

    /**
     * Initialize HttpStatusCode from the reponse of the http request
     * @param response Response obtained from the request reponse
     * @param httpResponseBody The String value of the response of the request.
     *         This value will contain the API Server Status Message if it is the
     *         returned codeMessage of the request
     */
    public HttpResponse(Response response, String httpResponseBody) {
        this.responseBody = httpResponseBody;
        initHttpResponse(response);
        // initialize codeMessage value
        if(code == ApiStatusCode.SOCKET_CONNECTION_TIMEOUT.getCode()) {
            this.codeMessage = MSG_NETWORK_TIMEOUT;
        } else if(code == ApiStatusCode.INTERNAL_SERVER_ERROR.getCode() ||
                code == ApiStatusCode.UNEXPECTED_ERROR.getCode()) {
            this.codeMessage = MSG_UNEXPECTED_ERROR;
        } else if(httpResponseBody.contains(JsonFieldName.STATUS_MESSAGE) &&
                httpResponseBody.startsWith("{") && httpResponseBody.endsWith("}")) {
            try {
                JSONObject jsonObj = new JSONObject(httpResponseBody);
                this.codeMessage = jsonObj.getString(JsonFieldName.STATUS_MESSAGE);
            } catch (JSONException e) {
                LogMe.w(TAG, "HttpResponse could not extract 'message' JSON field value.");
            }
        }
    }

    /**
     * This constructor is used to initialize HttpResponse
     * for network issue and unexpected error only.
     */
    public HttpResponse(ApiStatusCode apiStatusCode) {
        code = apiStatusCode.getCode();
        codeName = apiStatusCode.getCodeName();
        if(apiStatusCode.equals(ApiStatusCode.SOCKET_CONNECTION_TIMEOUT)) {
            isReqSuccess = false;
            codeMessage = MSG_NETWORK_TIMEOUT;
        } else if(apiStatusCode.equals(ApiStatusCode.UNEXPECTED_ERROR) ||
                apiStatusCode.equals(ApiStatusCode.INTERNAL_SERVER_ERROR)) {
            isReqSuccess = false;
            codeMessage = MSG_UNEXPECTED_ERROR;
        }
    }

    @Override
    public String toString() {
        return getCodeMessage();
    }
}

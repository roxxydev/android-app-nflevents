package com.nflevents.android.core.http;

import com.nflevents.android.core.AppConfiguration;
import com.nflevents.android.core.http.params.HttpParams;
import com.nflevents.android.core.http.rest.RestResponse;
import com.nflevents.android.core.utils.LogMe;
import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.client.UrlConnectionClient;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Class that execute calls of http request to the API Server.
 * This class can be replaced with other REST Client library(i.e. Retrofit).
 */
public class HttpRequest {

    private static final String TAG = HttpRequest.class.getSimpleName();

    public static int TIMEOUT = 10000;// default timeout

    private ApiServiceType apiServiceType;
    private RestResponse restResponse;

    private RestAdapter.Builder retrofitRestBuilder;
    private HttpErrorHandler httpErrorHandler;
    private OkHttpClient httpClient;

    public HttpRequest(ApiServiceType apiApiServiceType) {
        this.apiServiceType = apiApiServiceType;
        httpErrorHandler = new HttpErrorHandler();
        httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        httpClient.setReadTimeout(30, TimeUnit.SECONDS);
        httpClient.setWriteTimeout(30, TimeUnit.SECONDS);
        retrofitRestBuilder = new RestAdapter.Builder()
                .setClient(new OkClient(httpClient))
                .setEndpoint(AppConfiguration.HOST)
                .setErrorHandler(httpErrorHandler);
        if(AppConfiguration.ENABLE_LOG) {
            retrofitRestBuilder.setLogLevel(RestAdapter.LogLevel.FULL);
        } else {
            retrofitRestBuilder.setLogLevel(RestAdapter.LogLevel.NONE);
        }
    }

    /**
     * Set the timeout of http request in millisecond. By default the timeout is 10000ms.
     * @param requestTimeout Millisecond value of timeout of http request.
     */
    public void setRequestTimeout(int requestTimeout) {
        if (requestTimeout > TIMEOUT) {
            TIMEOUT = requestTimeout;
            retrofitRestBuilder.setClient(new HttpConnectionClient(TIMEOUT));
        }
    }

    /**
     * Get the {@link com.nflevents.android.core.http.rest.RestResponse} of the
     * http request made.
     */
    public RestResponse getRestResponse() {
        return restResponse;
    }

    /**
     * Initialize and log HttpResponse of the http request response.
     * @param response The Response object of http request.
     * @param body The body of the http request response.
     */
    protected void initRestResponse(Response response, String body) {
        if(response != null) {
            restResponse = new HttpResponse(response, body);
        } else {
            LogMe.w(TAG, "initRestResponse response null: " + response + " body: " + body);
        }
    }

    /**
     * Execute specific request based on the
     * {@link ApiServiceType}
     * set in the instance of this HttpRequest.
     * @param httpParams HttpRequestParams object containing the needed data in http request.
     */
    public void executeHttpRequest(HttpParams httpParams) {
        HeaderRequestInterceptor header = new HeaderRequestInterceptor();
        Response response = null;
        String responseBody = "(empty)";
        if (apiServiceType == ApiServiceType.GET_VENUE_LIST) {
            retrofitRestBuilder.setRequestInterceptor(header);
            ApiService apiService = retrofitRestBuilder.build().create(ApiService.class);
            response = apiService.getNflVenues();
        }
        try {
            if(response != null) {
                responseBody = getBodyString(response);
                initRestResponse(response, responseBody);
            }
        } catch(IOException e) {
            initRestResponse(null, "getBodyString(response) error in executeHttpRequest method");
            LogMe.d(TAG, "status: " + response.getStatus() + " body: " + responseBody);
        }
    }

    // to convert byte stream of Response body of request
    public static String getBodyString(Response response) throws IOException {
        TypedInput body = response.getBody();
        if (body!= null) {
            if (!(body instanceof TypedByteArray)) {
                // Read the entire response body to we can log it and replace the original response
                response = readBodyToBytesIfNecessary(response);
                body = response.getBody();
            }
            byte[] bodyBytes = ((TypedByteArray) body).getBytes();
            String bodyMime = body.mimeType();
            String bodyCharset = MimeUtil.parseCharset(bodyMime, Charset.defaultCharset().name());
            return new String(bodyBytes, bodyCharset);
        }
        return null;
    }

    static Response readBodyToBytesIfNecessary (Response response) throws IOException {
        TypedInput body = response.getBody();
        if (body == null || body instanceof TypedByteArray) {
            return response;
        }
        String bodyMime = body.mimeType();
        byte[] bodyBytes = streamToBytes(body.in());
        body = new TypedByteArray(bodyMime, bodyBytes);

        return replaceResponseBody(response, body);
    }

    static Response replaceResponseBody(Response response, TypedInput body) {
        return new Response(response.getUrl(), response.getStatus(),
                response.getReason(), response.getHeaders(), body);
    }

    static byte[] streamToBytes(InputStream stream) throws IOException {
        int BUFFER_SIZE = 0x1000;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (stream != null) {
            byte[] buf = new byte[BUFFER_SIZE];
            int r;
            while ((r = stream.read(buf)) != -1) {
                baos.write(buf, 0, r);
            }
        }
        return baos.toByteArray();
    }

    // add header to http request
    private class HeaderRequestInterceptor implements RequestInterceptor {
        private HashMap<String, String> headers = new HashMap<>();

        /** Add key and value to header. */
        public void addHeader(String key, String value) {
            headers.put(key, value);
        }

        @Override
        public void intercept(RequestFacade request) {
            for(Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    // custom UrlConnectionClient for Retrofit client to be able to set timeout
    private class HttpConnectionClient extends UrlConnectionClient {

        private int timeoutMs = 30000;

        public HttpConnectionClient(int timeoutMs) {
            this.timeoutMs = timeoutMs;
        }

        @Override
        protected HttpURLConnection openConnection(Request request) throws IOException {
            HttpURLConnection connection = super.openConnection(request);
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);
            return connection;
        }
    }

    /*
     * Set error handler of retrofit http client. For more details see
     * http://blog.robinchutaux.com/blog/a-smart-way-to-use-retrofit/ and
     * http://square.github.io/retrofit/
     */
    private class HttpErrorHandler implements ErrorHandler {

        @Override
        public Throwable handleError(RetrofitError cause) {
            Response r = cause.getResponse();
            if(r != null) {
                if(cause.getKind().equals(RetrofitError.Kind.NETWORK)) {
                    restResponse = new HttpResponse(ApiStatusCode.SOCKET_CONNECTION_TIMEOUT);
                } else if(cause.getKind().equals(RetrofitError.Kind.HTTP)) {
                    try {
                        String body = getBodyString(r);
                        LogMe.d(TAG, "handleError status: " + r.getStatus() + " body: " + body);
                        initRestResponse(r, body);
                    } catch (Exception e) {
                        LogMe.d(TAG, "handleError initRestResponse ERROR " + e.toString());
                    }
                } else {
                    restResponse = new HttpResponse(ApiStatusCode.UNEXPECTED_ERROR);
                }
            } else {
                /*
                 * No need to implement server side change, this workaround for 401
                 * http status code could not be extracted in some device
                 * http://stackoverflow.com/questions/10431202/
                 * java-io-ioexception-received-authentication-challenge-is-null
                 */
                if(cause != null && cause.getMessage() != null
                        && cause.getMessage().contains("authentication challenge is null")) {
                    LogMe.w(TAG, "handleError Response message: " + cause.getMessage());
                    restResponse = new HttpResponse(ApiStatusCode.UNAUTHORIZED);
                }
                LogMe.e(TAG, "handleError Response is null");
            }
            return cause;
        }
    }

}

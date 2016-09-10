package com.nflevents.android.core.http;


import retrofit.client.Response;
import retrofit.http.GET;

public interface ApiService {

    // TODO temporarily set the JSON source file as the endpoint
    @GET("/nflapi-static.json")
    public Response getNflVenues();

}
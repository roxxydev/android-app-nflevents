package com.nflevents.android.core;

/**
 * Application configuration. Set the host url of the API Server,
 * Google API Project Number, and Log enabling.
 */
public class AppConfiguration {

    // Default configuration values, this can be replaced by the app module using this core library

    // TODO temporary set the URL of the JSON of Venue list
    public static String HOST = "https://s3.amazonaws.com/jon-hancock-phunware";

    public final static String HOST_VERSION = "v1";
    public final static String HOST_API_ROOT_URI = "/api/";

    public static String GOOGLE_API_PROJ_NUMBER = "your google project api number for maps, gcm";
    public static boolean ENABLE_LOG = true;

}

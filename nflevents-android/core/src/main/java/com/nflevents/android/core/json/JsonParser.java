package com.nflevents.android.core.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nflevents.android.core.entity.venue.Venue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser of JSON and Entity classes.
 */
public class JsonParser {

    private static Gson getDefaultGson() {
        return new GsonBuilder().create();
    }

    /**
     * Parse JSON String value to Venue object.
     * @param jsonString The JSON String value to parse.
     * @return Object of Venue of parsed JSON String value.
     */
    public static Venue toVenue(String jsonString) {
        return getDefaultGson().fromJson(jsonString, Venue.class);
    }

    /**
     * Parse Venue object instance to JSON String value.
     * @param venue The Venue object instance to parse.
     * @return The JSON String value of parsed Venue object.
     */
    public static String fromVenue(Venue venue) {
        return getDefaultGson().toJson(venue, Venue.class);
    }

    /**
     * Parse JSON String value to List of Venue.
     * @param jsonString The JSON String value to parse.
     * @return Object List of Venue of parsed JSON String value.
     */
    public static ArrayList<Venue> toVenueList(String jsonString) {
        Type listType = new TypeToken<List<Venue>>() {}.getType();
        ArrayList<Venue> arrVenueList = getDefaultGson().fromJson(jsonString, listType);
        return arrVenueList;
    }

}

package com.nflevents.android.core.entity.venue;

import com.google.gson.annotations.SerializedName;
import com.nflevents.android.core.entity.venue.schedule.Schedule;
import com.nflevents.android.core.json.JsonFieldName;

import java.util.List;

public class Venue {

    @SerializedName(JsonFieldName.VENUE_ID)
    private int id;

    @SerializedName(JsonFieldName.VENUE_NAME)
    private String name;

    @SerializedName(JsonFieldName.VENUE_ADDRESS)
    private String address;

    @SerializedName(JsonFieldName.VENUE_CITY)
    private String city;

    @SerializedName(JsonFieldName.VENUE_STATE)
    private String state;

    @SerializedName(JsonFieldName.VENUE_ZIP)
    private String zip;

    @SerializedName(JsonFieldName.VENUE_PCODE)
    private String pCode;

    @SerializedName(JsonFieldName.VENUE_PHONE)
    private String phone;

    @SerializedName(JsonFieldName.VENUE_TOLLFREEPHONE)
    private String tollFreePhone;

    @SerializedName(JsonFieldName.VENUE_TICKET_LINK)
    private String ticketLink;

    @SerializedName(JsonFieldName.VENUE_IMAGE_URL)
    private String imageUrl;

    @SerializedName(JsonFieldName.VENUE_DESCRIPTION)
    private String description;

    @SerializedName(JsonFieldName.VENUE_LONGITUDE)
    private float longitude;

    @SerializedName(JsonFieldName.VENUE_LATITUDE)
    private float latitude;

    @SerializedName(JsonFieldName.VENUE_SCHEDULE)
    private List<Schedule> schedule;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getpCode() {
        return pCode;
    }

    public String getPhone() {
        return phone;
    }

    public String getTollFreePhone() {
        return tollFreePhone;
    }

    public String getTicketLink() {
        return ticketLink;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public List<Schedule> getSchedule() {
        return schedule;
    }
}

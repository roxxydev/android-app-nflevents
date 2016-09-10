package com.nflevents.android.core.entity.venue.schedule;

import com.google.gson.annotations.SerializedName;
import com.nflevents.android.core.json.JsonFieldName;

public class Schedule {

    @SerializedName(JsonFieldName.VENUE_STARTDATE)
    private String startDate;

    @SerializedName(JsonFieldName.VENUE_ENDDATE)
    private String endDate;

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}

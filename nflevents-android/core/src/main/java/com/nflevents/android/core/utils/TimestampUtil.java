package com.nflevents.android.core.utils;

import com.nflevents.android.core.entity.venue.schedule.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Timestamp converter to human readable date.
 */
public class TimestampUtil {

    private static final String TAG = TimestampUtil.class.getSimpleName();

    private static final String SERVER_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss zzzz";

    /**
     * Convert the passe Venue date in format of yyyy-MM-dd HH:mm:ss XXX
     * to display date.
     * Ex. 2015-04-16 12:00:00 -0800 to Monday
     */
    public static String toDisplayDate(Schedule venueSched) {
        StringBuilder sb = new StringBuilder();
        try {
            if(FieldValidator.isStringValid(venueSched.getStartDate()) &&
                    FieldValidator.isStringValid(venueSched.getEndDate())) {
                long convertedTimezoneStateDateTs = convertToOtherTimezone(
                        new SimpleDateFormat(SERVER_DATE_FORMAT)
                                .parse(venueSched.getStartDate()),
                        TimeZone.getTimeZone("GMT+0"),
                        TimeZone.getDefault());

                long convertedTimezoneEndDateTs = convertToOtherTimezone(
                        new SimpleDateFormat(SERVER_DATE_FORMAT)
                                .parse(venueSched.getStartDate()),
                        TimeZone.getTimeZone("GMT+0"),
                        TimeZone.getDefault());

                Calendar calStart = Calendar.getInstance();
                calStart.setTimeInMillis(convertedTimezoneStateDateTs);
                Calendar calEnd = Calendar.getInstance();
                calEnd.setTimeInMillis(convertedTimezoneEndDateTs);

                String day = getDayToString(calStart.get(Calendar.DAY_OF_WEEK));
                String month = String.valueOf(calStart.get(Calendar.MONTH) + 1);
                String dayOfMonth = String.valueOf(calStart.get(Calendar.DAY_OF_MONTH));
                String timeStart = calStart.get(Calendar.HOUR) + ":"
                        + getMinute(calStart.get(Calendar.MINUTE))
                        + getAmPm(calStart.get(Calendar.AM_PM));
                String timeEnd = calEnd.get(Calendar.HOUR) + ":"
                        + getMinute(calEnd.get(Calendar.MINUTE))
                        + getAmPm(calEnd.get(Calendar.AM_PM));
                sb.append(day + " " + month + "/" + dayOfMonth + " " + timeStart + " to " + timeEnd);
            }
        } catch(ParseException e) {
            LogMe.e(TAG, "toDisplayDate ERROR: " + e.toString());
        }
        return sb.toString();
    }

    /** Convert Date instance to other TimeZone. */
    public static long convertToOtherTimezone(Date date, TimeZone fromTZ, TimeZone toTZ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.setTimeZone(fromTZ);
        calendar.add(Calendar.MILLISECOND, fromTZ.getRawOffset() * -1);
        if (fromTZ.inDaylightTime(calendar.getTime())) {
            calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
        }
        calendar.add(Calendar.MILLISECOND, toTZ.getRawOffset());
        if (toTZ.inDaylightTime(calendar.getTime())) {
            calendar.add(Calendar.MILLISECOND, toTZ.getDSTSavings());
        }
        return calendar.getTimeInMillis();
    }

    /** Get day of week in word from Calendar value. */
    public static String getDayToString(int day) {
        String dayOfWeek = "Sunday";
        if(day == 1)
            dayOfWeek = "Sunday";
        else if (day == 2)
            dayOfWeek = "Monday";
        else if (day == 3)
            dayOfWeek = "Tuesday";
        else if (day == 4)
            dayOfWeek = "Wednesday";
        else if (day == 5)
            dayOfWeek = "Thursday";
        else if (day == 6)
            dayOfWeek = "Friday";
        else if (day == 7)
            dayOfWeek = "Saturday";
        return dayOfWeek;
    }

    /** Get minutes in two decimal place from Calendar value. */
    public static String getMinute(int minute) {
        String mins = String.valueOf(minute);
        if(mins.length() < 2) {
            mins = "0" + mins;
        }
        return mins;
    }

    /** Get am or pm in word from Calendar value. */
    public static String getAmPm(int amPm) {
        if(amPm == Calendar.AM) {
            return "am";
        } else {
            return "pm";
        }
    }

}

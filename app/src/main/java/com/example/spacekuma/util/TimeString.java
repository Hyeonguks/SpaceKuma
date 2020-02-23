package com.example.spacekuma.util;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeString {


    /*
    참고 사이트
    https://stackoverflow.com/questions/25174921/time-ago-for-android-java
    https://stackoverflow.com/questions/42115368/android-is-there-a-way-to-show-min-instead-of-minute-with-dateutils
    https://stackoverflow.com/questions/18607096/getting-time-difference-with-string-like-a-minute-ago-or-an-hour-ago-on-andr
     */
    public static String formatTimeString(String regTime) {
        String ago = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(regTime);
            Long Uploaded_Date = date.getTime();

            ago = DateUtils.getRelativeTimeSpanString(Uploaded_Date, System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS).toString();
//            Log.e("Hey_What","so_What : "+DateUtils.getRelativeTimeSpanString(Uploaded_Date, System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ago;
    }
}

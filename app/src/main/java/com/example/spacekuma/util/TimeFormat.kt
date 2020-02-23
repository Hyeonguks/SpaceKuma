package com.example.spacekuma.util

import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat

class TimeFormat {

    companion object{
        fun returnTime (date : String) : String {
            var ago = ""
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            try {
                val date = df.parse(date)
                val Uploaded_Date = date!!.time

                ago = DateUtils.getRelativeTimeSpanString(Uploaded_Date,System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS).toString()
                //            Log.e("Hey_What","so_What : "+DateUtils.getRelativeTimeSpanString(Uploaded_Date, System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS));
            } catch (e: ParseException) {
                e.printStackTrace()
            }


            return ago
        }
    }
}
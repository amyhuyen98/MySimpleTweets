package com.codepath.apps.restclienttemplate;

import android.text.format.DateUtils;

import com.codepath.apps.restclienttemplate.models.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Parcel
public class Tweet {

    // list out the attributes
    public String body;
    public long uid; // database ID for the tweet
    public User user;
    public String createdAt;
    public String relativeDate;
    public String tweetId;
    public long retweetNum;
    public long faveNum;
    public String date;
    public String time;

    public Tweet(){}

    // deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException{
        Tweet tweet = new Tweet();

        // extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.tweetId = jsonObject.getString("id_str");
        tweet.retweetNum = jsonObject.getLong("retweet_count");
        tweet.faveNum = jsonObject.getLong("favorite_count");

        // extract rawJsonDate value from JSON and convert to relativeDate
        String rawJsonDate = jsonObject.getString("created_at");
        tweet.relativeDate = getRelativeTimeAgo(rawJsonDate);

        // get date and time
        tweet.date = getDate(rawJsonDate);
        tweet.time = getTime(rawJsonDate);

        // get entities array

        return tweet;

    }

    // method that changes the rawJsonDate from Twitter API into relative terms
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    // method that changes the rawJsondate from Twitter API into date
    public static String getDate (String rawJsonDate) {
        String date="• ";
        String[] array = rawJsonDate.split(" ");
        for (int i=2; i>=1; i--){
            date += (array[i] + " ");
        }
        date += array[array.length-1];
        return date;
    }

    // method that changes the rawJsondate from Twitter API into time (+17 to correct for time zone differences)
    public static String getTime (String rawJsonDate){
        String time="";
        String[] array = rawJsonDate.split(" ");
        String temp = array[3];
        String[] timeArray = temp.split(":");
        time += Integer.toString((Integer.parseInt(timeArray[0])+17)%12) + ":";
        time += timeArray[1];
        if ((Integer.parseInt(timeArray[0])+17)>= 12){time += " PM";}
        else {time += " AM";}
        return time;
    }
}

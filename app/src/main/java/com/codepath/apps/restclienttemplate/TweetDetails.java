package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.GlideApp;

import org.parceler.Parcels;

public class TweetDetails extends AppCompatActivity {

    ImageView ivProfileImage;
    TextView tvUserName;
    TextView tvHandle;
    TextView tvBody;
    TextView tvTime;
    TextView tvDate;
    Tweet tweet;
    TextView tvRetweetNum;
    TextView tvFaveNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvUserName = findViewById(R.id.tvUserName);
        tvHandle = findViewById(R.id.tvHandle);
        tvBody = findViewById(R.id.tvBody);
        tvTime = findViewById(R.id.tvTime);
        tvDate = findViewById(R.id.tvDate);
        tvRetweetNum = findViewById(R.id.tvRetweetNum);
        tvFaveNum= findViewById(R.id.tvFaveNum);

        // unwrap the tweet from the intent
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        Log.d("TweetDetails", "Showing details for tweet");
        tvUserName.setText(tweet.user.name);
        tvHandle.setText("@" + tweet.user.screenName);
        tvBody.setText(tweet.body);
        tvTime.setText(tweet.time);
        tvDate.setText(tweet.date);
        tvRetweetNum.setText(Long.toString(tweet.retweetNum));
        tvFaveNum.setText(Long.toString(tweet.faveNum));

        GlideApp.with(this)
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.ic_vector_person)
                .transform(new CircleCrop())
                .into(ivProfileImage);
    }
}

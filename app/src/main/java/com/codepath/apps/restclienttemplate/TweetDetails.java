package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.GlideApp;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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
    ImageView ivReply;
    ImageView ivRetweet;
    ImageView ivFavorite;
    TwitterClient client;
    ImageView ivMedia;

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
        ivReply = findViewById(R.id.ivReply);
        ivRetweet = findViewById(R.id.ivRetweet);
        ivFavorite = findViewById(R.id.ivFavorite);
        ivMedia = findViewById(R.id.ivMedia);

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

        // create a new client
        client = TwitterApp.getRestClient(this);


        // check if tweet has already been retweeted or favorited (from the details screen)
        if (tweet.favorited){ivFavorite.setSelected(true);}
        if (tweet.retweeted){ivRetweet.setSelected(true);}

        // favoriting tweets
        ivFavorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("Tag", "Favorite Tapped");
                ivFavorite.setSelected(true);
                client.favorite(tweet.tweetId, new JsonHttpResponseHandler(){
                    @Override public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                        tvFaveNum.setText(Integer.toString(Integer.parseInt(tvFaveNum.getText().toString())+1));
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){
                        Log.d("Twitter Client", responseString);
                        throwable.printStackTrace();
                    }
                });
            }
        });

        // retweeeting tweets
        ivRetweet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("Tag", "Retweet Tapped");
                ivRetweet.setSelected(true);
                client.retweet(tweet.tweetId, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        tvRetweetNum.setText(Integer.toString(Integer.parseInt(tvRetweetNum.getText().toString())+1));
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("TwitterClient", responseString);
                        throwable.printStackTrace();
                    }
                });
            }
        });

        GlideApp.with(this)
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.ic_vector_person)
                .transform(new CircleCrop())
                .into(ivProfileImage);

        // check if there is media and get media
        if (tweet.embedUrl != null){
            ivMedia.setVisibility(View.VISIBLE);
            GlideApp.with(this)
                    .load(tweet.embedUrl)
                    .centerInside()
                    .transform(new RoundedCornersTransformation( 15, 0))
                    .into(ivMedia);
        }
        else{
            ivMedia.setVisibility(View.GONE);
        }
    }

    public void onClose(View view){
        // prepare new intent to return to timeline activity
        Intent intent = new Intent(this, TimelineActivity.class);
        startActivity(intent);
    }
}

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

import butterknife.BindView;
import butterknife.ButterKnife;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetails extends AppCompatActivity {

    // the view objects
    @BindView (R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView (R.id.tvUserName) TextView tvUserName;
    @BindView (R.id.tvHandle) TextView tvHandle;
    @BindView (R.id.tvBody) TextView tvBody;
    @BindView (R.id.tvTime) TextView tvTime;
    @BindView (R.id.tvDate) TextView tvDate;
    @BindView (R.id.tvRetweetNum) TextView tvRetweetNum;
    @BindView (R.id.tvFaveNum) TextView tvFaveNum;
    @BindView (R.id.ivReply) ImageView ivReply;
    @BindView (R.id.ivRetweet) ImageView ivRetweet;
    @BindView (R.id.ivFavorite) ImageView ivFavorite;
    @BindView (R.id.ivMedia) ImageView ivMedia;

    TwitterClient client;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        // resolve the view objects
        ButterKnife.bind(this);

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
                client.favorite(tweet.tweetId, new JsonHttpResponseHandler(){
                    @Override public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                        ivFavorite.setSelected(true);
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
                client.retweet(tweet.tweetId, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        ivRetweet.setSelected(true);
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

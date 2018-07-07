package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.GlideApp;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    // set request code for replying to someone's tweet
    private final int REPLY_REQUEST_CODE = 30;

    // declare variables
    private List<Tweet> mTweets;
    Context context;
    TwitterClient client;

    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets){
        mTweets = tweets;
        client = TwitterApp.getRestClient(context);

    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // remember: the inflater creates the view in memory
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get the data according to position
        final Tweet tweet = mTweets.get(position);
        // populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvRelativeDate.setText(tweet.relativeDate);
        holder.tvHandle.setText("@" + tweet.user.screenName);
        holder.tvRetweetNum.setText(Long.toString(tweet.retweetNum));
        holder.tvFaveNum.setText(Long.toString(tweet.faveNum));

        // check if there is media and get media
        if (tweet.embedUrl != null){
            holder.ivMedia.setVisibility(View.VISIBLE);
            GlideApp.with(context)
                    .load(tweet.embedUrl)
                    .centerInside()
                    .transform(new RoundedCornersTransformation( 15, 0))
                    .into(holder.ivMedia);
        }
        else{
            holder.ivMedia.setVisibility(View.GONE);
        }

        // set onClickListener for reply button
        holder.ivReply.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.d("Tag", "Reply Tapped");
                Intent intent = new Intent(context, ReplyActivity.class);
                intent.putExtra("originalTweet", Parcels.wrap(tweet));
                ((Activity) context).startActivityForResult(intent, REPLY_REQUEST_CODE);
            }
        });

        // check if tweet has already been retweeted or favorited and change colors to match
        if (tweet.favorited){holder.ivFavorite.setSelected(true);}
        if (tweet.retweeted){holder.ivRetweet.setSelected(true);}

        // set onClickListener for retweet button
        holder.ivRetweet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("Tag", "Retweet Tapped");
                client.retweet(tweet.tweetId, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        holder.ivRetweet.setSelected(true);
                        holder.tvRetweetNum.setText(Integer.toString(Integer.parseInt(holder.tvRetweetNum.getText().toString())+1));
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("TwitterClient", responseString);
                        throwable.printStackTrace();
                    }
                });
            }
        });

        // set onClickListener for favorite button
        holder.ivFavorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("Tag", "Favorite Tapped");
                client.favorite(tweet.tweetId, new JsonHttpResponseHandler(){
                    @Override public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                        holder.ivFavorite.setSelected(true);
                        holder.tvFaveNum.setText(Integer.toString(Integer.parseInt(holder.tvFaveNum.getText().toString())+1));
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){
                        Log.d("Twitter Client", responseString);
                        throwable.printStackTrace();
                    }
                });
            }
        });

        // load user profile images on tweets
        GlideApp.with(context)
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.ic_vector_person)
                .transform(new CircleCrop())
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public @BindView (R.id.ivProfileImage) ImageView ivProfileImage;
        public @BindView (R.id.tvUserName) TextView tvUsername;
        public @BindView (R.id.tvBody) TextView tvBody;
        public @BindView (R.id.tvRelativeDate) TextView tvRelativeDate;
        public @BindView (R.id.tvHandle) TextView tvHandle;
        public @BindView (R.id.ivReply) ImageView ivReply;
        public @BindView (R.id.tvRetweetNum) TextView tvRetweetNum;
        public @BindView (R.id.tvFaveNum) TextView tvFaveNum;
        public @BindView (R.id.ivRetweet) ImageView ivRetweet;
        public @BindView (R.id.ivFavorite) ImageView ivFavorite;
        public @BindView (R.id.ivMedia) ImageView ivMedia;

        public ViewHolder(View itemView){
            super (itemView);
            // resolve the view objects
            ButterKnife.bind(this, itemView);
            // set itemView's onClickListener
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            // get item position
            int position = getAdapterPosition();
            // make sure the position is valid
            if (position != RecyclerView.NO_POSITION){
                // get the tweet at the position
                Tweet tweet = mTweets.get(position);
                Intent intent = new Intent(context, TweetDetails.class);
                // create intent for new activity
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                context.startActivity(intent);
            }

        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }
}

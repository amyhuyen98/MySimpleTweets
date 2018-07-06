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

import cz.msebera.android.httpclient.Header;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    // set request code for replying to someone's tweet
    private final int REPLY_REQUEST_CODE = 30;
    private final int RETWEET_REQUEST_CODE = 40;

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
                holder.ivRetweet.setSelected(true);
                client.retweet(tweet.tweetId, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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
                holder.ivFavorite.setSelected(true);
                client.favorite(tweet.tweetId, new JsonHttpResponseHandler(){
                    @Override public void onSuccess(int statusCode, Header[] headers, JSONObject response){
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
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvRelativeDate;
        public TextView tvHandle;
        public ImageView ivReply;
        public TextView tvRetweetNum;
        public TextView tvFaveNum;
        public ImageView ivRetweet;
        public ImageView ivFavorite;

        public ViewHolder(View itemView){
            super (itemView);

            // perform findViewById lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvRelativeDate = (TextView) itemView.findViewById(R.id.tvRelativeDate);
            tvHandle = (TextView) itemView.findViewById(R.id.tvHandle);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
            tvRetweetNum = (TextView) itemView.findViewById(R.id.tvRetweetNum);
            tvFaveNum = (TextView) itemView.findViewById(R.id.tvFaveNum);
            ivRetweet = (ImageView) itemView.findViewById(R.id.ivRetweet);
            ivFavorite = (ImageView) itemView.findViewById(R.id.ivFavorite);

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
                // create intent for new activity
                Intent intent = new Intent(context, TweetDetails.class);
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

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

import com.codepath.apps.restclienttemplate.models.GlideApp;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    // set request code for replying to someone's tweet
    private final int REQUEST_CODE = 30;

    private List<Tweet> mTweets;
    Context context;
    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets){
        mTweets = tweets;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the data according to position
        final Tweet tweet = mTweets.get(position);
        // populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvRelativeDate.setText(tweet.relativeDate);
        holder.tvHandle.setText("@" + tweet.user.screenName);
        holder.ivReply.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.d("Tag", "Tapped");
                Intent intent = new Intent(context, ReplyActivity.class);
                intent.putExtra("originalTweet", Parcels.wrap(tweet));
                ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
            }
        });

        GlideApp.with(context)
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.ic_vector_person)
                .transform(new RoundedCornersTransformation( 15, 0))
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvRelativeDate;
        public TextView tvHandle;
        public ImageView ivReply;

        public ViewHolder(View itemView){
            super (itemView);

            // perform findViewById lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvRelativeDate = (TextView) itemView.findViewById(R.id.tvRelativeDate);
            tvHandle = (TextView) itemView.findViewById(R.id.tvHandle);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
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

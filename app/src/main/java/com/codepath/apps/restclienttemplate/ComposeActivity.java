package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.GlideApp;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    EditText etText;
    Button btnTweet;
    TwitterClient client;
    TextView tvUserName;
    TextView tvHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApp.getRestClient(this);
        etText = (EditText) findViewById(R.id.etText);
        btnTweet = (Button) findViewById(R.id.btnTweet);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvHandle = (TextView) findViewById(R.id.tvHandle);


        // get user info to load user image
        client.getUserInfo(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                User user = null;
                try {
                    user = User.fromJSON(response);
                    tvHandle.setText("@" + user.screenName);
                    tvUserName.setText(user.name);
                    GlideApp.with(ComposeActivity.this)
                            .load(user.profileImageUrl)
                            .placeholder(R.drawable.ic_vector_person)
                            .into((ImageView) findViewById(R.id.ivProfileImage));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }
        });


    }

    public void onClose(View view){
        // prepare new intent to return to timeline activity
        Intent intent = new Intent(this, TimelineActivity.class);
        startActivity(intent);
    }

    public void onPostTweet(View view){
        client.sendTweet(etText.getText().toString(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet newTweet;
                try {
                     newTweet = Tweet.fromJSON(response);
                    // prepare data intent
                    Intent intent = new Intent();

                    // pass relevant data back as result
                    intent.putExtra("newTweet", Parcels.wrap(newTweet));
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }
        });
    }
}

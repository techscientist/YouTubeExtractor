package com.commit451.youtubeextractor.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.commit451.youtubeextractor.YouTubeExtractor;
import com.commit451.youtubeextractor.YouTubeExtractionResult;
import com.devbrackets.android.exomedia.core.video.scale.ScaleType;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String GRID_YOUTUBE_ID = "9d8wWcJLnFI";

    private static final String STATE_SAVED_POSITION = "saved_position";

    private ImageView mImageView;
    private TextView mDescription;
    private EMVideoView mVideoView;

    private int mSavedPosition;

    private Callback<YouTubeExtractionResult> mExtractionCallback = new Callback<YouTubeExtractionResult>() {
        @Override
        public void onResponse(Call<YouTubeExtractionResult> call, Response<YouTubeExtractionResult> response) {
            Log.d("OnSuccess", "Got a result with the best url: " + response.body().getBestAvailableQualityVideoUri());
            bindVideoResult(response.body());
        }

        @Override
        public void onFailure(Call<YouTubeExtractionResult> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(MainActivity.this, "It failed to extract. So sad", Toast.LENGTH_SHORT).show();
        }
    };

    private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            if (mVideoView == null) {
                return;
            }

            mVideoView.setVolume(0);
            mVideoView.seekTo(mSavedPosition);
            mSavedPosition = 0;
            mVideoView.start();
        }
    };
    private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion() {
            //I dunno, maybe play it again
        }
    };
    private OnErrorListener mOnErrorListener = new OnErrorListener() {
        @Override
        public boolean onError() {
            //cry I guess
            Log.d("ERROR", "There was an error. Oh no!");
            return true;
        }
    };

    private final YouTubeExtractor mExtractor = new YouTubeExtractor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.thumb);
        mVideoView = (EMVideoView) findViewById(R.id.video_view);
        mDescription = (TextView) findViewById(R.id.description);

        mExtractor.getYouTubeVideoData(GRID_YOUTUBE_ID).enqueue(mExtractionCallback);
        if (savedInstanceState != null) {
            mSavedPosition = savedInstanceState.getInt(STATE_SAVED_POSITION, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.setScaleType(ScaleType.CENTER_CROP);
        mVideoView.seekTo(mSavedPosition);
        mVideoView.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SAVED_POSITION, mSavedPosition);
    }

    private void bindVideoResult(YouTubeExtractionResult result) {
        Glide.with(this)
                .load(result.getBestAvailableQualityThumbUri())
                .into(mImageView);
        mVideoView.setScaleType(ScaleType.CENTER_CROP);
        mVideoView.setOnPreparedListener(mOnPreparedListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
        mVideoView.setVideoURI(result.getBestAvailableQualityVideoUri());
    }
}

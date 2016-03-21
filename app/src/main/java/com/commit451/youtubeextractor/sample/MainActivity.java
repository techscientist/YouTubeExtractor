package com.commit451.youtubeextractor.sample;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;

import com.commit451.youtubeextractor.YouTubeExtractor;

public class MainActivity extends AppCompatActivity {

    private static final String GRID_YOUTUBE_ID = "YE7VzlLtp-4";

    private ImageView mImageView;
    private TextureView mTextureView;

    private MediaPlayer mMediaPlayer;

    private YouTubeExtractor.Callback mCallback = new YouTubeExtractor.Callback() {
        @Override
        public void onSuccess(YouTubeExtractor.Result result) {
            bindVideoResult(result);
        }

        @Override
        public void onFailure(Throwable t) {
            t.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image);
        mTextureView = (TextureView) findViewById(R.id.texture_view);

        YouTubeExtractor extractor = new YouTubeExtractor(GRID_YOUTUBE_ID);
        extractor.extract(mCallback);
    }

    private void bindVideoResult(YouTubeExtractor.Result result) {
        try {
            mMediaPlayer= new MediaPlayer();
            mMediaPlayer.setDataSource(this, result.getBestAvaiableQualityVideoUri());
            mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            mMediaPlayer.setSurface(new Surface(mTextureView.getSurfaceTexture()));
            mMediaPlayer.prepare();
            //mMediaPlayer.setOnBufferingUpdateListener(this);
            //mMediaPlayer.setOnCompletionListener(this);
            //mMediaPlayer.setOnPreparedListener(this);
            //mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setVolume(0.0f, 0.0f);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.commit451.youtubeextractor.sample;

import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import com.commit451.youtubeextractor.YouTubeExtractor;

public class MainActivity extends AppCompatActivity {

    private static final String GRID_YOUTUBE_ID = "YE7VzlLtp-4";

    private ImageView mImageView;
    private TextureView mTextureView;

    private MediaPlayer mMediaPlayer;
    private Matrix mMatrix = new Matrix();
    private View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            setTransformMatrix();
        }
    };
    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            setTransformMatrix();
        }
    };
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.seekTo(7000);

        }
    };
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.start();
            mp.seekTo(7000);
        }
    };
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
        mTextureView.addOnLayoutChangeListener(mOnLayoutChangeListener);

        YouTubeExtractor extractor = new YouTubeExtractor(GRID_YOUTUBE_ID);
        extractor.extract(mCallback);
    }

    private void bindVideoResult(YouTubeExtractor.Result result) {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(this, result.getBestAvaiableQualityVideoUri());
            mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            mMediaPlayer.setSurface(new Surface(mTextureView.getSurfaceTexture()));
            mMediaPlayer.prepare();
            //mMediaPlayer.setOnBufferingUpdateListener(this);
            //mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            //mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setVolume(0.0f, 0.0f);
            mMediaPlayer.start();
            mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTransformMatrix() {
        if (mMediaPlayer != null) {
            float height = mMediaPlayer.getVideoHeight();
            float width = mMediaPlayer.getVideoWidth();
            float viewHeight = mTextureView.getHeight();
            float viewWidth = mTextureView.getWidth();
            if (width != 0 && height != 0 && viewHeight != 0 && viewWidth != 0) {
                float aspect = width / height;
                float scaledWidth = Math.max(viewWidth, viewHeight * aspect);
                float scaledHeight = Math.max(viewHeight, viewWidth / aspect);
                mMatrix.reset();
                mMatrix.postScale(scaledWidth / viewWidth, scaledHeight / viewHeight);
                mMatrix.postTranslate((viewWidth - scaledWidth) / 2, (viewHeight - scaledHeight) / 2);

                mTextureView.setTransform(mMatrix);


            }
        }
    }


}

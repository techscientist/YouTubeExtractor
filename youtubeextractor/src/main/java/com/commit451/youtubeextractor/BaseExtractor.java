package com.commit451.youtubeextractor;

import android.support.annotation.Nullable;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public abstract class BaseExtractor<T> {

    private static final String BASE_URL = "https://www.youtube.com/";
    static final int YOUTUBE_VIDEO_QUALITY_SMALL_240 = 36;
    static final int YOUTUBE_VIDEO_QUALITY_MEDIUM_360 = 18;
    static final int YOUTUBE_VIDEO_QUALITY_HD_720 = 22;
    static final int YOUTUBE_VIDEO_QUALITY_HD_1080 = 37;

    protected final T mYouTube;

    private final YoutubeExtractorInterceptor mYoutubeExtractorInterceptor = new YoutubeExtractorInterceptor();

    public BaseExtractor(Class<T> youTubeClass, OkHttpClient.Builder okBuilder,
                         @Nullable CallAdapter.Factory callAdapterFactory) {

        okBuilder.addInterceptor(mYoutubeExtractorInterceptor);

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

        if (callAdapterFactory != null) {
            retrofitBuilder.addCallAdapterFactory(callAdapterFactory);
        }

        retrofitBuilder
            .baseUrl(BASE_URL)
            .client(okBuilder.build())
            .addConverterFactory(YouTubeExtractionConverterFactory.create());

        mYouTube = retrofitBuilder.build().create(youTubeClass);
    }

    public void setLanguage(String language) {
        mYoutubeExtractorInterceptor.setLanguage(language);
    }

}

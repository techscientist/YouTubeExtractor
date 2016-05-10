package com.commit451.youtubeextractor;

import android.support.annotation.NonNull;

import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Class that allows you to extract desired data from a YouTube video, such as streamable {@link android.net.Uri}s
 * given its video id, which is typically contained within the YouTube video url, ie. https://www.youtube.com/watch?v=dQw4w9WgXcQ
 * has a video id of dQw4w9WgXcQ
 */
public class YouTubeExtractor {

    public static final int YOUTUBE_VIDEO_QUALITY_SMALL_240 = 36;
    public static final int YOUTUBE_VIDEO_QUALITY_MEDIUM_360 = 18;
    public static final int YOUTUBE_VIDEO_QUALITY_HD_720 = 22;
    public static final int YOUTUBE_VIDEO_QUALITY_HD_1080 = 37;

    private static final String EL_TYPE = "info";
    private static final String PS_TYPE = "default";
    private static final String GL_TYPE = "US";
    /**
     * Retrofit interface, which we use, but do not expose
     */
    interface YouTube {

        @GET("get_video_info?el=" + EL_TYPE + "&ps=" + PS_TYPE + "&gl=" + GL_TYPE)
        Call<YouTubeExtractionResult> getYouTubeVideoData(@Query("video_id") String videoId,
                                                          @Query("language") String language,
                                                          @Header("Accept-Language") String languageForHeader);
    }

    private YouTube mYouTube;
    private String mLanguage;

    /**
     * Create a YouTubeExtractor
     * @return a new {@link YouTubeExtractor}
     */
    public static YouTubeExtractor create() {
        return create(new OkHttpClient());
    }

    /**
     * Create a YouTubeExtractor, with a customly configured {@link OkHttpClient}
     * @param okHttpClient a configured {@link OkHttpClient}
     * @return a new {@link YouTubeExtractor}
     */
    public static YouTubeExtractor create(OkHttpClient okHttpClient) {
        YouTubeExtractor extractor = new YouTubeExtractor();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.youtube.com/")
                .client(okHttpClient)
                .addConverterFactory(YouTubeExtractionConverterFactory.create())
                .build();
        extractor.mYouTube = retrofit.create(YouTube.class);
        extractor.mLanguage = Locale.getDefault().getLanguage();
        return extractor;
    }

    /**
     * Set the language. Defaults to {@link Locale#getDefault()}
     * @param language the language
     */
    public void setLanguage(String language) {
        mLanguage = language;
    }

    /**
     * Extract the YouTube video data.
     * @param videoId the id of the YouTube video, which can be found in the URL
     * @return the Retrofit call, which you can call {@link Call#execute()} or {@link Call#enqueue(Callback)} on
     */
    public Call<YouTubeExtractionResult> extract(@NonNull String videoId) {
        return mYouTube.getYouTubeVideoData(videoId, mLanguage, mLanguage);
    }
}

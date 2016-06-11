package com.commit451.youtubeextractor;

import okhttp3.OkHttpClient;
import retrofit2.Call;

/**
 * Class that allows you to extract desired data from a YouTube video, such as streamable {@link android.net.Uri}s
 * given its video id, which is typically contained within the YouTube video url, ie. https://www.youtube.com/watch?v=dQw4w9WgXcQ
 * has a video id of dQw4w9WgXcQ
 */
public class YouTubeExtractor extends BaseExtractor<YouTubeDefault> implements YouTubeDefault {

    public YouTubeExtractor() {
        super(YouTubeDefault.class, new OkHttpClient.Builder(), null);
    }

    @Override public Call<YouTubeExtractionResult> getYouTubeVideoData(String videoId) {
        return mYouTube.getYouTubeVideoData(videoId);
    }
}

package com.commit451.youtubeextractor;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YouTubeDefault {

    @GET(Constants.INFO) Call<YouTubeExtractionResult> getYouTubeVideoData(@Query(Constants.VIDEO_ID) String videoId);

}

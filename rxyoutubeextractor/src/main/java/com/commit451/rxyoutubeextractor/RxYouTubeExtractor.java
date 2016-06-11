package com.commit451.rxyoutubeextractor;


import com.commit451.youtubeextractor.BaseExtractor;
import com.commit451.youtubeextractor.YouTubeExtractionResult;

import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Class that allows you to extract desired data from a YouTube video, such as streamable {@link android.net.Uri}s
 * given its video id, which is typically contained within the YouTube video url, ie. https://www.youtube.com/watch?v=dQw4w9WgXcQ
 * has a video id of dQw4w9WgXcQ
 */
public class RxYouTubeExtractor extends BaseExtractor<RxYouTube> implements RxYouTube {

    public RxYouTubeExtractor() {
        this(new OkHttpClient.Builder(), Schedulers.io());
    }

    public RxYouTubeExtractor(OkHttpClient.Builder okHttpClientBuilder, Scheduler scheduler) {
        super(RxYouTube.class, okHttpClientBuilder, RxJavaCallAdapterFactory.createWithScheduler(scheduler));
    }

    @Override
    public Observable<YouTubeExtractionResult> extract(String videoId) {
        return getYouTube().extract(videoId);
    }
}

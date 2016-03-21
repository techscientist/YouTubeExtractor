package com.commit451.youtubeextractor;

import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.webkit.MimeTypeMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static java.util.Arrays.asList;

/**
 * Extracts needed information for a YouTube video given a video identifier.
 */
public class YouTubeExtractor {

    private static final int YOUTUBE_VIDEO_QUALITY_SMALL_240 = 36;
    private static final int YOUTUBE_VIDEO_QUALITY_MEDIUM_360 = 18;
    private static final int YOUTUBE_VIDEO_QUALITY_HD_720 = 22;
    private static final int YOUTUBE_VIDEO_QUALITY_HD_1080 = 37;

    private final String mVideoIdentifier;
    private HttpsURLConnection mConnection;
    private boolean mCancelled;

    public YouTubeExtractor(String videoIdentifier) {
        mVideoIdentifier = videoIdentifier;
    }

    public void extract(final Callback listener) {
        String elField = "embedded";

        final String language = Locale.getDefault().getLanguage();

        final String link = String.format("https://www.youtube.com/get_video_info?video_id=%s&el=%s&ps=default&eurl=&gl=US&hl=%s", mVideoIdentifier, elField, language);

        final HandlerThread youtubeExtractorThread = new HandlerThread("YouTubeExtractorThread",
                Process.THREAD_PRIORITY_BACKGROUND);
        youtubeExtractorThread.start();

        final Handler youtubeExtractorHandler = new Handler(youtubeExtractorThread.getLooper());

        final Handler listenerHandler = new Handler(Looper.getMainLooper());

        youtubeExtractorHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mConnection = (HttpsURLConnection) new URL(link).openConnection();
                    mConnection.setRequestProperty("Accept-Language", language);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(mConnection.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null && !mCancelled) builder.append(line);

                    reader.close();

                    if (!mCancelled) {
                        final Result result = getYouTubeResult(builder.toString());

                        listenerHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!mCancelled && listener != null) {
                                    listener.onSuccess(result);
                                }
                            }
                        });
                    }
                } catch (final Exception e) {
                    listenerHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!mCancelled && listener != null) {
                                listener.onFailure(e);
                            }
                        }
                    });
                } finally {
                    if (mConnection != null) {
                        mConnection.disconnect();
                    }

                    youtubeExtractorThread.quit();
                }
            }
        });
    }

    public void cancel() {
        mCancelled = true;
    }

    private static HashMap<String, String> getQueryMap(String queryString, String charsetName) throws UnsupportedEncodingException {
        HashMap<String, String> map = new HashMap<>();

        String[] fields = queryString.split("&");

        for (String field : fields) {
            String[] pair = field.split("=");
            if (pair.length == 2) {
                String key = pair[0];
                String value = URLDecoder.decode(pair[1], charsetName).replace('+', ' ');
                map.put(key, value);
            }
        }

        return map;
    }

    private Result getYouTubeResult(String html) throws UnsupportedEncodingException, YouTubeExtractorException {
        HashMap<String, String> video = getQueryMap(html, "UTF-8");

        if (video.containsKey("url_encoded_fmt_stream_map")) {
            List<String> streamQueries = new ArrayList<>(asList(video.get("url_encoded_fmt_stream_map").split(",")));

            String adaptiveFmts = video.get("adaptive_fmts");
            String[] split = adaptiveFmts.split(",");

            streamQueries.addAll(asList(split));

            SparseArray<String> streamLinks = new SparseArray<>();
            for (String streamQuery : streamQueries) {
                HashMap<String, String> stream = getQueryMap(streamQuery, "UTF-8");
                String type = stream.get("type").split(";")[0];
                String urlString = stream.get("url");

                if (urlString != null && MimeTypeMap.getSingleton().hasMimeType(type)) {
                    String signature = stream.get("sig");

                    if (signature != null) {
                        urlString = urlString + "&signature=" + signature;
                    }

                    if (getQueryMap(urlString, "UTF-8").containsKey("signature")) {
                        streamLinks.put(Integer.parseInt(stream.get("itag")), urlString);
                    }
                }
            }

            final Uri sd240VideoUri = extractVideoUri(YOUTUBE_VIDEO_QUALITY_SMALL_240, streamLinks);
            final Uri sd360VideoUri = extractVideoUri(YOUTUBE_VIDEO_QUALITY_MEDIUM_360, streamLinks);
            final Uri hd720VideoUri = extractVideoUri(YOUTUBE_VIDEO_QUALITY_HD_720, streamLinks);
            final Uri hd1080VideoUri = extractVideoUri(YOUTUBE_VIDEO_QUALITY_HD_1080, streamLinks);

            final Uri mediumThumbUri = video.containsKey("iurlmq") ? Uri.parse(video.get("iurlmq")) : null;
            final Uri highThumbUri = video.containsKey("iurlhq") ? Uri.parse(video.get("iurlhq")) : null;
            final Uri defaultThumbUri = video.containsKey("iurl") ? Uri.parse(video.get("iurl")) : null;
            final Uri standardThumbUri = video.containsKey("iurlsd") ? Uri.parse(video.get("iurlsd")) : null;

            return new Result(sd240VideoUri, sd360VideoUri, hd720VideoUri, hd1080VideoUri,
                    mediumThumbUri, highThumbUri, defaultThumbUri, standardThumbUri);
        } else {
            throw new YouTubeExtractorException("Status: " + video.get("status") + "\nReason: " + video.get("reason") + "\nError code: " + video.get("errorcode"));
        }
    }

    @Nullable
    private Uri extractVideoUri(int quality, SparseArray<String> streamLinks) {
        Uri videoUri = null;
        if (streamLinks.get(quality, null) != null) {
            String streamLink = streamLinks.get(quality);
            videoUri = Uri.parse(streamLink);
        }
        return videoUri;
    }

    /**
     * The result of the extraction.
     */
    public static final class Result {
        private final Uri mSd240VideoUri;
        private final Uri mSd360VideoUri;
        private final Uri mHd720VideoUri;
        private final Uri mHd1080VideoUri;
        private final Uri mMediumThumbUri;
        private final Uri mHighThumbUri;
        private final Uri mDefaultThumbUri;
        private final Uri mStandardThumbUri;

        private Result(Uri sd240VideoUri, Uri sd360VideoUri, Uri hd720VideoUri, Uri hd1080VideoUri, Uri mediumThumbUri, Uri highThumbUri, Uri defaultThumbUri, Uri standardThumbUri) {
            mSd240VideoUri = sd240VideoUri;
            mSd360VideoUri = sd360VideoUri;
            mHd720VideoUri = hd720VideoUri;
            mHd1080VideoUri = hd1080VideoUri;
            mMediumThumbUri = mediumThumbUri;
            mHighThumbUri = highThumbUri;
            mDefaultThumbUri = defaultThumbUri;
            mStandardThumbUri = standardThumbUri;
        }

        @Nullable
        public Uri getSd240VideoUri() {
            return mSd240VideoUri;
        }

        @Nullable
        public Uri getSd360VideoUri() {
            return mSd360VideoUri;
        }

        @Nullable
        public Uri getHd720VideoUri() {
            return mHd720VideoUri;
        }

        @Nullable
        public Uri getHd1080VideoUri() {
            return mHd1080VideoUri;
        }

        /**
         * Get the best available quality video, starting with 1080p all the way down to 240p.
         * @return the best quality video uri, or null if no uri is available
         */
        @Nullable
        public Uri getBestAvaiableQualityVideoUri() {
            Uri uri = getHd1080VideoUri();
            if (uri != null) {
                return uri;
            }
            uri = getHd720VideoUri();
            if (uri != null) {
                return uri;
            }
            uri = getSd360VideoUri();
            if (uri != null) {
                return uri;
            }
            uri = getSd240VideoUri();
            if (uri != null) {
                return uri;
            }
            return null;
        }

        @Nullable
        public Uri getMediumThumbUri() {
            return mMediumThumbUri;
        }

        @Nullable
        public Uri getHighThumbUri() {
            return mHighThumbUri;
        }

        @Nullable
        public Uri getDefaultThumbUri() {
            return mDefaultThumbUri;
        }

        @Nullable
        public Uri getStandardThumbUri() {
            return mStandardThumbUri;
        }
    }

    public static final class YouTubeExtractorException extends Exception {
        public YouTubeExtractorException(String detailMessage) {
            super(detailMessage);
        }
    }

    public interface Callback {
        void onSuccess(Result result);

        void onFailure(Throwable t);
    }
}

package com.commit451.youtubeextractor;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

/**
 * The result of a YouTube extraction.
 */
public class YouTubeExtractionResult implements Parcelable {

    private final Uri mSd240VideoUri;
    private final Uri mSd360VideoUri;
    private final Uri mHd720VideoUri;
    private final Uri mHd1080VideoUri;
    private final Uri mMediumThumbUri;
    private final Uri mHighThumbUri;
    private final Uri mDefaultThumbUri;
    private final Uri mStandardThumbUri;

    protected YouTubeExtractionResult(Uri sd240VideoUri, Uri sd360VideoUri, Uri hd720VideoUri, Uri hd1080VideoUri, Uri mediumThumbUri, Uri highThumbUri, Uri defaultThumbUri, Uri standardThumbUri) {
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

    /**
     * Convenience method which will go through all thumbnail {@link Uri}s and return you the best one
     * @return the best image uri, or null if none exist
     */
    @Nullable
    public Uri getBestAvaiableQualityThumbUri() {
        Uri uri = getHighThumbUri();
        if (uri != null) {
            return uri;
        }
        uri = getMediumThumbUri();
        if (uri != null) {
            return uri;
        }
        uri = getDefaultThumbUri();
        if (uri != null) {
            return uri;
        }
        uri = getStandardThumbUri();
        if (uri != null) {
            return uri;
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mSd240VideoUri, flags);
        dest.writeParcelable(this.mSd360VideoUri, flags);
        dest.writeParcelable(this.mHd720VideoUri, flags);
        dest.writeParcelable(this.mHd1080VideoUri, flags);
        dest.writeParcelable(this.mMediumThumbUri, flags);
        dest.writeParcelable(this.mHighThumbUri, flags);
        dest.writeParcelable(this.mDefaultThumbUri, flags);
        dest.writeParcelable(this.mStandardThumbUri, flags);
    }

    protected YouTubeExtractionResult(Parcel in) {
        this.mSd240VideoUri = in.readParcelable(Uri.class.getClassLoader());
        this.mSd360VideoUri = in.readParcelable(Uri.class.getClassLoader());
        this.mHd720VideoUri = in.readParcelable(Uri.class.getClassLoader());
        this.mHd1080VideoUri = in.readParcelable(Uri.class.getClassLoader());
        this.mMediumThumbUri = in.readParcelable(Uri.class.getClassLoader());
        this.mHighThumbUri = in.readParcelable(Uri.class.getClassLoader());
        this.mDefaultThumbUri = in.readParcelable(Uri.class.getClassLoader());
        this.mStandardThumbUri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Parcelable.Creator<YouTubeExtractionResult> CREATOR = new Parcelable.Creator<YouTubeExtractionResult>() {
        @Override
        public YouTubeExtractionResult createFromParcel(Parcel source) {
            return new YouTubeExtractionResult(source);
        }

        @Override
        public YouTubeExtractionResult[] newArray(int size) {
            return new YouTubeExtractionResult[size];
        }
    };
}

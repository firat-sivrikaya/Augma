package world.augma.work.visual;

import com.bumptech.glide.load.model.GlideUrl;

public class S3UrlBuilder extends GlideUrl {

    private String mSourceUrl;

    public S3UrlBuilder(String base, String userID, String noteID) {
        super(build(base, userID, noteID, false));

        mSourceUrl = base;
    }

    public S3UrlBuilder(String base, String userID, boolean isProfile) {
        super(build(base, userID, null, isProfile));

        mSourceUrl = base;
    }

    private static String build(String base, String userID, String noteID, boolean isProfile) {
        StringBuilder builder = new StringBuilder(base);

        if(noteID != null) {
            builder
                    .append(userID)
                    .append('/')
                    .append(noteID)
                    .append(".jpg");
        } else {
            builder
                    .append(userID)
                    .append('/')
                    .append(userID)
                    .append(isProfile ? ".jpg" : "B.jpg");
        }

        return builder.toString();
    }

    @Override
    public String getCacheKey() {
        return mSourceUrl;
    }

    @Override
    public String toString() {
        return super.getCacheKey();
    }
}

package world.augma.work;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class S3 {

    private static final String URL = "https://s3.eu-central-1.amazonaws.com/augma/";

    /** Don't instantiate */
    private S3() {}

    public static void fetchProfileImage(Activity activity, ImageView img, String path){

            Glide.with(activity)
                    .load(URL.concat(path).concat(".jpg"))
                    .crossFade().bitmapTransform(new ProfileImageTransformer(activity))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img);
    }

    public static void fetchImage(Activity activity, ImageView img, String path) {

        //TODO şimdilik local sonra url çek

        Glide.with(activity)
                .load(path/*URL.concat(path).concat(".jpg")*/)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img);
    }

}

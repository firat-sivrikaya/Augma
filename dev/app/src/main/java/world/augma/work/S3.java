package world.augma.work;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class S3 {

    private static final String URL = "https://s3.eu-central-1.amazonaws.com/augma/";

    /** Don't instantiate */
    private S3() {}

    public static void fetchProfileImage(Activity activity, ImageView img, String userID){
        //TODO burada ilk once localde var mi diye kontrol edip yoksa S3 den cekip locale kaydedicez
            Glide.with(activity)
                    .load(URL.concat(userID).concat("/").concat(userID).concat(".jpg"))
                    .crossFade().bitmapTransform(new ProfileImageTransformer(activity))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img);
    }

    public static void fetchBackgroundImage(Activity activity, ImageView img, String path) {

        //TODO burada ilk once localde var mi diye kontrol edip yoksa S3 den cekip locale kaydedicez

        Glide.with(activity)
                .load(path)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img);

        /*S3
        Glide.with(activity)
                .load(URL.concat(userID).concat("/").concat(userID).concat("B").concat(".jpg"))
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img);

         */
    }

    public static void fetchNoteImage(){
        //TODO
    }

}

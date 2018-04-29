package world.augma.work;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class S3 {

    private static final String URL = "https://s3.eu-central-1.amazonaws.com/augma/";

    /** Don't instantiate */
    private S3() {}

    public static void s3FetchImage(Activity activity, ImageView img, String userID, String noteID ){

        if(userID.equals(noteID)){
            Glide.with(activity)
                    .load(URL +  userID +"/" + noteID + ".jpg")
                    .crossFade().bitmapTransform(new ProfileImageTransformer(activity))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img);
        }
        else{
            Glide.with(activity)
                    .load(URL +  userID +"/"+ noteID + ".jpg")
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img);
        }


    }
}

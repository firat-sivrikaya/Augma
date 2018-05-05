package world.augma.work.visual;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.util.concurrent.ExecutionException;

import world.augma.asset.AugmaVisualType;
import world.augma.work.AWS;

public class S3 {

    private static final String BASE_URL = "https://s3.eu-central-1.amazonaws.com/augma/";
    private static final String TAG = "[".concat(S3.class.getSimpleName()).concat("]");

    /** Don't instantiate */
    private S3() {}

    public static void fetchProfileImage(Activity activity, ImageView img, String userID){
          AugmaImager.set(AugmaVisualType.PROFILE, activity, img, new S3UrlBuilder(BASE_URL, userID, true));
    }

    public static void fetchBackgroundImage(Activity activity, ImageView img, String userID) {
        AugmaImager.set(AugmaVisualType.BACKGROUND, activity, img, new S3UrlBuilder(BASE_URL, userID, false));
    }

    public static void fetchNoteImage(Activity activity, ImageView img, String userID, String noteID){
        AugmaImager.set(AugmaVisualType.NOTE, activity, img, new S3UrlBuilder(BASE_URL, userID, noteID));
    }

    public static boolean uploadProfileImage(byte[] imageByte, String userID){
        try {
            return new AWS().execute(AWS.Service.UPLOAD_IMAGE, userID, userID,Base64.encodeToString(imageByte, 0)).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "ERROR: Cannot upload image to cloud.");
        }
        return false;
    }

}
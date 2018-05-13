package world.augma.work.visual;

import android.app.Activity;
import android.content.Context;
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
          //AugmaImager.set(AugmaVisualType.PROFILE, activity, img, new S3UrlBuilder(BASE_URL, userID, true));
        AugmaImager.set(AugmaVisualType.PROFILE, activity, img, BASE_URL.concat(userID).concat("/").concat(userID).concat(".jpg"));
    }

    public static void fetchBackgroundImage(Activity activity, ImageView img, String userID) {
        //AugmaImager.set(AugmaVisualType.BACKGROUND, activity, img, new S3UrlBuilder(BASE_URL, userID, false));
        AugmaImager.set(AugmaVisualType.BACKGROUND, activity, img, BASE_URL.concat(userID).concat("/").concat(userID).concat("B.jpg"));
    }

    public static void fetchNoteImage(Activity activity, ImageView img, String userID, String noteID){
        //AugmaImager.set(AugmaVisualType.NOTE, activity, img, new S3UrlBuilder(BASE_URL, userID, noteID));
        AugmaImager.set(AugmaVisualType.NOTE, activity, img, BASE_URL.concat(userID).concat("/").concat(noteID).concat(".jpg"));
    }

    public static void fetchNotePreviewImage(Context activity, ImageView img, String userID, String noteID){
        //AugmaImager.set(AugmaVisualType.NOTE, activity, img, new S3UrlBuilder(BASE_URL, userID, noteID));
        AugmaImager.set(AugmaVisualType.NOTE_PREVIEW, activity, img, BASE_URL.concat(userID).concat("/").concat(noteID).concat(".jpg"));
    }

    public static boolean uploadProfileImage(byte[] imageByte, String userID){
        try {
            return new AWS().execute(AWS.Service.UPLOAD_IMAGE, userID, userID,Base64.encodeToString(imageByte, 0)).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "ERROR: Cannot upload image to cloud.");
        }
        return false;
    }

    public static boolean uploadNoteImage(String base64image, String userID,String noteID){
        try {
            return new AWS().execute(AWS.Service.UPLOAD_IMAGE, userID, noteID,base64image).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "ERROR: Cannot upload image to cloud.");
        }
        return false;
    }

}

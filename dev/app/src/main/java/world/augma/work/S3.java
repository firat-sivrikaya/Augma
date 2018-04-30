package world.augma.work;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class S3 {

    private static final String URL = "https://s3.eu-central-1.amazonaws.com/augma/";

    /** Don't instantiate */
    private S3() {}

    public static void fetchProfileImage( Activity activity, ImageView img, String userID){
        //TODO burada ilk once localde var mi diye kontrol edip yoksa S3 den cekip locale kaydedicez
            File image = new File(activity.getApplicationContext().getFilesDir(),userID);
            if(!image.exists()){
                Glide.with(activity)
                        .load(URL.concat(userID).concat("/").concat(userID).concat(".jpg"))
                        .crossFade().bitmapTransform(new ProfileImageTransformer(activity))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(img);
            }
            else{
                Glide.with(activity).load(image).crossFade()
                        .bitmapTransform(new ProfileImageTransformer(activity))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(img);
            }

            /*


             */


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

    public static boolean saveImageToStorage(Context context,byte[] imageByte, String userID){
        File image = new File(context.getFilesDir(),userID);
        if(image.exists()){
            image.delete();
        }
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(userID, Context.MODE_PRIVATE);
            outputStream.write(imageByte);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean uploadProfileImage(Context context,byte[] imageByte, String userID){


       boolean saveRes = saveImageToStorage(context,imageByte,userID);

        //upload
        String base64_image = Base64.encodeToString(imageByte, 0);

        AWS aws = new AWS();
        boolean uploadRes = false;
        try {
            uploadRes= aws.execute(AWS.Service.UPLOAD_IMAGE, userID,userID,base64_image).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return saveRes && uploadRes;

    }

}

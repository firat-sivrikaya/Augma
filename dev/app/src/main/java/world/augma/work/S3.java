package world.augma.work;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.util.IOUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;

public class S3 {

    private static final String URL = "https://s3.eu-central-1.amazonaws.com/augma/";

    /** Don't instantiate */
    private S3() {}

    public static void fetchProfileImage(Activity activity, ImageView img, String userID){
        //TODO burada ilk once localde var mi diye kontrol edip yoksa S3 den cekip locale kaydedicez
           File image = new File(activity.getApplicationContext().getFilesDir(),userID);
            if(!image.exists()){

                Log.e("Fetchin from S3:",URL.concat(userID).concat("/").concat(userID).concat(".jpg"));
                img.invalidate();
                Glide.with(activity)
                        .load(URL.concat(userID).concat("/").concat(userID).concat(".jpg"))
                        .crossFade().bitmapTransform(new ProfileImageTransformer(activity))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(img);

            }
            else{
                Log.e("Fetchin from Internal storage:","Fetched");
                img.invalidate();
                Glide.with(activity).load(image).crossFade()
                        .bitmapTransform(new ProfileImageTransformer(activity))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(img);
            }





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


       /* File file = new File(context.getFilesDir(), userID);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(imageByte);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
        */

    }

    public static boolean uploadProfileImage(Context context,byte[] imageByte, String userID){
        String base64_image = Base64.encodeToString(imageByte, 0);
        //boolean delRes = false;
        //delRes = context.deleteFile(userID);
        //Log.e("Filedeleted:",""+delRes);
       boolean saveRes = saveImageToStorage(context,imageByte,userID);
       Log.e("Imagesaved:",""+saveRes);

        //upload


        AWS aws = new AWS();
        boolean uploadRes = false;
        try {
            uploadRes= aws.execute(AWS.Service.UPLOAD_IMAGE, userID,userID,base64_image).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Log.e("ImageUploaded:",""+uploadRes);

        return /*saveRes &&*/ uploadRes;

    }

}

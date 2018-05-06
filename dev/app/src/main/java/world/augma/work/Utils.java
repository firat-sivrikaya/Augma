package world.augma.work;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.irozon.sneaker.Sneaker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import world.augma.asset.effects.BlurEffect;

/**
 * Created by Burak on 23-Mar-18.
 */

public final class Utils {

    /**
     * Constants for blurring.
     */
    public static final float GLOBAL_BLUR_SCALE_FACTOR = 8.0f;
    public static final float GLOBAL_BLUR_SCALE_RADIUS = 2;

    /**
     * Regular expressions for validation.
     */
    private static final Pattern USERNAME_REGEX = Pattern.compile("[a-zA-Z][0-9a-zA-Z,._]+");
    private static final Pattern EMAIL_REGEX = Pattern.compile("[A-Za-z0-9._]+@[A-Za-z0-9.]+\\.[A-Za-z]+");
    /****************/

    private static final String TAG = "[".concat(Utils.class.getSimpleName()).concat("]");

    /* Don't let this class to be instantiated */
    private Utils() {}

    public static Bitmap convertDrawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);

        if(view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static boolean validateUsername(String username) {
        return USERNAME_REGEX.matcher(username).matches();
    }

    public static boolean validateEmail(String email) {
        return EMAIL_REGEX.matcher(email).matches();
    }

    public static void sendErrorNotification(Activity activity, String errorMsg) {
        Sneaker.with(activity)
                .setTitle("Error!")
                .setMessage(errorMsg)
                .sneakError();
    }

    public static void sendWarningNotification(Activity activity, String warningMsg) {
        Sneaker.with(activity)
                .setTitle("Warning!")
                .setMessage(warningMsg)
                .sneakWarning();
    }

    public static void sendSuccessNotification(Activity activity, String successMsg) {
        Sneaker.with(activity)
                .setTitle("Success!")
                .setMessage(successMsg)
                .sneakSuccess();
    }

    public static void blur(Bitmap bm, View view) {

        int scaleFactor = (int) Utils.GLOBAL_BLUR_SCALE_FACTOR;

        Bitmap overlay = Bitmap.createBitmap(view.getMeasuredWidth() / scaleFactor,
                view.getMeasuredHeight() / scaleFactor, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        Paint paint = new Paint();

        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        canvas.drawBitmap(bm, 0, 0, paint);

        overlay = BlurEffect.blur(overlay, (int) Utils.GLOBAL_BLUR_SCALE_RADIUS, true);
        view.setBackground(new BitmapDrawable(view.getResources(), overlay));
    }

    public static void storeImage(Bitmap bitmap, Context context) {

        File picture = createLocalFileInstance(context);
        if(picture == null) {
            Log.e(TAG, "ERROR: Unable to store the image. Check storage permissions.");
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(picture);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File createLocalFileInstance(Context context) {
        File fileDir = new File(Environment.getExternalStorageDirectory()
                +"/Android/data" + context.getApplicationContext().getPackageName() + "/Files");

        if(!fileDir.exists() && !fileDir.mkdirs()) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder(fileDir.getPath());

        stringBuilder
                .append(File.separator)
                .append("AUGMA_")
                .append(new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date()))
                .append(".jpg");

        return new File(stringBuilder.toString());
    }
}

package world.augma.work;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.irozon.sneaker.Sneaker;

import java.util.regex.Pattern;

/**
 * Created by Burak on 23-Mar-18.
 */

public final class Utils {

    public static final float GLOBAL_BLUR_SCALE_FACTOR = 8.0f;
    public static final float GLOBAL_BLUR_SCALE_RADIUS = 2;

    /**
     * Regular expressions for validation.
     */
    private static final Pattern USERNAME_REGEX = Pattern.compile("[a-zA-Z][0-9a-zA-Z,._]+");

    private static final Pattern EMAIL_REGEX = Pattern.compile("[A-Za-z0-9._]+@[A-Za-z0-9.]+\\.[A-Za-z]+");

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

    /*
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();

        if(view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
    }

    public static void hideKeyboardFromContext(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
    }

    */

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
}

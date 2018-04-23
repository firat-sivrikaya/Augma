package world.augma.utils;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Burak on 21-Mar-18.
 */

public final class UIUtils {

    /* This class is used for commonly used UI operations and should not be instantiated */
    private UIUtils() {}

    public static void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
}

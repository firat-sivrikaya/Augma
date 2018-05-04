package world.augma.asset;

import android.content.Context;
import android.content.SharedPreferences;

public final class AugmaSharedPreferences {

    /** Shared Preferences for Augma */
    private static final String SHARED_PREFS = "augmaSP";

    /**
     * Shared Preferences Fields
     */
    private static final String USER_ID = "userID";

    private AugmaSharedPreferences() {}

    public static void login(Context context, String userID) {
        SharedPreferences.Editor sp = context.getSharedPreferences(AugmaSharedPreferences.SHARED_PREFS, Context.MODE_PRIVATE).edit();
        sp.putString(AugmaSharedPreferences.USER_ID, userID);
        sp.apply();
    }

    public static boolean isLoggedIn(Context context) {
        return context.getSharedPreferences(AugmaSharedPreferences.SHARED_PREFS,
                Context.MODE_PRIVATE).getString(AugmaSharedPreferences.USER_ID, null) != null;
    }

    public static void logout(Context context) {
        SharedPreferences.Editor sp = context.getSharedPreferences(AugmaSharedPreferences.SHARED_PREFS, Context.MODE_PRIVATE).edit();
        sp.remove(AugmaSharedPreferences.USER_ID);
        sp.apply();
    }

    public static String getUserId(Context context) {
        return context.getSharedPreferences(AugmaSharedPreferences.SHARED_PREFS, Context.MODE_PRIVATE).getString(USER_ID, "DEFAULT");
    }
}

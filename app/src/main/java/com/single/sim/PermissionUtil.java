package com.single.sim;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public abstract class PermissionUtil {

    /**
     * Returns true if the Context has access to all given permissions.
     * Always returns true on platforms below M.
     *
     * @see Context#checkSelfPermission(String)
     */
    public static boolean hasSelfPermission(Context context, String[] permissions) {
        // Below Android M all permissions are granted at install time and are already available.
        if (!isM()) {
            return true;
        }

        // Verify that all required permissions have been granted
        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the Activity has access to a given permission.
     * Always returns true on platforms below M.
     *
     * @see Context#checkSelfPermission(String)
     */
    public static boolean hasSelfPermission(Context context, String permission) {
        // Below Android M all permissions are granted at install time and are already available.
        if (!isM()) {
            return true;
        }

        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isM() {
        /*
         TODO: In the Android M Preview release, checking if the platform is M is done through
         the codename, not the version code. Once the API has been finalised, the following check
         should be used: */
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}

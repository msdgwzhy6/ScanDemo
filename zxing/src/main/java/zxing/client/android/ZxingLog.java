package zxing.client.android;

/**
 * Created by kingman on 2017/10/24.
 */

public class ZxingLog {
    private static final String TAG = "scanPaySDK";
    public static boolean LOG_ENABLE=true;
    public static void i(String tag, String message) {
        if (LOG_ENABLE) {
            android.util.Log.i(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (LOG_ENABLE) {
            android.util.Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (LOG_ENABLE) {
            android.util.Log.e(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (LOG_ENABLE) {
            android.util.Log.w(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (LOG_ENABLE) {
            android.util.Log.v(tag, message);
        }
    }

    public static void i(String message) {
        if (LOG_ENABLE) {
            android.util.Log.i(TAG, message);
        }
    }

    public static void d(String message) {
        if (LOG_ENABLE) {
            android.util.Log.d(TAG, message);
        }
    }

    public static void e(String message) {
        if (LOG_ENABLE) {
            android.util.Log.e(TAG, message);
        }
    }

    public static void w(String message) {
        if (LOG_ENABLE) {
            android.util.Log.w(TAG, message);
        }
    }

    public static void v(String message) {
        if (LOG_ENABLE) {
            android.util.Log.v(TAG, message);
        }
    }
}

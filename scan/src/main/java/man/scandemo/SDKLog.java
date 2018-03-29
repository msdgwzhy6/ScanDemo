package man.scandemo;

/**
 * Created by kingman on 2017/7/20.
 */

public class SDKLog {
    private static final String TAG="scanPaySDK";
    public static void i(String tag,String message){
        if(Config.LOG_ENABLE){
            android.util.Log.i(tag,message);
        }
    }
    public static void d(String tag,String message){
        if(Config.LOG_ENABLE) {
            android.util.Log.d(tag, message);
        }
    }
    public static void e(String tag,String message){
        if(Config.LOG_ENABLE) {
            android.util.Log.e(tag, message);
        }
    }
    public static void w(String tag,String message){
        if(Config.LOG_ENABLE) {
            android.util.Log.w(tag, message);
        }
    }
    public static void v(String tag,String message){
        if(Config.LOG_ENABLE) {
            android.util.Log.v(tag, message);
        }
    }
    public static void i(String message){
        if(Config.LOG_ENABLE) {
            android.util.Log.i(TAG, message);
        }
    }
    public static void d(String message){
        if(Config.LOG_ENABLE) {
            android.util.Log.d(TAG, message);
        }
    }
    public static void e(String message){
        if(Config.LOG_ENABLE) {
            android.util.Log.e(TAG, message);
        }
    }
    public static void w(String message){
        if(Config.LOG_ENABLE) {
            android.util.Log.w(TAG, message);
        }
    }
    public static void v(String message){
        if(Config.LOG_ENABLE) {
            android.util.Log.v(TAG, message);
        }
    }

    static class Config{
        public static boolean LOG_ENABLE = true;
    }
}

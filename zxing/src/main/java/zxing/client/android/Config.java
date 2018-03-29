package zxing.client.android;

import zxing.client.android.camera.FrontLightMode;

/**
 * Created by kingman on 2017/7/23.
 */

public class Config {

    public static boolean KEY_AUTO_FOCUS=true;
    public static boolean KEY_DISABLE_CONTINUOUS_FOCUS=true;
    public static boolean KEY_INVERT_SCAN=false;
    public static boolean KEY_DISABLE_BARCODE_SCENE_MODE=true;
    public static boolean KEY_DISABLE_METERING=true;
    public static boolean KEY_DISABLE_EXPOSURE=true;
    public static boolean KEY_VIBRATE=false;
    public static boolean KEY_PLAY_BEEP=false;
    public static Enum KEY_FRONT_LIGHT_MODE= FrontLightMode.OFF;
    public static boolean KEY_DECODE_1D_PRODUCT=true;
    public static boolean KEY_DECODE_1D_INDUSTRIAL=true;
    public static boolean KEY_DECODE_QR=true;
    public static boolean KEY_DECODE_DATA_MATRIX=true;
    public static boolean KEY_DECODE_AZTEC=false;
    public static boolean KEY_DECODE_PDF417=false;



}

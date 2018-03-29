package man.scandemo;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;

import zxing.client.android.DecodeCallBack;
import zxing.client.android.camera.CameraManager;


/**
 * Created by kingman on 2017/9/1.
 */

public class ScanActivity extends Activity implements SurfaceHolder.Callback,DecodeCallBack,BasePopupWindow.GlobalOnDismissListener{
    public static final String KEY_CARDINDEX="KEY_CARDINDEX";


    private static final String TAG = ScanActivity.class.getSimpleName();

    Point screenResolution;
    private CameraManager cameraManager;
    private zxing.client.android.CaptureActivityHandler handler;
    private zxing.client.android.ViewfinderView viewfinderView;
    SurfaceView surfaceView;
    private boolean hasSurface;
    private Collection<BarcodeFormat> decodeFormats;
    private String characterSet;
    private zxing.client.android.InactivityTimer inactivityTimer;
    private zxing.client.android.BeepManager beepManager;
    private zxing.client.android.AmbientLightManager ambientLightManager;
    LinearLayout fukuanma;
    private ImageView scanLine;
    TranslateAnimation animation;
    FrameLayout capture_crop_view;
    View rootView;
    String qrCode;
    TextView tv_scan_result;
    private String cardIndex;
    public Handler getHandler() {
        return handler;
    }

    CameraManager getCameraManager() {
        return cameraManager;
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_scan);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hasSurface = false;
        inactivityTimer = new zxing.client.android.InactivityTimer(this);
        beepManager = new zxing.client.android.BeepManager(this);
        ambientLightManager = new zxing.client.android.AmbientLightManager(this);


        scanLine = (ImageView)findViewById(R.id.capture_scan_line);
         tv_scan_result = (TextView) findViewById(R.id.tv_scan_result);
        capture_crop_view=(FrameLayout) findViewById(R.id.capture_crop_view);
        viewfinderView = (zxing.client.android.ViewfinderView) findViewById(R.id.viewfinder_view);
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -0.08f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);

    }


    @Override
    public void onResume() {
        super.onResume();
        // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
        // want to open the camera driver and measure the screen size if we're going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
        // off screen.
        BasePopupWindow.setGlobalOnDismissListener(this);
        cameraManager = new CameraManager(this.getApplication());
        viewfinderView.setCameraManager(cameraManager);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        handler = null;
        beepManager.updatePrefs();
        ambientLightManager.start(cameraManager);
        inactivityTimer.onResume();
        decodeFormats = null;
        characterSet = null;
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceView.getHolder(),screenResolution);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceView.getHolder().addCallback(this);
        }
    }
    @Override
    public void onPause() {
        BasePopupWindow.setGlobalOnDismissListener(null);
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        ambientLightManager.stop();
        beepManager.close();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) rootView.findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    public void onDismiss(BasePopupWindow basePopupWindow) {
        restartPreviewAfterDelay(500);
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        scanLine.clearAnimation();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            SDKLog.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (!hasSurface) {
            hasSurface = true;
            screenResolution = new Point(width,height);
            initCamera(holder,screenResolution);
            capture_crop_view.getLayoutParams().height=cameraManager.findDesiredWidthInRange(screenResolution.x);
            capture_crop_view.getLayoutParams().width=cameraManager.findDesiredWidthInRange(screenResolution.x);
            capture_crop_view.requestLayout();
        }
    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param rawResult   The contents of the barcode.
     */
    @Override
    public void handleDecode(Result rawResult) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();
        qrCode = recode(rawResult.getText());
        Log.i(TAG, "handleDecode: "+qrCode);
         tv_scan_result.setText(qrCode);


    }
    private void initJSData(HashMap data){

    }
    private void initCamera(SurfaceHolder surfaceHolder,Point screenResolution) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            SDKLog.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder,screenResolution);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new zxing.client.android.CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            SDKLog.w(TAG, ioe.getMessage());
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            e.printStackTrace();
            SDKLog.w(TAG, "Unexpected error initializing camera"+e.getMessage());
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        Toast.makeText(ScanActivity.this,"displayFrameworkBugMessageAndExit:",Toast.LENGTH_SHORT).show();

//        SimplePopupWindow simplePopupWindow=new SimplePopupWindow(this,"无法获取相机权限，请允许\""+  commom.Utils.getAppName(this)
//        +"\"访问你的相机");
//        simplePopupWindow.setCancelFinishActivity(true);
//        simplePopupWindow.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(zxing.client.android.CaptureActivityHandler.restart_preview, delayMS);
        }
    }

    /**
     * 进行中文乱码处理
     *
     * @param str
     * @return
     */
    private String recode(String str) {
        String formart = "";
        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder().canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
            } else {
                formart = str;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return formart;
    }

}

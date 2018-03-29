package zxing.client.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.lang.ref.SoftReference;
import java.util.Hashtable;

/**
 * Created by kingman on 2017/7/24.
 */

public class CodeView extends ImageView {
    public static final int TYPE_QR_CODE = 0;
    public static final int TYPE_BAR_CODE = 1;
    private int type = TYPE_QR_CODE;
    Context context;
    String content;
    int width;
    int height;
    Bitmap bitmap;
    public CodeView(Context context) {
        super(context);
        init(context);
    }

    public CodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void init(Context context) {
        this.context = context;
    }

    /**
     * @param content
     * @param type    CodeView.TYPE_QR_CODE or CodeView.TYPE_BAR_CODE
     */
    public void setContent(String content, int type) {
        this.type = type;
        this.content = content;
        if(width>0&&height>0){
            this.setImageBitmap(createCodeImage(content, width, height, getBarcodeFormat()));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            width = right - left;
            height = bottom - top;
            if (content != null) {
                this.setImageBitmap(createCodeImage(content, width, height, getBarcodeFormat()));
            }

        }
    }

    private BarcodeFormat getBarcodeFormat() {
        if (type == TYPE_QR_CODE) {
            return BarcodeFormat.QR_CODE;
        } else {
            return BarcodeFormat.CODE_128;
        }
    }

    public Bitmap createCodeImage(String url, int width, int height, BarcodeFormat barcodeFormat) {
        // 判断URL合法性
        if (url == null || "".equals(url) || url.length() < 1) {
            return null;
        }
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();//一些配置项
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN,0);//不要边框


        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(url, barcodeFormat, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        int[] pixels = new int[width * height];
        // 下面这里按照二维码的算法，逐个生成二维码的图片，
        // 两个for循环是图片横列扫描的结果
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        this.bitmap=bitmap;
        return bitmap;
    }
}

/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zxing.client.android;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import zxing.client.android.camera.CameraManager;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * This thread does all the heavy lifting of decoding the images.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
final class DecodeThread extends Thread {

  public static final String BARCODE_BITMAP = "barcode_bitmap";
  public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";

  private final CaptureActivityHandler captureActivityHandler;
  private final CameraManager cameraManager;
  private final Map<DecodeHintType,Object> hints;
  private Handler handler;
  private final CountDownLatch handlerInitLatch;

  DecodeThread(CaptureActivityHandler captureActivityHandler,
               CameraManager cameraManager,
               Collection<BarcodeFormat> decodeFormats,
               String characterSet
               ) {

    this.captureActivityHandler =captureActivityHandler;
    this.cameraManager=cameraManager;
    handlerInitLatch = new CountDownLatch(1);

    hints = new EnumMap<>(DecodeHintType.class);


    // The prefs can't change while the thread is running, so pick them up once here.
    if (decodeFormats == null || decodeFormats.isEmpty()) {
      decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
      if (Config.KEY_DECODE_1D_PRODUCT) {
        decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
      }
      if (Config.KEY_DECODE_1D_INDUSTRIAL) {
        decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
      }
      if (Config.KEY_DECODE_QR) {
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
      }
      if (Config.KEY_DECODE_DATA_MATRIX) {
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
      }
      if (Config.KEY_DECODE_AZTEC) {
        decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
      }
      if (Config.KEY_DECODE_PDF417) {
        decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
      }
    }
    hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

    if (characterSet != null) {
      hints.put(DecodeHintType.CHARACTER_SET, characterSet);
    }
    Log.i("DecodeThread", "Hints: " + hints);
  }

  Handler getHandler() {
    try {
      handlerInitLatch.await();
    } catch (InterruptedException ie) {
      // continue?
    }
    return handler;
  }

  @Override
  public void run() {
    Looper.prepare();
    handler = new DecodeHandler(captureActivityHandler,cameraManager, hints);
    handlerInitLatch.countDown();
    Looper.loop();
  }

}

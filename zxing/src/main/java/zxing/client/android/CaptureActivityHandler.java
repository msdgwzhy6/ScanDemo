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
import android.os.Message;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Collection;

import zxing.client.android.camera.CameraManager;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {
    public static final int restart_preview = 0;
    public static final int decode_succeeded = 1;
    public static final int decode_failed = 2;
    public static final int quit = 3;
    public static final int decode = 4;

    private static final String TAG = CaptureActivityHandler.class.getSimpleName();

    private final DecodeCallBack activity;
    private final DecodeThread decodeThread;
    private State state;
    private final CameraManager cameraManager;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    public CaptureActivityHandler(DecodeCallBack activity,
                                  Collection<BarcodeFormat> decodeFormats,
                                  String characterSet,
                                  CameraManager cameraManager) {
        this.activity = activity;
        this.decodeThread = new DecodeThread(this, cameraManager, decodeFormats, characterSet);
        this.decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case CaptureActivityHandler.restart_preview:
                restartPreviewAndDecode();
                break;
            case CaptureActivityHandler.decode_succeeded:
                state = State.SUCCESS;
                activity.handleDecode((Result) message.obj);
                break;
            case CaptureActivityHandler.decode_failed:
                // We're decoding as fast as possible, so when one decode fails, start another.
                state = State.PREVIEW;
                cameraManager.requestPreviewFrame(decodeThread.getHandler(), CaptureActivityHandler.decode);
                break;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), CaptureActivityHandler.quit);
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause() will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(CaptureActivityHandler.decode_succeeded);
        removeMessages(CaptureActivityHandler.decode_failed);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), CaptureActivityHandler.decode);
        }
    }

}

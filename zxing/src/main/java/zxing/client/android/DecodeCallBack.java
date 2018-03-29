package zxing.client.android;

import com.google.zxing.Result;

/**
 * Created by kingman on 2017/7/24.
 */

public interface DecodeCallBack {
    public void handleDecode(Result rawResult);
}

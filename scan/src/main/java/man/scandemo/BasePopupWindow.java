package man.scandemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;


/**
 * Created by kingman on 16/6/17.
 */
public abstract class BasePopupWindow {
    PopupWindow popupWindow;
    Context context;
    View contentView;
    View topLine;
    boolean isOK = false;
    boolean isCancelFinishActivity = false;
    boolean isCancelFinishAllActivity = false;
    ArrayList<PopupWindow.OnDismissListener> dismissListenerList=new ArrayList<>();
    private static GlobalOnDismissListener globalOnDismissListener;
    private static BasePopupWindow globalInstance = null;

    public BasePopupWindow() {
        setGlobalInstance(this);
    }

    public BasePopupWindow(final Context context, View topLine) {
        superInit(context, topLine);
        setGlobalInstance(this);
    }

    public static void setGlobalInstance(BasePopupWindow instance) {
        BasePopupWindow prePopupWindow = globalInstance;
        globalInstance = instance;
        if (prePopupWindow != null&&prePopupWindow.isShowing()) {
            prePopupWindow.hide();
        }
        SDKLog.i("BasePopupWindow："+"instance:" + instance);
    }

    public static BasePopupWindow getGlobalInstance() {
        return globalInstance;
    }

    public void setCancelFinishActivity(boolean cancelFinishActivity) {
        isCancelFinishActivity = cancelFinishActivity;
    }

    public void setCancelFinishAllActivity(boolean cancelFinishAllActivity) {
        isCancelFinishAllActivity = cancelFinishAllActivity;
    }

    protected void superInit(final Context context, View topLine) {
        this.context = context;
        this.topLine = topLine;
        contentView = LayoutInflater.from(context).inflate(getLayout(), null);
        popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(globalOnDismissListener!=null){
                    globalOnDismissListener.onDismiss(BasePopupWindow.this);
                }
                for (int i = 0; i <dismissListenerList.size() ; i++) {
                    dismissListenerList.get(i).onDismiss();
                }
                if (isCancelFinishAllActivity) {
                    if (!isOK) {
//                        PaySDKManager.getInstance().finishAllSDKActivity();
                    }
                } else {
                    if (isCancelFinishActivity) {
                        if (!isOK && context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    }
                }
            }
        });
        init();
    }

    public Context getContext() {
        return context;
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public boolean isShowing() {
        return popupWindow.isShowing();
    }

    protected abstract void init();

    protected abstract int getLayout();

    public void show() {
        if (((Activity) context).getCurrentFocus() != null) {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (topLine != null) {
            popupWindow.showAsDropDown(topLine, 0, -1);
        } else {
            popupWindow.showAtLocation(contentView, Gravity.CENTER, 0, 0);
        }
    }

    public void hide() {
        popupWindow.dismiss();
    }

    public void addOnDismissListener(PopupWindow.OnDismissListener dismissListener) {
        this.dismissListenerList.add(dismissListener);
    }

    public static void setGlobalOnDismissListener(GlobalOnDismissListener globalOnDismissListener) {
        BasePopupWindow.globalOnDismissListener = globalOnDismissListener;
    }

    public interface GlobalOnDismissListener {
        public void onDismiss(BasePopupWindow basePopupWindow);
    }

    public void setOK(boolean OK) {
        isOK = OK;
    }
}

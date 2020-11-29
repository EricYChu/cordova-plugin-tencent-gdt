package cordova.plugins.tencentgdt;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

import androidx.annotation.Nullable;

public class SplashAdFragment extends AdFragment implements SplashADListener {
    private ViewGroup container;
    private int delay;

    public static SplashAdFragment newInstance(String slotId, int delay) {
        Bundle bundle = new Bundle();
        bundle.putString("slotId", slotId);
        bundle.putInt("delay", delay);

        SplashAdFragment fragment = new SplashAdFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected boolean retrieveData(Bundle arguments) {
        delay = arguments.getInt("delay", 0);

        return delay > 0;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.windowAnimations = android.R.style.Animation_Dialog;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
                    return true;
                }
                return false;
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout relativeLayout = new RelativeLayout(mContext);
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        relativeLayout.addView(linearLayout);

        this.container = new FrameLayout(mContext);
        this.container.setBackgroundColor(Color.TRANSPARENT);
        this.container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        linearLayout.addView(this.container);

        return relativeLayout;
    }

    @Override
    protected void onBeforeDestroy() {
        super.onBeforeDestroy();
        if (container != null) {
            container.clearAnimation();
        }
        System.gc();
    }

    @Override
    protected void showAd() {
        SplashAD splashAd = new SplashAD(mContext, slotId, this, delay * 1000);
        splashAd.fetchAndShowIn(container);
    }

    @Override
    public void onADClicked() {
        sendPluginResult("click", true);
    }

    @Override
    public void onADDismissed() {
        finish();
    }

    @Override
    public void onADExposure() {
    }

    @Override
    public void onADLoaded(long l) {
    }

    @Override
    public void onADPresent() {
        TranslateAnimation t3 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0);
        t3.setFillAfter(true);
        t3.setDuration(150);
        t3.setInterpolator(new LinearInterpolator());
        container.startAnimation(t3);

        sendPluginResult("show", true);
    }

    @Override
    public void onADTick(long l) {
    }

    @Override
    public void onNoAD(AdError adError) {
        sendPluginResult("error", adError.getErrorCode(), adError.getErrorMsg(), true);
        finish();
    }
}

package cordova.plugins.tencentgdt;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.comm.util.AdError;

import androidx.annotation.Nullable;

public class InterstitialAdFragment extends AdFragment implements UnifiedInterstitialADListener {
    private UnifiedInterstitialAD iad;

    public static InterstitialAdFragment newInstance(String slotId) {
        Bundle bundle = new Bundle();
        bundle.putString("slotId", slotId);

        InterstitialAdFragment fragment = new InterstitialAdFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent);
        Window window = dialog.getWindow();
        if (window != null) {
            Point size = getDisplaySize();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.windowAnimations = android.R.style.Animation_Dialog;
            lp.width = size.x;
            lp.height = size.y;
            lp.gravity = Gravity.CENTER;
            lp.x = 0;
            lp.y = 0;
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME;
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return frameLayout;
    }

    @Override
    protected void onBeforeDestroy() {
        super.onBeforeDestroy();
        if (this.iad != null) {
            iad.close();
            iad.destroy();
            iad = null;
        }
    }

    @Override
    protected void showAd() {
        if (iad == null) {
            iad = new UnifiedInterstitialAD((Activity) mContext, slotId, this);
        }
        iad.loadAD();
    }

    @Override
    public void onADClicked() {
        sendPluginResult("click", true);
    }

    @Override
    public void onADClosed() {
        finish();
    }

    @Override
    public void onADExposure() {
    }

    @Override
    public void onADLeftApplication() {
    }

    @Override
    public void onADOpened() {
    }

    @Override
    public void onADReceive() {
        iad.show();
        sendPluginResult("show", true);
    }

    @Override
    public void onNoAD(AdError adError) {
        sendPluginResult("error", adError.getErrorCode(), adError.getErrorMsg(), true);
        finish();
    }

    @Override
    public void onVideoCached() {
    }
}

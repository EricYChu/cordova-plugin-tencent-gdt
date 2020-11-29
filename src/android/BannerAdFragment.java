package cordova.plugins.tencentgdt;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.comm.util.AdError;

import androidx.annotation.Nullable;

public class BannerAdFragment extends AdFragment {

    private int width;

    private int height;

    private int interval = 30;

    UnifiedBannerView bv;
    ViewGroup bannerContainer;

    public static BannerAdFragment newInstance(String slotId, int width, int height, int interval) {
        Bundle bundle = new Bundle();
        bundle.putString("slotId", slotId);
        bundle.putInt("width", width);
        bundle.putInt("height", height);
        bundle.putInt("interval", interval);

        BannerAdFragment fragment = new BannerAdFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
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
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bannerContainer = new FrameLayout(mContext);
        frameLayout.addView(bannerContainer, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return frameLayout;
    }

    @Override
    protected void showAd() {
        bv = new UnifiedBannerView((Activity) mContext, slotId, new UnifiedBannerADListener() {

            @Override
            public void onADClicked() {
                sendPluginResult("click", true);
            }

            @Override
            public void onADCloseOverlay() {
                Log.i("=====", "onADCloseOverlay");
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
            public void onADOpenOverlay() {
            }

            @Override
            public void onADReceive() {
                sendPluginResult("show", true);
            }

            @Override
            public void onNoAD(AdError adError) {
                sendPluginResult("error", adError.getErrorCode(), adError.getErrorMsg(), true);
                finish();
            }
        });
        bv.setRefresh(interval);
        bannerContainer.addView(bv);
        bv.loadAD();
    }
}

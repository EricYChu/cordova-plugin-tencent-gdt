package cordova.plugins.tencentgdt;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qq.e.ads.rewardvideo2.ExpressRewardVideoAD;
import com.qq.e.ads.rewardvideo2.ExpressRewardVideoAdListener;
import com.qq.e.comm.managers.GDTADManager;
import com.qq.e.comm.util.AdError;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CDVTencentGDT extends CordovaPlugin {

    ExpressRewardVideoAD mRewardVideoAd;

    public CallbackContext splashAdCallbackContext;

    public CallbackContext rewardedVideoAdCallbackContext;

    private BannerAdFragment bannerFragment;

    private RelativeLayout bottomView, contentView;

    private static final int BOTTOM_VIEW_ID = 0x1;


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        final Activity activity = cordova.getActivity();

        String appId = webView.getPreferences().getString("CDVTencentGDTAppId", "");

        GDTADManager.getInstance().initWith(activity, appId);
//        GlobalSetting.setChannel(0);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (args.length() < 0) {
            Toast.makeText(cordova.getActivity(), "ssss", Toast.LENGTH_LONG).show();
            return true;
        }
        JSONObject jsonObject = args.getJSONObject(0);

        final Activity activity = cordova.getActivity();

        // Rewarded Video AD
        if ("showRewardedVideoAd".equals(action)) {
            this.rewardedVideoAdCallbackContext = callbackContext;

            String userId = args.length() <= 1 ? "" : jsonObject.getString("userId");
            String slotId = jsonObject.getString("slotId");

            showRewardedVideoAd(slotId, userId);

            return true;
        }

        // Splash AD
        else if ("showSplashAd".equals(action)) {
            this.splashAdCallbackContext = callbackContext;

            String slotId = jsonObject.getString("slotId");

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    SplashAdFragment fragment = SplashAdFragment.newInstance(slotId, 3);
                    fragment.setCallbackContext(callbackContext);
                    ft.add(fragment, SplashAdFragment.class.getSimpleName());
                    ft.commitAllowingStateLoss();
                }
            });

            return true;
        }

        // Interstitial AD
        else if ("showInterstitialAd".equals(action)) {

            String slotId = jsonObject.getString("slotId");

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    InterstitialAdFragment interstitialFragment = InterstitialAdFragment.newInstance(slotId);
                    interstitialFragment.setCallbackContext(callbackContext);
                    ft.add(interstitialFragment, InterstitialAdFragment.class.getSimpleName());
                    ft.commitAllowingStateLoss();
                }
            });

            return true;
        }

        // Banner AD
        else if ("showBannerAd".equals(action)) {

            final String slotId = jsonObject.getString("slotId");
            final int width = jsonObject.getInt("width");
            final int height = jsonObject.getInt("height");
            final String align = jsonObject.optString("align");
            final int interval = jsonObject.getInt("interval");

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    bottomView = new RelativeLayout(activity);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    if (align.equalsIgnoreCase("top")) {
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    } else {
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    }
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    bottomView.setLayoutParams(params);
                    bottomView.setId(BOTTOM_VIEW_ID);

                    contentView = new RelativeLayout(activity);
                    contentView.addView(bottomView);
                    activity.addContentView(contentView, new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT));

                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    bannerFragment = BannerAdFragment.newInstance(slotId, width, height, interval);
                    bannerFragment.setCallbackContext(callbackContext);
                    ft.replace(BOTTOM_VIEW_ID, bannerFragment);
                    ft.commitAllowingStateLoss();
                }
            });

            return true;
        } else if (action.equals("hideBannerAd")) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bannerFragment != null) {
                        sendPluginResult(bannerFragment.callbackContext, "close", false);
                        FragmentManager fm = activity.getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.remove(bannerFragment);
                        ft.commitAllowingStateLoss();
                    }
                    ViewGroup group = activity.findViewById(android.R.id.content);
                    if (group != null) {
                        group.removeView(contentView);
                    }
                    sendPluginResult(callbackContext, "close", false);
                }
            });
        }

        return false;
    }

    private void sendPluginResult(CallbackContext callbackContext, JSONObject obj, boolean keepCallback) {
        if (callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
            result.setKeepCallback(keepCallback);
            callbackContext.sendPluginResult(result);
        }
    }

    public void sendPluginResult(CallbackContext callbackContext, String type, boolean keepCallback) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendPluginResult(callbackContext, obj, keepCallback);
    }

    public void sendPluginResult(CallbackContext callbackContext, String type, long code, String message, boolean keepCallback) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", type);
            obj.put("code", code);
            obj.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendPluginResult(callbackContext, obj, keepCallback);
    }

    public void showRewardedVideoAd(String slotId, String userId) {
        final Activity activity = cordova.getActivity();

        mRewardVideoAd = new ExpressRewardVideoAD(activity, slotId, new ExpressRewardVideoAdListener() {
            @Override
            public void onAdLoaded() {
                mRewardVideoAd.showAD(null);
            }

            @Override
            public void onVideoCached() {
            }

            @Override
            public void onShow() {
                sendPluginResult(rewardedVideoAdCallbackContext, "show", true);
            }

            @Override
            public void onExpose() {
            }

            @Override
            public void onReward() {
            }

            @Override
            public void onClick() {
                sendPluginResult(rewardedVideoAdCallbackContext, "click", true);
            }

            @Override
            public void onVideoComplete() {
                sendPluginResult(rewardedVideoAdCallbackContext, "play:finish", true);
            }

            @Override
            public void onClose() {
                sendPluginResult(rewardedVideoAdCallbackContext, "close", false);
                rewardedVideoAdCallbackContext = null;
            }

            @Override
            public void onError(AdError adError) {
                sendPluginResult(rewardedVideoAdCallbackContext, "error", adError.getErrorCode(), adError.getErrorMsg(), false);
                rewardedVideoAdCallbackContext = null;
            }
        });

        mRewardVideoAd.setVolumeOn(false);
        mRewardVideoAd.loadAD();
    }
}

#import <Cordova/CDVPlugin.h>
#import "GDTSplashAd.h"
#import "GDTRewardVideoAd.h"
#import "GDTUnifiedInterstitialAd.h"
#import "GDTUnifiedBannerView.h"

@interface CDVTencentGDT : CDVPlugin <GDTSplashAdDelegate, GDTRewardedVideoAdDelegate, GDTUnifiedInterstitialAdDelegate, GDTUnifiedBannerViewDelegate>

@property(nonatomic, strong)CDVInvokedUrlCommand *splashCommand;
@property(nonatomic, strong)CDVInvokedUrlCommand *rewardedVideoCommand;
@property(nonatomic, strong)CDVInvokedUrlCommand *interstitialCommand;
@property(nonatomic, strong)CDVInvokedUrlCommand *bannerCommand;

- (void)showSplashAd:(CDVInvokedUrlCommand*)command;
- (void)showRewardedVideoAd:(CDVInvokedUrlCommand*)command;
- (void)showInterstitialAd:(CDVInvokedUrlCommand*)command;
- (void)showBannerAd:(CDVInvokedUrlCommand*)command;
- (void)hideBannerAd:(CDVInvokedUrlCommand*)command;

@end

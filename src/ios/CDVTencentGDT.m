#import "CDVTencentGDT.h"
#import "GDTSDKConfig.h"

@implementation CDVTencentGDT
{
    GDTSplashAd *_splashAd;
    GDTRewardVideoAd *_rewardedVideoAd;
    GDTUnifiedInterstitialAd *_interstitialAd;
    GDTUnifiedBannerView *_bannerAdView;
}

- (void)pluginInitialize
{
    NSString *appId = [self.commandDelegate.settings objectForKey:[@"CDVTencentGDTAppId" lowercaseString]];

    [GDTSDKConfig registerAppId:appId];
    [GDTSDKConfig enableGPS:YES];
    [GDTSDKConfig enableDefaultAudioSessionSetting:YES];
    //    [GDTSDKConfig setChannel:0];
}

- (void)sendPluginResult:(CDVInvokedUrlCommand *)command
                withType:(NSString*)type
            keepCallback:(BOOL)keepCallback {
    if (command != nil) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsDictionary:@{@"type":type}];
        [pluginResult setKeepCallback:[NSNumber numberWithBool:keepCallback]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void)sendPluginResult:(CDVInvokedUrlCommand *)command
                withType:(NSString*)type
                    code:(NSNumber*)code
                 message:(NSString*)message
            keepCallback:(BOOL)keepCallback {
    if (command != nil) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsDictionary:@{@"type":type,@"code":code,@"message":message}];
        [pluginResult setKeepCallback:[NSNumber numberWithBool:keepCallback]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (UIViewController *)getRootViewControler {
    UIViewController *rootVC = [[UIApplication sharedApplication].delegate window].rootViewController;
    UIViewController *parent = rootVC;

    while ((parent = rootVC.presentedViewController) != nil ) {
        rootVC = parent;
    }

    while ([rootVC isKindOfClass:[UINavigationController class]]) {
        rootVC = [(UINavigationController *)rootVC topViewController];
    }

    return rootVC;
}

#pragma mark - Splash Ad

- (void)showSplashAd:(CDVInvokedUrlCommand*)command
{
    self.splashCommand = command;

    if (_splashAd) {
        _splashAd.delegate = nil;
    }

    NSDictionary* options = [command.arguments objectAtIndex:0];
    NSString *slotId = options[@"slotId"];

    if (slotId) {
        _splashAd = [[GDTSplashAd alloc] initWithPlacementId:slotId];
        _splashAd.delegate = self;
        _splashAd.backgroundColor = [UIColor clearColor];
        _splashAd.fetchDelay = 3;
        [_splashAd loadAd];
    }
}

- (void)removeSplashAd {
    if (_splashAd) {
        _splashAd.delegate = nil;
        _splashAd = nil;
    }
    [self sendPluginResult:self.splashCommand withType:@"close" keepCallback:NO];
    self.splashCommand = nil;
}

#pragma mark GDTSplashAdDelegate

/**
 闪屏广告物料载入成功时调用。
 */
- (void)splashAdDidLoad:(GDTSplashAd *)splashAd {
    UIWindow *window = [[UIApplication sharedApplication] keyWindow];
    [splashAd showAdInWindow:window withBottomView:nil skipView:nil];
}

/**
 闪屏广告展示成功时调用。
 */
- (void)splashAdSuccessPresentScreen:(GDTSplashAd *)splashAd {
    [self sendPluginResult:self.splashCommand withType:@"show" keepCallback:YES];
}

/**
 闪屏广告展示成功时调用。
 */
- (void)splashAdFailToPresent:(GDTSplashAd *)splashAd withError:(NSError *)error {
    NSNumber *code = [NSNumber numberWithLong:error.code];
    NSString *message = error.description;

    [self sendPluginResult:self.splashCommand withType:@"error" code:code message:message keepCallback:YES];
    [self removeSplashAd];
}

/**
 闪屏广告被关闭时调用。
 */
- (void)splashAdClosed:(GDTSplashAd *)splashAd {
    [self removeSplashAd];
}

/**
 闪屏广告被点击时调用。
 */
- (void)splashAdDidClick:(GDTSplashAd *)splashAd {
    [self sendPluginResult:self.splashCommand withType:@"click" keepCallback:YES];
}

#pragma mark - Rewarded Video Ad

- (void)showRewardedVideoAd:(CDVInvokedUrlCommand *)command {
    self.rewardedVideoCommand = command;
    if (_rewardedVideoAd) {
        _rewardedVideoAd.delegate = nil;
    }

    NSDictionary* options = [command.arguments objectAtIndex:0];
    NSString *slotId = options[@"slotId"];

    _rewardedVideoAd = [[GDTRewardVideoAd alloc] initWithPlacementId:slotId];
    _rewardedVideoAd.delegate = self;
    _rewardedVideoAd.videoMuted = YES;
    [_rewardedVideoAd loadAd];
}

- (void)removeRewardedVideoAd {
    if (_rewardedVideoAd) {
        _rewardedVideoAd.delegate = nil;
        _rewardedVideoAd = nil;
    }
    [self sendPluginResult:self.rewardedVideoCommand withType:@"close" keepCallback:NO];
    self.rewardedVideoCommand = nil;
}

#pragma mark - GDTRewardedVideoAdDelegate

/**
 视频广告物料载入成功时调用。
 */
- (void)gdt_rewardVideoAdDidLoad:(GDTRewardVideoAd *)rewardedVideoAd {
    [rewardedVideoAd showAdFromRootViewController:[self getRootViewControler]];
    [self sendPluginResult:self.rewardedVideoCommand withType:@"show" keepCallback:YES];
}

/**
 视频广告各种错误信息回调。
 @param error : 失败原因
 */
- (void)gdt_rewardVideoAd:(GDTRewardVideoAd *)rewardedVideoAd didFailWithError:(NSError *)error {
    NSNumber *code = [NSNumber numberWithLong:error.code];
    NSString *message = error.description;

    [self sendPluginResult:self.rewardedVideoCommand withType:@"error" code:code message:message keepCallback:YES];
    [self removeRewardedVideoAd];
}

/**
 视频广告关闭时调用。
 */
- (void)gdt_rewardVideoAdDidClose:(GDTRewardVideoAd *)rewardedVideoAd {
    [self removeRewardedVideoAd];
}

/**
 视频广告被点击时调用。
 */
- (void)gdt_rewardVideoAdDidClicked:(GDTRewardVideoAd *)rewardedVideoAd {
    [self sendPluginResult:self.rewardedVideoCommand withType:@"click" keepCallback:YES];
}

/**
 视频广告播放完成时调用。
 */
- (void)gdt_rewardVideoAdDidPlayFinish:(GDTRewardVideoAd *)rewardedVideoAd {
    [self sendPluginResult:self.rewardedVideoCommand withType:@"play:finish" keepCallback:YES];
}

#pragma mark - Interstitial Ad

- (void)showInterstitialAd:(CDVInvokedUrlCommand *)command {
    self.interstitialCommand = command;
    if (_interstitialAd) {
        _interstitialAd.delegate = nil;
    }

    NSDictionary* options = [command.arguments objectAtIndex:0];
    NSString *slotId = options[@"slotId"];

    _interstitialAd = [[GDTUnifiedInterstitialAd alloc] initWithPlacementId:slotId];
    _interstitialAd.delegate = self;
    _interstitialAd.videoMuted = YES;
    _interstitialAd.videoAutoPlayOnWWAN = YES;
    [_interstitialAd loadAd];
}

- (void)removeInterstitialAd {
    if (_interstitialAd) {
        _interstitialAd.delegate = nil;
        _interstitialAd = nil;
    }
    [self sendPluginResult:self.interstitialCommand withType:@"close" keepCallback:NO];
    self.interstitialCommand = nil;
}

#pragma mark - GDTUnifiedInterstitialAdDelegate

- (void)unifiedInterstitialSuccessToLoadAd:(GDTUnifiedInterstitialAd *)unifiedInterstitial {
    [_interstitialAd presentAdFromRootViewController:[self getRootViewControler]];
}

- (void)unifiedInterstitialFailToLoadAd:(GDTUnifiedInterstitialAd *)unifiedInterstitial error:(NSError *)error {
    NSNumber *code = [NSNumber numberWithLong:error.code];
    NSString *message = error.description;

    [self sendPluginResult:self.interstitialCommand withType:@"error" code:code message:message keepCallback:YES];
    [self removeInterstitialAd];
}

- (void)unifiedInterstitialFailToPresent:(GDTUnifiedInterstitialAd *)unifiedInterstitial error:(NSError *)error {
    NSNumber *code = [NSNumber numberWithLong:error.code];
    NSString *message = error.description;

    [self sendPluginResult:self.interstitialCommand withType:@"error" code:code message:message keepCallback:YES];
    [self removeInterstitialAd];
}

- (void)unifiedInterstitialDidPresentScreen:(GDTUnifiedInterstitialAd *)unifiedInterstitial {
    [self sendPluginResult:self.interstitialCommand withType:@"show" keepCallback:YES];
}

- (void)unifiedInterstitialClicked:(GDTUnifiedInterstitialAd *)unifiedInterstitial {
    [self sendPluginResult:self.interstitialCommand withType:@"click" keepCallback:YES];
}

- (void)unifiedInterstitialDidDismissScreen:(GDTUnifiedInterstitialAd *)unifiedInterstitial {
    [self removeInterstitialAd];
}

#pragma mark - Banner Ad

- (void)showBannerAd:(CDVInvokedUrlCommand *)command {
    self.interstitialCommand = command;

    [self removeBannerAdView];

    NSDictionary* options = [command.arguments objectAtIndex:0];
    NSString *slotId = options[@"slotId"];
    NSNumber *width = options[@"width"];
    NSNumber *height = options[@"height"];
    NSNumber *interval = options[@"interval"];
    NSString *align = options[@"align"];

    UIWindow *window = nil;
    if ([[UIApplication sharedApplication].delegate respondsToSelector:@selector(window)]) {
        window = [[UIApplication sharedApplication].delegate window];
    }
    if (![window isKindOfClass:[UIView class]]) {
        window = [UIApplication sharedApplication].keyWindow;
    }
    if (!window) {
        window = [[UIApplication sharedApplication].windows objectAtIndex:0];
    }

    CGFloat top = 0.0;
    CGFloat bottom = 0.0;
    if (@available(iOS 11.0, *)) {
        top = window.safeAreaInsets.top;
        bottom = window.safeAreaInsets.bottom;
    }

    NSValue *sizeValue = [NSValue valueWithCGSize:CGSizeMake([width doubleValue], [height doubleValue])];
    CGSize size = [sizeValue CGSizeValue];

    CGFloat screenWidth = CGRectGetWidth([UIScreen mainScreen].bounds);
    CGFloat screenHeight = CGRectGetHeight([UIScreen mainScreen].bounds);


    CGRect rect;
    if ([@"top" isEqualToString:align]) {
        rect = CGRectMake((screenWidth-size.width)/2.0, top, size.width, size.height);
    } else {
        rect = CGRectMake((screenWidth-size.width)/2.0, screenHeight-size.height-bottom, size.width, size.height);
    }
    _bannerAdView = [[GDTUnifiedBannerView alloc] initWithFrame:rect placementId:slotId viewController:[self getRootViewControler]];


    if (interval > 0) {
        _bannerAdView.animated = YES;
        _bannerAdView.autoSwitchInterval = (int)interval;
    }

    _bannerAdView.delegate = self;
    [window addSubview:_bannerAdView];
    [_bannerAdView loadAdAndShow];
}

- (void)hideBannerAd:(CDVInvokedUrlCommand*)command {
    [self removeBannerAd];
}

- (void)removeBannerAdView {
    if (_bannerAdView) {
        _bannerAdView.delegate = nil;
        if (_bannerAdView.superview) {
            [_bannerAdView removeFromSuperview];
        }
        _bannerAdView = nil;
    }
}

- (void)removeBannerAd {
    [self removeBannerAdView];
    [self sendPluginResult:self.bannerCommand withType:@"close" keepCallback:NO];
    self.bannerCommand = nil;
}

#pragma mark - GDTUnifiedBannerViewDelegate

- (void)nativeExpressBannerAdView:(GDTUnifiedBannerView *)bannerAdView didLoadFailWithError:(NSError *)error {
    NSNumber *code = [NSNumber numberWithLong:error.code];
    NSString *message = error.description;

    [self sendPluginResult:self.bannerCommand withType:@"error" code:code message:message keepCallback:YES];
    [self removeBannerAd];
}

- (void)unifiedBannerViewFailedToLoad:(GDTUnifiedBannerView *)bannerAdView error:(NSError *)error {
    NSNumber *code = [NSNumber numberWithLong:error.code];
    NSString *message = error.description;

    [self sendPluginResult:self.bannerCommand withType:@"error" code:code message:message keepCallback:YES];
    [self removeBannerAd];
}

- (void)unifiedBannerViewWillExpose:(GDTUnifiedBannerView *)bannerAdView {
    [self sendPluginResult:self.interstitialCommand withType:@"show" keepCallback:YES];
}

- (void)unifiedBannerViewClicked:(GDTUnifiedBannerView *)bannerAdView {
    [self sendPluginResult:self.interstitialCommand withType:@"click" keepCallback:YES];
}

- (void)unifiedBannerViewWillClose:(GDTUnifiedBannerView *)unifiedBannerView {
    [self removeBannerAd];
}

@end

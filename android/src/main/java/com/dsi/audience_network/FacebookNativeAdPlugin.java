package com.dsi.audience_network;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.NativeBannerAdView;

import java.util.HashMap;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;



import android.app.Activity;
import android.widget.TextView;
import com.facebook.ads.AdOptionsView;
import android.widget.RelativeLayout;
import android.view.LayoutInflater;


import android.widget.TextView;
// import android.graphics.drawable.ShapeDrawable;
// import android.graphics.drawable.shapes.RectShape;
// import android.graphics.Paint.Style;
import com.facebook.ads.AdOptionsView;
import android.widget.RelativeLayout;
import android.view.LayoutInflater;
import android.widget.Button;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import java.util.List;
import java.util.ArrayList;
import android.R.id;
import android.R.layout;


class FacebookNativeAdPlugin extends PlatformViewFactory {

    private final BinaryMessenger messenger;

    FacebookNativeAdPlugin(BinaryMessenger messenger) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
    }


    @Override
    public PlatformView create(Context context, int id, Object args) {
        //setContentView(R.layout.fb_native_ad_container);
        return new FacebookNativeAdView(context, id, (HashMap) args, this.messenger);
    }
}

class FacebookNativeAdView implements PlatformView, NativeAdListener {

    private LinearLayout adView;
    private RelativeLayout _adView;

    private final MethodChannel channel;
    private final HashMap args;
    private boolean loaderror = false;
    private final Context context;
    private NativeAdLayout nativeAdLayout;
    private NativeAd nativeAd;
    private NativeBannerAd bannerAd;
    private boolean isbanner = false;

    FacebookNativeAdView(Context context, int id, HashMap args, BinaryMessenger messenger) {

        adView = new LinearLayout(context);
        nativeAdLayout = new NativeAdLayout(context);
        this.channel = new MethodChannel(messenger,
                FacebookConstants.NATIVE_AD_CHANNEL + "_" + id);

        this.args = args;
        this.context = context;

        if ((boolean) args.get("banner_ad")) {
            // bannerAd = new NativeBannerAd(context, (String) this.args.get("id"));
            // NativeAdBase.NativeLoadAdConfig loadAdConfig = bannerAd.buildLoadAdConfig().withAdListener(this).withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL).build();

            isbanner = true;
            bannerAd = new NativeBannerAd(context, (String) this.args.get("id"));
            bannerAd.loadAd(
                bannerAd.buildLoadAdConfig()
                        .withAdListener(this).withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
                        .build());
            
            // bannerAd.loadAd(loadAdConfig);
        } else {
            nativeAd = new NativeAd(context, (String) this.args.get("id"));
            //NativeAdBase.NativeLoadAdConfig loadAdConfig = nativeAd.buildLoadAdConfig().withAdListener(this).withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL).build();
            nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                        .withAdListener(this).withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
                        .build());
            // nativeAd.loadAd(loadAdConfig);
        }

        // if((boolean) args.get("ismainbanner")){
        //     refresher();
        // }

        // if (args.get("bg_color") != null)
        //     adView.setBackgroundColor(Color.parseColor((String) args.get("bg_color")));
    }

    private NativeAdViewAttributes getViewAttributes(Context context, HashMap args) {
        NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes(context);

        if (args.get("bg_color") != null)
            viewAttributes.
                    setBackgroundColor(Color.parseColor((String) args.get("bg_color")));
        if (args.get("title_color") != null)
            viewAttributes.
                    setTitleTextColor(Color.parseColor((String) args.get("title_color")));
        if (args.get("desc_color") != null)
            viewAttributes.
                    setDescriptionTextColor(Color.parseColor((String) args.get("desc_color")));
        if (args.get("button_color") != null)
            viewAttributes.
                    setButtonColor(Color.parseColor((String) args.get("button_color")));
        if (args.get("button_title_color") != null)
            viewAttributes.
                    setButtonTextColor(Color.parseColor((String) args.get("button_title_color")));
        if (args.get("button_border_color") != null)
            viewAttributes.
                    setButtonBorderColor(Color.parseColor((String) args.get("button_border_color")));

        return viewAttributes;
    }

    private NativeBannerAdView.Type getBannerSize(HashMap args) {
        final int height = (int) args.get("height");

        switch (height) {
            case 50:
                return NativeBannerAdView.Type.HEIGHT_50;
            case 100:
                return NativeBannerAdView.Type.HEIGHT_100;
            case 120:
                return NativeBannerAdView.Type.HEIGHT_120;
            default:
                return NativeBannerAdView.Type.HEIGHT_120;
        }
    }

    private void loadit(){
        if(loaderror){
            if (isbanner) {
                bannerAd = new NativeBannerAd(context, (String) this.args.get("id"));
                bannerAd.loadAd(
                    bannerAd.buildLoadAdConfig()
                            .withAdListener(this).withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
                            .build());
            } else {
                nativeAd = new NativeAd(context, (String) this.args.get("id"));
                NativeAdBase.NativeLoadAdConfig loadAdConfig = nativeAd.buildLoadAdConfig().withAdListener(this).withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL).build();
                nativeAd.loadAd(loadAdConfig);
            }
        }
    }

    @Override
    public View getView() {
        System.out.println("......................... getting ad");
        return nativeAdLayout;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onError(Ad ad, AdError adError) {
        loaderror = true;
        HashMap<String, Object> _args = new HashMap<>();
        _args.put("placement_id", ad.getPlacementId());
        _args.put("invalidated", ad.isAdInvalidated());
        _args.put("error_code", adError.getErrorCode());
        _args.put("error_message", adError.getErrorMessage());
        System.out.println("......................... error loading");
        System.out.println("......................... error is "+adError.getErrorMessage());
        System.out.println("......................... error code is "+adError.getErrorCode());

        channel.invokeMethod(FacebookConstants.ERROR_METHOD, _args);
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
    new Runnable() {
        public void run() {
            
            System.out.println("This'll run 5 seconds later");
            loadit();

        }
    }, 
5000);
    }

    

    @Override
    public void onAdLoaded(Ad ad) {
        loaderror = false;
        HashMap<String, Object> args = new HashMap<>();
        args.put("placement_id", ad.getPlacementId());
        args.put("invalidated", ad.isAdInvalidated());
        System.out.println("......................... AD LOADED isBanner"+isbanner);
        System.out.println("......................... IS INVALIDATED "+ad.isAdInvalidated());
        channel.invokeMethod(FacebookConstants.LOAD_SUCCESS_METHOD, args);
            // adView.postDelayed(new Runnable() {
            //     @Override
            //     public void run() {
            //         showNativeAd();
            //     }
            // }, 200);
            if (isbanner) {
                System.out.println("......................... is banner");
                if (nativeAdLayout.getChildCount() > 0){
                    System.out.println("......................... > 0");
                    nativeAdLayout.removeAllViews();}
                System.out.println("......................... inflating");
                inflateAdBanner(bannerAd);
                
            
            } else {
               // inflateAdNative(nativeAd);
               System.out.println("......................... is banner");
               if (nativeAdLayout.getChildCount() > 0){
                   System.out.println("......................... > 0");
                   nativeAdLayout.removeAllViews();}
               System.out.println("......................... inflating");
               inflateAdNative(nativeAd);
            }
       
        
        
    }

    private void inflateAdBanner(NativeBannerAd nativeBannerAd) {
        // Unregister last ad
        nativeBannerAd.unregisterView();

        // Add the Ad view into the ad container.
        //nativeAdLayout = findViewById(R.id.native_ad_container);
        // RelativeLayout _adView = (RelativeLayout) LayoutInflater.from(this.context).inflate(R.layout.fb_native_ad_container, null);
        // nativeAdLayout = _adView.findViewById(R.id.native_ad_container);
         nativeAdLayout = (NativeAdLayout) LayoutInflater.from(this.context).inflate(R.layout.fb_native_ad_container, null);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.fb_native_banner_ad, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(this.context, nativeBannerAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        MediaView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        System.out.println("......................... ad title is ");
        System.out.println("......................... "+nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        if (args.get("bg_color") != null)
        adView.setBackgroundColor(Color.parseColor((String) args.get("bg_color")));
    if (args.get("title_color") != null)
    nativeAdTitle.setTextColor(Color.parseColor((String) args.get("title_color")));
    if (args.get("desc_color") != null)
    nativeAdSocialContext.setTextColor(Color.parseColor((String) args.get("desc_color")));
    
    if (args.get("button_title_color") != null)
    nativeAdCallToAction.setTextColor(Color.parseColor((String) args.get("button_title_color")));
    if (args.get("button_color") != null){
    nativeAdCallToAction.setBackgroundColor(Color.parseColor((String) args.get("button_color")));}



        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        clickableViews.add(nativeAdSocialContext);
        clickableViews.add(nativeAdIconView);
        nativeBannerAd.registerViewForInteraction(nativeAdLayout, nativeAdIconView, clickableViews);
        //showNativeAd();
        System.out.println("......................... let see");
        
       channel.invokeMethod(FacebookConstants.LOADED_METHOD, args);
        
      
    }


    private void inflateAdNative(NativeAd _nativeAd) {
        // Unregister last ad
        _nativeAd.unregisterView();
        boolean ishorizontal = false;
        // Add the Ad view into the ad container.
        //nativeAdLayout = findViewById(R.id.native_ad_container);
        // RelativeLayout _adView = (RelativeLayout) LayoutInflater.from(this.context).inflate(R.layout.fb_native_ad_container, null);
        // nativeAdLayout = _adView.findViewById(R.id.native_ad_container);
         nativeAdLayout = (NativeAdLayout) LayoutInflater.from(this.context).inflate(R.layout.fb_native_ad_container, null);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        if (args.get("ishorizontal") != null){
               if((boolean)args.get("ishorizontal") == true){
                ishorizontal = true;
                adView = (LinearLayout) inflater.inflate(R.layout.fb_native_ad_layout_horizontal, nativeAdLayout, false);
                nativeAdLayout.addView(adView);
               }
               else{
                adView = (LinearLayout) inflater.inflate(R.layout.fb_native_ad_layout_vertical, nativeAdLayout, false);
                nativeAdLayout.addView(adView);
               }
        }
       

        // Add the AdOptionsView
    LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
    AdOptionsView adOptionsView = new AdOptionsView(this.context, _nativeAd, nativeAdLayout);
    adChoicesContainer.removeAllViews();
    adChoicesContainer.addView(adOptionsView, 0);

    // Create native UI using the ad metadata.
    MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
    TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
    MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
    TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
    TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
    TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
    Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
    
    // Set the Text.
    nativeAdTitle.setText(_nativeAd.getAdvertiserName());
    System.out.println("......................... ad title is ");
    System.out.println("......................... "+_nativeAd.getAdvertiserName());
    nativeAdBody.setText(_nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(_nativeAd.getAdSocialContext());
    
    
    nativeAdCallToAction.setVisibility(_nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
    nativeAdCallToAction.setText(_nativeAd.getAdCallToAction());
    sponsoredLabel.setText(_nativeAd.getSponsoredTranslation());


    if (args.get("bg_color") != null)
    adView.setBackgroundColor(Color.parseColor((String) args.get("bg_color")));
if (args.get("title_color") != null)
nativeAdTitle.setTextColor(Color.parseColor((String) args.get("title_color")));
if (args.get("desc_color") != null)
nativeAdBody.setTextColor(Color.parseColor((String) args.get("desc_color")));
nativeAdSocialContext.setTextColor(Color.parseColor((String) args.get("desc_color")));

if (args.get("button_title_color") != null)
nativeAdCallToAction.setTextColor(Color.parseColor((String) args.get("button_title_color")));
// if (args.get("button_border_color") != null){
//  ShapeDrawable shapedrawable = new ShapeDrawable();
//                 shapedrawable.setShape(new RectShape());
//                 shapedrawable.getPaint().setColor(Color.parseColor((String) args.get("button_border_color")));
//                 shapedrawable.getPaint().setStrokeWidth(6f);
//                 shapedrawable.getPaint().setStyle(Style.STROKE);     
//                 nativeAdCallToAction.setBackground(shapedrawable);
// }
if (args.get("button_color") != null){
nativeAdCallToAction.setBackgroundColor(Color.parseColor((String) args.get("button_color")));}



      

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        clickableViews.add(nativeAdBody);
        clickableViews.add(nativeAdSocialContext);
        clickableViews.add(nativeAdMedia);
        clickableViews.add(nativeAdIcon);
        _nativeAd.registerViewForInteraction(nativeAdLayout, nativeAdMedia, nativeAdIcon, clickableViews);
        //showNativeAd();
        System.out.println("......................... let see");
        
       channel.invokeMethod(FacebookConstants.LOADED_METHOD, args);
        
      
    }

    private void showNativeAd() {
        if (adView.getChildCount() > 0)
            adView.removeAllViews();

        if ((boolean) this.args.get("banner_ad")) {
            adView.addView(NativeBannerAdView.render(this.context,
                    this.bannerAd,
                    getBannerSize(this.args),
                    getViewAttributes(this.context, this.args)));
        } else {
            //View view = inflateView();
            // adView.addView(view);
            adView.addView(NativeAdView.render(this.context,
                    this.nativeAd,
                    getViewAttributes(this.context, this.args)));
        }

        channel.invokeMethod(FacebookConstants.LOADED_METHOD, args);
    }

    @Override
    public void onAdClicked(Ad ad) {
        HashMap<String, Object> args = new HashMap<>();
        args.put("placement_id", ad.getPlacementId());
        args.put("invalidated", ad.isAdInvalidated());
        System.out.println("......................... CLICK");
        channel.invokeMethod(FacebookConstants.CLICKED_METHOD, args);
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        HashMap<String, Object> args = new HashMap<>();
        args.put("placement_id", ad.getPlacementId());
        args.put("invalidated", ad.isAdInvalidated());
        System.out.println("......................... IMPRESSION isBanner"+isbanner);
        channel.invokeMethod(FacebookConstants.LOGGING_IMPRESSION_METHOD, args);
    }

    @Override
    public void onMediaDownloaded(Ad ad) {
        HashMap<String, Object> args = new HashMap<>();
        args.put("placement_id", ad.getPlacementId());
        args.put("invalidated", ad.isAdInvalidated());
        System.out.println("......................... MEDIA DOWNLOADED");
        channel.invokeMethod(FacebookConstants.MEDIA_DOWNLOADED_METHOD, args);
    }
}

package com.reactnativejitsimeet;

import android.util.Log;
import java.net.URL;
import java.net.MalformedURLException;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;


@ReactModule(name = RNJitsiMeetModule.MODULE_NAME)
public class RNJitsiMeetModule extends ReactContextBaseJavaModule {
    public static final String MODULE_NAME = "RNJitsiMeetModule";
    private IRNJitsiMeetViewReference mJitsiMeetViewReference;
    private ReactApplicationContext mReactContext;

    public RNJitsiMeetModule(ReactApplicationContext reactContext, IRNJitsiMeetViewReference jitsiMeetViewReference) {
        super(reactContext);
        mJitsiMeetViewReference = jitsiMeetViewReference;
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @ReactMethod
    public void initialize() {
        Log.d("JitsiMeet", "Initialize is deprecated in v2");
    }

    @ReactMethod
    public void call(String url, ReadableMap userInfo, ReadableMap featureFlags) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mJitsiMeetViewReference.getJitsiMeetView() != null) {
                    RNJitsiMeetUserInfo _userInfo = new RNJitsiMeetUserInfo();
                    if (userInfo != null) {
                        if (userInfo.hasKey("displayName")) {
                            _userInfo.setDisplayName(userInfo.getString("displayName"));
                          }
                          if (userInfo.hasKey("email")) {
                            _userInfo.setEmail(userInfo.getString("email"));
                          }
                          if (userInfo.hasKey("avatar")) {
                            String avatarURL = userInfo.getString("avatar");
                            try {
                                _userInfo.setAvatar(new URL(avatarURL));
                            } catch (MalformedURLException e) {
                            }
                          }
                    }
                    RNJitsiMeetConferenceOptions.Builder optionsBuilder = new RNJitsiMeetConferenceOptions.Builder()
                            .setRoom(url)
                            .setAudioOnly(false)
                            .setUserInfo(_userInfo);

                    ReadableMapKeySetIterator iterator = featureFlags.keySetIterator();
                    while (iterator.hasNextKey()) {
                        String key = iterator.nextKey();
                        optionsBuilder.setFeatureFlag(key, featureFlags.getBoolean(key));
                    }
                    RNJitsiMeetConferenceOptions options = optionsBuilder.build();

                    mJitsiMeetViewReference.getJitsiMeetView().join(options);
                }
            }
        });
    }

    @ReactMethod
    public void audioCall(String url, ReadableMap userInfo, ReadableMap featureFlags) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mJitsiMeetViewReference.getJitsiMeetView() != null) {
                    RNJitsiMeetUserInfo _userInfo = new RNJitsiMeetUserInfo();
                    if (userInfo != null) {
                        if (userInfo.hasKey("displayName")) {
                            _userInfo.setDisplayName(userInfo.getString("displayName"));
                          }
                          if (userInfo.hasKey("email")) {
                            _userInfo.setEmail(userInfo.getString("email"));
                          }
                          if (userInfo.hasKey("avatar")) {
                            String avatarURL = userInfo.getString("avatar");
                            try {
                                _userInfo.setAvatar(new URL(avatarURL));
                            } catch (MalformedURLException e) {
                            }
                          }
                    }
                    RNJitsiMeetConferenceOptions.Builder optionsBuilder = new RNJitsiMeetConferenceOptions.Builder()
                            .setRoom(url)
                            .setAudioOnly(true)
                            .setUserInfo(_userInfo);

                    ReadableMapKeySetIterator iterator = featureFlags.keySetIterator();
                    while (iterator.hasNextKey()) {
                        String key = iterator.nextKey();
                        optionsBuilder.setFeatureFlag(key, featureFlags.getBoolean(key));
                    }
                    RNJitsiMeetConferenceOptions options = optionsBuilder.build();

                    mJitsiMeetViewReference.getJitsiMeetView().join(options);
                }
            }
        });
    }

    @ReactMethod
    public void endCall() {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mJitsiMeetViewReference.getJitsiMeetView() != null) {
                    mJitsiMeetViewReference.getJitsiMeetView().leave();
                }
            }
        });
    }
    
    @ReactMethod
    public void setAudioMuted(Boolean isMuted) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mJitsiMeetViewReference.getJitsiMeetView() != null) {
                    mReactContext.getJSModule(RCTEventEmitter.class).sendBroadcast(
                    "SET_AUDIO_MUTED",
                    isMuted);
                }
            }
        });
    }
    
    @ReactMethod
    public void setVideoMuted(Boolean isMuted) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mJitsiMeetViewReference.getJitsiMeetView() != null) {
                    mReactContext.getJSModule(RCTEventEmitter.class).sendBroadcast(
                    "SET_VIDEO_MUTED",
                    isMuted);
                }
            }
        });
    }
}
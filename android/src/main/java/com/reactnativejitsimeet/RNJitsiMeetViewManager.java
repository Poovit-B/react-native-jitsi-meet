package com.reactnativejitsimeet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.JitsiMeetViewListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import timber.log.Timber;

import static org.webrtc.ContextUtils.getApplicationContext;


public class RNJitsiMeetViewManager extends SimpleViewManager<RNJitsiMeetView>  {

  public static final String REACT_CLASS = "JitsiView";
  private IRNJitsiMeetViewReference mJitsiMeetViewReference;
  public String jitsiServerUrl ="https://meet.jit.si";
  private ReactApplicationContext mReactContext;

  public RNJitsiMeetViewManager(ReactApplicationContext reactContext, IRNJitsiMeetViewReference jitsiMeetViewReference) {
    mJitsiMeetViewReference = jitsiMeetViewReference;
    mReactContext = reactContext;
  }

  private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      onBroadcastReceived(intent);
    }
  };

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  protected RNJitsiMeetView createViewInstance(ThemedReactContext reactContext) {

    if(mJitsiMeetViewReference.getJitsiMeetView() == null){
      RNJitsiMeetView view = new RNJitsiMeetView(reactContext.getCurrentActivity());
      mJitsiMeetViewReference.setJitsiMeetView(view);
    }
    registerForBroadcastMessages();
    return mJitsiMeetViewReference.getJitsiMeetView();
  }

  private void registerForBroadcastMessages() {
    IntentFilter intentFilter = new IntentFilter();

        /* This registers for every possible event sent from JitsiMeetSDK
           If only some of the events are needed, the for loop can be replaced
           with individual statements:
           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
                ... other events
         */
    for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
      intentFilter.addAction(type.getAction());
    }

    LocalBroadcastManager.getInstance(mJitsiMeetViewReference.getJitsiMeetView().getContext()).registerReceiver(broadcastReceiver, intentFilter);
  }

  // Example for handling different JitsiMeetSDK events
  private void onBroadcastReceived(Intent intent) {
    if (intent != null) {

      BroadcastEvent event = new BroadcastEvent(intent);
      WritableMap eventMap = Arguments.createMap();

      switch (event.getType()) {
        case CONFERENCE_JOINED:
          Timber.i("Conference Joined with url%s", event.getData().get("url"));
          eventMap = Arguments.createMap();
          eventMap.putString("url", (String) event.getData().get("url"));
          eventMap.putString("error", (String) event.getData().get("error"));
          mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
            mJitsiMeetViewReference.getJitsiMeetView().getId(),
            "conferenceJoined",
            eventMap);
          break;

        case CONFERENCE_TERMINATED:
          Timber.i("TERMINATED TERMINATED%s", event.getData().get("url"));
          eventMap = Arguments.createMap();
          eventMap.putString("url", (String) event.getData().get("url"));
          eventMap.putString("error", (String) event.getData().get("error"));
          mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
            mJitsiMeetViewReference.getJitsiMeetView().getId(),
            "conferenceTerminated",
            eventMap);

          Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
          LocalBroadcastManager.getInstance(mJitsiMeetViewReference.getJitsiMeetView().getContext()).sendBroadcast(hangupBroadcastIntent);

          mJitsiMeetViewReference.getJitsiMeetView().dispose();
          break;

        case CONFERENCE_WILL_JOIN:
          Timber.i("CONFERENCE_WILL_JOIND%s", event.getData().get("url"));
          eventMap = Arguments.createMap();
          eventMap.putString("url", (String) event.getData().get("url"));
          eventMap.putString("error", (String) event.getData().get("error"));
          mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
            mJitsiMeetViewReference.getJitsiMeetView().getId(),
            "conferenceWillJoin",
            eventMap);
          break;

        case AUDIO_MUTED_CHANGED:
          Timber.i("AUDIO_MUTED_CHANGED%s", event.getData().get("muted"));
          eventMap = Arguments.createMap();
          eventMap.putString("muted", (String) event.getData().get("muted"));
          eventMap.putString("error", (String) event.getData().get("error"));
          mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
            mJitsiMeetViewReference.getJitsiMeetView().getId(),
            "audioMuted",
            eventMap);
          break;

        case VIDEO_MUTED_CHANGED:
          Timber.i("VIDEO_MUTED_CHANGED%s", event.getData().get("muted"));
          eventMap = Arguments.createMap();
          eventMap.putString("muted", (String) event.getData().get("muted"));
          eventMap.putString("error", (String) event.getData().get("error"));
          mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
            mJitsiMeetViewReference.getJitsiMeetView().getId(),
            "videoMuted",
            eventMap);
          break;

      }
    }
  }

  @Override
  public Map getExportedCustomBubblingEventTypeConstants() {
    return MapBuilder.builder()
      .put("conferenceJoined", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onConferenceJoined")))
      .put("conferenceTerminated", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onConferenceTerminated")))
      .put("conferenceWillJoin", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onConferenceWillJoin")))
      .put("audioMuted", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onAudioMutedChanged")))
      .put("videoMuted", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onVideoMutedChanged")))
      .build();
  }
}
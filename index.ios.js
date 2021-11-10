/**
 * @providesModule JitsiMeet
 */

import { NativeModules, requireNativeComponent } from 'react-native';

export const JitsiMeetView = requireNativeComponent('RNJitsiMeetView');
export const JitsiMeetModule = NativeModules.RNJitsiMeetView;
const call = JitsiMeetModule.call;
const audioCall = JitsiMeetModule.audioCall;
const endCall = JitsiMeetModule.endCall;
const setAudioMuted = JitsiMeetModule.setAudioMuted;
const setVideoMuted = JitsiMeetModule.setVideoMuted;

JitsiMeetModule.call = (url, userInfo, featureFlags) => {
  userInfo = userInfo || {};
  featureFlags = featureFlags || {};
  call(url, userInfo, featureFlags);
}
JitsiMeetModule.audioCall = (url, userInfo, featureFlags) => {
  userInfo = userInfo || {};
  featureFlags = featureFlags || {};
  audioCall(url, userInfo, featureFlags);
}
JitsiMeetModule.setAudioMuted = (isMuted) => {
  isMuted = Boolean(isMuted) || false;
  setAudioMuted(isMuted);
}
JitsiMeetModule.setVideoMuted = (isMuted) => {
  isMuted = Boolean(isMuted) || false;
  setVideoMuted(isMuted);
}
JitsiMeetModule.endCall = () => {
  endCall();
}

export default JitsiMeetModule;



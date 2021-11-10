/**
 * @providesModule JitsiMeet
 */

import { NativeModules, requireNativeComponent } from 'react-native';

export const JitsiMeetView = requireNativeComponent('RNJitsiMeetView');
export const JitsiMeetModule = NativeModules.RNJitsiMeetModule
const call = JitsiMeetModule.call;
const audioCall = JitsiMeetModule.audioCall;
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
export default JitsiMeetModule;


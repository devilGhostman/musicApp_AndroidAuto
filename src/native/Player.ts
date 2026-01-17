import { NativeModules } from 'react-native';

const { PlayerModule } = NativeModules;
console.log('NativeModules keys:', Object.keys(NativeModules));

export default {
  play: (track: any) => PlayerModule.play(track),
  pause: () => PlayerModule.pause(),
  resume: () => PlayerModule.resume(),
};

import { NativeModules } from 'react-native';

const { PlayerModule } = NativeModules;

export default {
  play: (track: any) => PlayerModule.play(track),
  pause: () => PlayerModule.pause(),
  resume: () => PlayerModule.resume(),
  updateTracks: (tracks: any) => PlayerModule.updateTracks(tracks),
};

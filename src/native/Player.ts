import { NativeModules, NativeEventEmitter } from 'react-native';

const { PlayerModule } = NativeModules;
const playerEmitter = new NativeEventEmitter(PlayerModule);

const Player = {
  play: (track: any) => PlayerModule.play(track),
  pause: () => PlayerModule.pause(),
  resume: () => PlayerModule.resume(),
  next: () => PlayerModule.next?.(),
  previous: () => PlayerModule.previous?.(),
  updateTracks: (tracks: any) => PlayerModule.updateTracks(tracks),

  emitter: playerEmitter,
};

export default Player;

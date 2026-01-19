import React, { useEffect } from 'react';
import { View, Text, FlatList, TouchableOpacity } from 'react-native';
import Player from '../native/Player';

const TRACKS = [
  {
    id: '1',
    title: 'Rakita',
    artist: 'Demo Artist',
    radioUrl:
      ' https://playerservices.streamtheworld.com/api/livestream-redirect/RAKITAAAC.aac',
  },
  {
    id: '2',
    title: 'Cats',
    artist: 'Demo Artist',
    radioUrl: 'https://stream.rcs.revma.com/ggz4zeuv0y3vv',
  },
  {
    id: '3',
    title: 'Manis',
    artist: 'Demo Artist',
    radioUrl: 'https://stream.rcs.revma.com/nzgauqq1v7zuv',
  },
  {
    id: '4',
    title: 'Rakita',
    artist: 'Demo Artist',
    radioUrl:
      ' https://playerservices.streamtheworld.com/api/livestream-redirect/RAKITAAAC.aac',
  },
  {
    id: '5',
    title: 'Cats',
    artist: 'Demo Artist',
    radioUrl: 'https://stream.rcs.revma.com/ggz4zeuv0y3vv',
  },
  {
    id: '6',
    title: 'Manis',
    artist: 'Demo Artist',
    radioUrl: 'https://stream.rcs.revma.com/nzgauqq1v7zuv',
  },
];

export default function HomeScreen() {
  useEffect(() => {
    Player.updateTracks(TRACKS);
  }, []);

  return (
    <FlatList
      data={TRACKS}
      keyExtractor={item => item.id}
      renderItem={({ item }) => (
        <TouchableOpacity
          style={{ backgroundColor: 'lightgray', marginVertical: 10 }}
          onPress={() => {
            console.log("item::",item);
            Player.play(item);
          }}
        >
          <Text style={{ padding: 20 }}>{item.title}</Text>
        </TouchableOpacity>
      )}
    />
  );
}

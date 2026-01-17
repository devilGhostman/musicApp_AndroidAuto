import React from 'react';
import { View, Text, FlatList, TouchableOpacity } from 'react-native';
import Player from '../native/Player';

const TRACKS = [
  {
    id: '1',
    title: 'Sample AAC Track',
    artist: 'Demo Artist',
    url: 'https://stream.rcs.revma.com/ggz4zeuv0y3vv',
  },
];

export default function HomeScreen() {
  return (
    <FlatList
      data={TRACKS}
      keyExtractor={item => item.id}
      renderItem={({ item }) => (
        <TouchableOpacity
        style={{ backgroundColor: 'lightgray' }}
          onPress={() => {
            Player.play(item);
          }}
        >
          <Text style={{ padding: 20 }}>{item.title}</Text>
        </TouchableOpacity>
      )}
    />
  );
}

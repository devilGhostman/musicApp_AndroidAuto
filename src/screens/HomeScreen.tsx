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
    thumbnailUrl:
      'https://cdn6.mogiio.com/661384178820be00085a7121/2025/10/02/09-27-30/thumbnail/383.png',
  },
  {
    id: '2',
    title: 'Cats',
    artist: 'Demo Artist',
    radioUrl: 'https://stream.rcs.revma.com/ggz4zeuv0y3vv',
    thumbnailUrl :'https://cdn6.mogiio.com/661384178820be00085a7121/2025/10/02/09-27-30/thumbnail/383.png'

  },
  {
    id: '3',
    title: 'Manis',
    artist: 'Demo Artist',
    radioUrl: 'https://stream.rcs.revma.com/nzgauqq1v7zuv',
    thumbnailUrl :'https://cdn6.mogiio.com/661384178820be00085a7121/2025/10/02/09-27-30/thumbnail/383.png'

  },
  {
    id: '4',
    title: 'Rakita',
    artist: 'Demo Artist',
    radioUrl:
      ' https://playerservices.streamtheworld.com/api/livestream-redirect/RAKITAAAC.aac',
    thumbnailUrl :'https://cdn6.mogiio.com/661384178820be00085a7121/2025/10/02/09-27-30/thumbnail/383.png'

  },
  {
    id: '5',
    title: 'Cats',
    artist: 'Demo Artist',
    radioUrl: 'https://stream.rcs.revma.com/ggz4zeuv0y3vv',
    thumbnailUrl :'https://cdn6.mogiio.com/661384178820be00085a7121/2025/10/02/09-27-30/thumbnail/383.png'

  },
  {
    id: '6',
    title: 'Manis',
    artist: 'Demo Artist',
    radioUrl: 'https://stream.rcs.revma.com/nzgauqq1v7zuv',
    thumbnailUrl :'https://cdn6.mogiio.com/661384178820be00085a7121/2025/10/02/09-27-30/thumbnail/383.png'

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
            console.log('item::', item);
            Player.play(item);
          }}
        >
          <Text style={{ padding: 20 }}>{item.title}</Text>
        </TouchableOpacity>
      )}
    />
  );
}

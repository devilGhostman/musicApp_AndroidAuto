package com.mymusicapp.player

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes

class PlaybackManager(context: Context) {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()

    fun play(url: String) {
        
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMimeType(MimeTypes.AUDIO_AAC)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun resume() {
        player.play()
    }

    fun release() {
        player.release()
    }
}

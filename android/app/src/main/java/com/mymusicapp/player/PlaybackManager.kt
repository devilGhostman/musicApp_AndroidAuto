package com.mymusicapp.player

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MimeTypes

class PlaybackManager(context: Context) {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)                 
            .setContentType(C.CONTENT_TYPE_MUSIC)    
            .build()

        setAudioAttributes(audioAttributes, true)
    }

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

    fun getCurrentPosition(): Long = player.currentPosition
}

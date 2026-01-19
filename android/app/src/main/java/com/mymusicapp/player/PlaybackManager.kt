package com.mymusicapp.player

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager


class PlaybackManager(context: Context) {

    private val player = ExoPlayer.Builder(context).build()
    private val audioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val audioFocusRequest: AudioFocusRequest =
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setOnAudioFocusChangeListener { focus ->
                when (focus) {
                    AudioManager.AUDIOFOCUS_LOSS,
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                        player.pause()
                    }
                }
            }
            .build()

    fun play(url: String) {
        val result = audioManager.requestAudioFocus(audioFocusRequest)
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return
        }

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
        audioManager.abandonAudioFocusRequest(audioFocusRequest)
        player.release()
}


    fun getCurrentPosition(): Long {
        return player.currentPosition
    }
}

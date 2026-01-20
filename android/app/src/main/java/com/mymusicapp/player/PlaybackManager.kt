package com.mymusicapp.player

import android.content.Context
import androidx.media3.common.*
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.extractor.metadata.icy.IcyInfo

class PlaybackManager(
    context: Context,
    private val onMetadata: (title: String?, artist: String?) -> Unit
) {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()

        player.setAudioAttributes(audioAttributes, true)

        player.addListener(object : Player.Listener {

            override fun onMetadata(metadata: Metadata) {
                for (i in 0 until metadata.length()) {
                    val entry = metadata[i]

                    if (entry is IcyInfo) {
                        val streamTitle = entry.title ?: continue

                        // Common format: "Artist - Song title"
                        val parts = streamTitle.split(" - ", limit = 2)

                        val artist = parts.getOrNull(0)
                        val title = parts.getOrNull(1)

                        onMetadata(title, artist)
                    }
                }
            }
        })
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

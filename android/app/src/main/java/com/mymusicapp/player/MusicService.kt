package com.mymusicapp.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.mymusicapp.model.Track
import org.json.JSONArray

class MusicService : MediaBrowserServiceCompat() {

    companion object {
        const val ACTION_PLAY = "com.mymusicapp.PLAY"
        const val ACTION_PAUSE = "com.mymusicapp.PAUSE"
        const val ACTION_RESUME = "com.mymusicapp.RESUME"

        const val EXTRA_URL = "EXTRA_URL"
        const val EXTRA_TITLE = "EXTRA_TITLE"

        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "media"
    }

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var playbackManager: PlaybackManager

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        playbackManager = PlaybackManager(this)

        mediaSession = MediaSessionCompat(this, "MusicService").apply {

            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            // setCallback(object : MediaSessionCompat.Callback() {

            //     override fun onPlay() {
            //         playbackManager.resume()
            //         updatePlaybackState(true)
            //     }

            //     override fun onPause() {
            //         playbackManager.pause()
            //         updatePlaybackState(false)
            //     }

            //     override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            //         uri ?: return
            //         playbackManager.play(uri.toString())
            //         updatePlaybackState(true)
            //     }
            // })
            setCallback(object : MediaSessionCompat.Callback() {

                override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                    val track = loadTracks().find { it.id == mediaId } ?: return

                    playbackManager.play(track.radioUrl)
                    updateMetadata(track.title, track.artist)
                    updatePlaybackState(true)
                }

                override fun onPause() {
                    playbackManager.pause()
                    updatePlaybackState(false)
                }
            })


            isActive = true
        }

        sessionToken = mediaSession.sessionToken
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            ACTION_PLAY -> {
                val url = intent.getStringExtra(EXTRA_URL) ?: return START_NOT_STICKY
                val title = intent.getStringExtra(EXTRA_TITLE) ?: "MyMusicApp"

                playbackManager.play(url)
                updateMetadata(title, "Radio")
                updatePlaybackState(true)

                startForeground(NOTIFICATION_ID, buildNotification())
            }

            ACTION_PAUSE -> {
                playbackManager.pause()
                updatePlaybackState(false)
            }

            ACTION_RESUME -> {
                playbackManager.resume()
                updatePlaybackState(true)
            }
        }

        return START_STICKY
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot("ROOT", null)
    }

    // override fun onLoadChildren(
    //     parentId: String,
    //     result: Result<List<MediaBrowserCompat.MediaItem>>
    // ) {
    //     val item = MediaBrowserCompat.MediaItem(
    //         MediaDescriptionCompat.Builder()
    //             .setMediaId("sample")
    //             .setTitle("Sample AAC")
    //             .build(),
    //         MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
    //     )
    //     result.sendResult(listOf(item))
    // }

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        val tracks = loadTracks()

        val items = tracks.map { track ->
            MediaBrowserCompat.MediaItem(
                MediaDescriptionCompat.Builder()
                    .setMediaId(track.id)
                    .setTitle(track.title)
                    .setSubtitle(track.artist)
                    .build(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
            )
        }

        result.sendResult(items)
    }


    override fun onDestroy() {
        mediaSession.release()
        playbackManager.release()
        super.onDestroy()
    }

    private fun updateMetadata(title: String, artist: String) {
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
            .build()

        mediaSession.setMetadata(metadata)
    }

    private fun updatePlaybackState(isPlaying: Boolean) {
        val state = if (isPlaying)
            PlaybackStateCompat.STATE_PLAYING
        else
            PlaybackStateCompat.STATE_PAUSED

        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_PLAY_PAUSE
            )
            .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
            .build()

        mediaSession.setPlaybackState(playbackState)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MyMusicApp")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun loadTracks(): List<Track> {
        val prefs = getSharedPreferences("tracks_store", MODE_PRIVATE)
        val json = prefs.getString("tracks_json", null) ?: return emptyList()

        val array = JSONArray(json)
        val list = mutableListOf<Track>()

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)

            list.add(
                Track(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    artist = obj.getString("artist"),
                    radioUrl = obj.getString("radioUrl")
                )
            )
        }

        return list
    }

}


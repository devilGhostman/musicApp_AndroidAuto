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
import com.mymusicapp.bridge.PlayerModule

class MusicService : MediaBrowserServiceCompat() {

    companion object {
        const val ACTION_PLAY = "com.mymusicapp.PLAY"
        const val ACTION_PAUSE = "com.mymusicapp.PAUSE"
        const val ACTION_RESUME = "com.mymusicapp.RESUME"
        const val ACTION_NEXT = "com.mymusicapp.NEXT"
        const val ACTION_PREVIOUS = "com.mymusicapp.PREVIOUS"

        const val EXTRA_URL = "EXTRA_URL"
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val EXTRA_ARTIST = "EXTRA_ARTIST"
        const val EXTRA_THUMBNAIL_URL = "EXTRA_THUMBNAIL_URL"

        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "media"
    }

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var playbackManager: PlaybackManager

    private lateinit var tracks: List<Track>
    private var currentIndex = -1
    private var currentTrack: Track? = null


    override fun onCreate() {
        super.onCreate()

        tracks = loadTracks()

        createNotificationChannel()

        playbackManager = PlaybackManager(this) { title, artist ->
            updateMetadata(
                title = title ?: "Live Radio",
                artist = artist ?: "",
                thumbnailUrl = currentTrack?.thumbnailUrl ?: null
            )
            sendPlaybackEvent()
        }

        mediaSession = MediaSessionCompat(this, "MusicService").apply {

            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            setCallback(object : MediaSessionCompat.Callback() {

                override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                    val index = tracks.indexOfFirst { it.id == mediaId }
                    if (index == -1) return

                    currentIndex = index
                    currentTrack = tracks[currentIndex]
                    playCurrent()
                }

                override fun onPause() {
                    playbackManager.pause()
                    updatePlaybackState(false)
                    sendPlaybackEvent()
                }

                override fun onSkipToNext() {
                    skipToNext()
                }

                override fun onSkipToPrevious() {
                    skipToPrevious()
                }
            })

            isActive = true
        }

        sessionToken = mediaSession.sessionToken
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startForeground(NOTIFICATION_ID, buildNotification())

        when (intent?.action) {
            ACTION_PLAY -> {
                val url = intent.getStringExtra(EXTRA_URL) ?: return START_NOT_STICKY
                val title = intent.getStringExtra(EXTRA_TITLE) ?: "MyMusicApp"
                val artist = intent.getStringExtra(EXTRA_ARTIST) ?: ""
                val thumbnailUrl = intent.getStringExtra(EXTRA_THUMBNAIL_URL) ?: ""

                currentIndex = tracks.indexOfFirst { it.radioUrl == url }
                currentTrack = tracks.getOrNull(currentIndex)

                playbackManager.play(url)
                updateMetadata(
                    title = title,
                    artist = artist.ifEmpty { currentTrack?.artist ?: "" },
                    thumbnailUrl = currentTrack?.thumbnailUrl
                )
                updatePlaybackState(true)
                sendPlaybackEvent()

                startForeground(NOTIFICATION_ID, buildNotification())
            }

            ACTION_PAUSE -> {
                playbackManager.pause()
                updatePlaybackState(false)
                sendPlaybackEvent()
            }

            ACTION_RESUME -> {
                playbackManager.resume()
                updatePlaybackState(true)
                sendPlaybackEvent()
            }

            ACTION_NEXT -> {
                skipToNext()
            }

            ACTION_PREVIOUS -> {
                skipToPrevious()
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

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        tracks = loadTracks()

        val items = tracks.map { track ->
            MediaBrowserCompat.MediaItem(
                MediaDescriptionCompat.Builder()
                    .setMediaId(track.id)
                    .setTitle(track.title)
                    .setSubtitle(track.artist)
                    .setIconUri(Uri.parse(track.thumbnailUrl))
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
                    radioUrl = obj.getString("radioUrl"),
                    thumbnailUrl = obj.getString("thumbnailUrl")
                )
            )
        }

        return list
    }

    private fun playCurrent() {
        if (tracks.isEmpty() || currentIndex !in tracks.indices) return

        val track = tracks[currentIndex]
        currentTrack = track
        
        playbackManager.play(track.radioUrl)
        updateMetadata(track.title, track.artist, track.thumbnailUrl)
        updatePlaybackState(true)
        sendPlaybackEvent()
    }

    private fun skipToNext() {
        if (tracks.isEmpty()) return
        currentIndex = (currentIndex + 1) % tracks.size
        playCurrent()
    }

    private fun skipToPrevious() {
        if (tracks.isEmpty()) return
        currentIndex =
            if (currentIndex - 1 < 0) tracks.size - 1 else currentIndex - 1
        playCurrent()
    }

    private fun updateMetadata(title: String, artist: String, thumbnailUrl: String? = null) {
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, thumbnailUrl)
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
                PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
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

    private fun sendPlaybackEvent() {
        val map = com.facebook.react.bridge.Arguments.createMap()
        map.putString("title", currentTrack?.title)
        map.putString("artist", currentTrack?.artist)
        map.putString("thumbnailUrl", currentTrack?.thumbnailUrl)
        map.putBoolean("isPlaying", mediaSession.controller.playbackState.state == PlaybackStateCompat.STATE_PLAYING)
        PlayerModule.sendPlaybackEvent(map)
    }

}


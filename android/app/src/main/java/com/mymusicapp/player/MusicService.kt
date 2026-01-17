package com.mymusicapp.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.net.Uri

class MusicService : MediaBrowserServiceCompat() {

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

            // ✅ ADD THIS CALLBACK HERE
            setCallback(object : MediaSessionCompat.Callback() {

                override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
                    if (uri != null) {
                        playbackManager.play(uri.toString())
                    }
                }

                override fun onPause() {
                    playbackManager.pause()
                }
            })


            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setActions(
                        PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_PLAY_FROM_URI
                    )
                    .setState(
                        PlaybackStateCompat.STATE_NONE,
                        PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                        1f
                    )
                    .build()
            )

            isActive = true
        }

        sessionToken = mediaSession.sessionToken
    }


    // 🔥 THIS IS REQUIRED FOR ANDROID AUTO
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val url = intent?.getStringExtra("PLAY_URL")
        if (url != null) {
            mediaSession.controller.transportControls
                .playFromUri(Uri.parse(url), null)
        }

        startForeground(1, buildNotification())
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
        if (parentId == "ROOT") {
            val item = MediaBrowserCompat.MediaItem(
                MediaDescriptionCompat.Builder()
                    .setMediaId("sample")
                    .setTitle("Sample AAC")
                    .build(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
            )
            result.sendResult(listOf(item))
        } else {
            result.sendResult(emptyList())
        }
    }

    override fun onDestroy() {
        mediaSession.release()
        playbackManager.pause()
        super.onDestroy()
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, "media")
            .setContentTitle("MyMusicApp")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                "media",
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}




// package com.mymusicapp.player

// import android.os.Bundle
// import androidx.media.MediaBrowserServiceCompat
// import android.support.v4.media.MediaBrowserCompat
// import android.support.v4.media.MediaDescriptionCompat
// import android.support.v4.media.MediaMetadataCompat
// import android.support.v4.media.session.MediaSessionCompat
// import android.support.v4.media.session.PlaybackStateCompat

// class MusicService : MediaBrowserServiceCompat() {

//     private lateinit var mediaSession: MediaSessionCompat
//     private lateinit var playbackManager: PlaybackManager

//     override fun onCreate() {
//         super.onCreate()

//         createNotificationChannel()   // 1️⃣ FIRST

//         playbackManager = PlaybackManager(this)

//         mediaSession = MediaSessionCompat(this, "MusicService").apply {
//             setFlags(
//                 MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
//                 MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
//             )

//             setPlaybackState(
//                 PlaybackStateCompat.Builder()
//                     .setActions(
//                         PlaybackStateCompat.ACTION_PLAY or
//                         PlaybackStateCompat.ACTION_PAUSE or
//                         PlaybackStateCompat.ACTION_PLAY_PAUSE
//                     )
//                     .setState(
//                         PlaybackStateCompat.STATE_NONE,
//                         0,
//                         1.0f
//                     )
//                     .build()
//             )

//             setCallback(object : MediaSessionCompat.Callback() {
//                 override fun onPlay() {
//                     // Android Auto requires this method
//                 }

//                 override fun onPause() {
//                     playbackManager.pause()
//                 }
//             })

//             isActive = true
//         }

//         sessionToken = mediaSession.sessionToken

//          // 3️⃣ START FOREGROUND SERVICE (EXACT SPOT)
//         startForeground(
//             1,
//             androidx.core.app.NotificationCompat.Builder(this, "media")
//                 .setContentTitle("MyMusicApp")
//                 .setSmallIcon(android.R.drawable.ic_media_play)
//                 .setOngoing(true)
//                 .build()
//         )
//     }

//     override fun onGetRoot(
//         clientPackageName: String,
//         clientUid: Int,
//         rootHints: Bundle?
//     ): BrowserRoot {
//         return BrowserRoot("ROOT", null)
//     }

//     override fun onLoadChildren(
//         parentId: String,
//         result: Result<List<MediaBrowserCompat.MediaItem>>
//     ) {
//         if (parentId == "ROOT") {
//             val item = MediaBrowserCompat.MediaItem(
//                 MediaDescriptionCompat.Builder()
//                     .setMediaId("sample")
//                     .setTitle("Sample AAC")
//                     .build(),
//                 MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
//             )
//             result.sendResult(listOf(item))
//         } else {
//             result.sendResult(emptyList())
//         }
//     }

//     override fun onDestroy() {
//         mediaSession.release()
//         playbackManager.pause()
//         super.onDestroy()
//     }

//     private fun createNotificationChannel() {
//     if (android.os.Build.VERSION.SDK_INT >= 26) {
//         val channel = android.app.NotificationChannel(
//             "media",
//             "Media Playback",
//             android.app.NotificationManager.IMPORTANCE_LOW
//         )
//         getSystemService(android.app.NotificationManager::class.java)
//             .createNotificationChannel(channel)
//     }
// }

// }

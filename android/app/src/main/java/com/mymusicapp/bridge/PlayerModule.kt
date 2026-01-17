// package com.mymusicapp.bridge

// import android.os.Handler
// import android.os.Looper
// import com.facebook.react.bridge.*
// import android.content.Intent

// import com.mymusicapp.player.PlaybackManager
// import com.mymusicapp.player.MusicService


// class PlayerModule(
//     reactContext: ReactApplicationContext
// ) : ReactContextBaseJavaModule(reactContext) {

//     private val playbackManager = PlaybackManager(reactContext)
//     private val mainHandler = Handler(Looper.getMainLooper())

//     override fun getName(): String = "PlayerModule"

//     @ReactMethod
//     fun play(track: ReadableMap) {
//         val url = track.getString("url") ?: return

//         val intent = Intent(reactApplicationContext, MusicService::class.java).apply {
//             putExtra("PLAY_URL", url)
//         }

//         reactApplicationContext.startService(intent)
//     }

//     @ReactMethod
//     fun pause() {
//         mainHandler.post {
//             playbackManager.pause()
//         }
//     }

//     @ReactMethod
//     fun resume() {
//         mainHandler.post {
//             playbackManager.resume()
//         }
//     }
// }



package com.mymusicapp.bridge

import android.content.Intent
import com.facebook.react.bridge.*
import com.mymusicapp.player.MusicService

class PlayerModule(
    private val reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = "PlayerModule"

    @ReactMethod
    fun play(track: ReadableMap) {
        val url = track.getString("url") ?: return
        val title = track.getString("title") ?: "MyMusicApp"

        val intent = Intent(reactContext, MusicService::class.java).apply {
            action = MusicService.ACTION_PLAY
            putExtra(MusicService.EXTRA_URL, url)
            putExtra(MusicService.EXTRA_TITLE, title)
        }

        reactContext.startService(intent)
    }

    @ReactMethod
    fun pause() {
        val intent = Intent(reactContext, MusicService::class.java).apply {
            action = MusicService.ACTION_PAUSE
        }
        reactContext.startService(intent)
    }

    @ReactMethod
    fun resume() {
        val intent = Intent(reactContext, MusicService::class.java).apply {
            action = MusicService.ACTION_RESUME
        }
        reactContext.startService(intent)
    }
}


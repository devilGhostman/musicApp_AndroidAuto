// package com.mymusicapp.bridge

// import com.facebook.react.bridge.ReactApplicationContext
// import com.facebook.react.bridge.ReactContextBaseJavaModule
// import com.facebook.react.bridge.ReactMethod
// import com.facebook.react.bridge.ReadableMap

// import com.mymusicapp.player.PlaybackManager

// class PlayerModule(
//     reactContext: ReactApplicationContext
// ) : ReactContextBaseJavaModule(reactContext) {

//     private val playbackManager = PlaybackManager(reactContext)

//     override fun getName(): String {
//         return "PlayerModule"
//     }

//     @ReactMethod
//     fun play(track: ReadableMap) {
//         val url = track.getString("url") ?: return
//         playbackManager.play(url)
//     }

//     @ReactMethod
//     fun pause() {
//         playbackManager.pause()
//     }

//     @ReactMethod
//     fun resume() {
//         playbackManager.resume()
//     }
// }


package com.mymusicapp.bridge

import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.*
import android.content.Intent

import com.mymusicapp.player.PlaybackManager
import com.mymusicapp.player.MusicService


class PlayerModule(
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    private val playbackManager = PlaybackManager(reactContext)
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun getName(): String = "PlayerModule"

    @ReactMethod
    fun play(track: ReadableMap) {
        val url = track.getString("url") ?: return

        val intent = Intent(reactApplicationContext, MusicService::class.java).apply {
            putExtra("PLAY_URL", url)
        }

        reactApplicationContext.startService(intent)
    }

    @ReactMethod
    fun pause() {
        mainHandler.post {
            playbackManager.pause()
        }
    }

    @ReactMethod
    fun resume() {
        mainHandler.post {
            playbackManager.resume()
        }
    }
}


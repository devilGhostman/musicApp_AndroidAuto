package com.mymusicapp.bridge

import android.content.Intent
import com.facebook.react.bridge.*
import com.mymusicapp.player.MusicService
import org.json.JSONArray
import org.json.JSONObject
import android.content.Context


class PlayerModule(
    private val reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = "PlayerModule"

    @ReactMethod
    fun play(track: ReadableMap) {
        val url = track.getString("radioUrl") ?: return
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

    @ReactMethod
    fun updateTracks(tracks: ReadableArray) {
        val prefs = reactApplicationContext
            .getSharedPreferences("tracks_store", Context.MODE_PRIVATE)

        val jsonArray = JSONArray()

        for (i in 0 until tracks.size()) {
            val map = tracks.getMap(i) ?: continue
            val obj = JSONObject()

            obj.put("id", map.getString("id"))
            obj.put("title", map.getString("title"))
            obj.put("artist", map.getString("artist"))
            obj.put("radioUrl", map.getString("radioUrl"))

            jsonArray.put(obj)
        }

        prefs.edit()
            .putString("tracks_json", jsonArray.toString())
            .apply()
    }

}


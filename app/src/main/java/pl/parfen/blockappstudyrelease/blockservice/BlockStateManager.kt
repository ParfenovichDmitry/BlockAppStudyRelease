package pl.parfen.blockappstudyrelease.blockservice

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class BlockStateManager(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "AppMonitorPrefs"
        private const val KEY_BLOCKED_APP_TIMES = "blocked_app_times"
        private const val KEY_BLOCK_ACTIVE = "block_active"
        private const val KEY_BLOCKED_APP = "blocked_app"
        private const val KEY_LAST_CHECK_TIME = "last_check_time"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    private val gson = Gson()

    private var blockedAppTimes: MutableMap<String, Long> = mutableMapOf()
    private var lastCheckTime: MutableMap<String, Long> = mutableMapOf()
    private var isBlockActive: Boolean = false
    private var blockedApp: String? = null

    init {
        loadBlockedAppTimes()
        loadLastCheckTime()
        loadBlockState()
    }

    fun onAppUsed(appPackage: String, maxUsageTimeMs: Long): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val lastTime = lastCheckTime[appPackage] ?: currentTimeMillis
        val elapsedTime = currentTimeMillis - lastTime
        lastCheckTime[appPackage] = currentTimeMillis
        saveLastCheckTime()
        val totalTime = (blockedAppTimes[appPackage] ?: 0L) + elapsedTime
        blockedAppTimes[appPackage] = totalTime
        saveBlockedAppTimes()
        if (!isBlockActive && totalTime >= maxUsageTimeMs) {
            isBlockActive = true
            blockedApp = appPackage
            saveBlockState()
            return true
        }
        return isBlockActive && blockedApp == appPackage
    }

    fun clearBlockState() {
        isBlockActive = false
        blockedApp = null
        saveBlockState()
    }

    fun saveBlockedAppTimes() {
        prefs.edit {
            val json = gson.toJson(blockedAppTimes)
            putString(KEY_BLOCKED_APP_TIMES, json)
        }
    }

    fun loadBlockedAppTimes() {
        val json = prefs.getString(KEY_BLOCKED_APP_TIMES, "{}")
        val type = object : TypeToken<Map<String, Long>>() {}.type
        blockedAppTimes.clear()
        blockedAppTimes.putAll(gson.fromJson(json, type) ?: emptyMap())
    }

    fun saveLastCheckTime() {
        prefs.edit {
            val json = gson.toJson(lastCheckTime)
            putString(KEY_LAST_CHECK_TIME, json)
        }
    }

    fun loadLastCheckTime() {
        val json = prefs.getString(KEY_LAST_CHECK_TIME, "{}")
        val type = object : TypeToken<Map<String, Long>>() {}.type
        lastCheckTime.clear()
        lastCheckTime.putAll(gson.fromJson(json, type) ?: emptyMap())
    }

    fun getBlockedAppTime(appPackage: String): Long {
        return blockedAppTimes[appPackage] ?: 0L
    }

    fun saveBlockState() {
        prefs.edit {
            putBoolean(KEY_BLOCK_ACTIVE, isBlockActive)
            putString(KEY_BLOCKED_APP, blockedApp)
        }
    }

    fun loadBlockState() {
        isBlockActive = prefs.getBoolean(KEY_BLOCK_ACTIVE, false)
        blockedApp = prefs.getString(KEY_BLOCKED_APP, null)
    }
}

package pl.parfen.blockappstudyrelease.blockservice

import android.annotation.SuppressLint
import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.*
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import pl.parfen.blockappstudyrelease.BlockedAppActivity
import pl.parfen.blockappstudyrelease.MainActivity
import pl.parfen.blockappstudyrelease.R
import pl.parfen.blockappstudyrelease.data.database.AppDatabase
import pl.parfen.blockappstudyrelease.data.model.Profile
import java.util.concurrent.atomic.AtomicBoolean

class AppMonitoringService : Service() {

    companion object {
        private const val CHANNEL_ID = "monitoring_channel"
        private const val MONITOR_INTERVAL_MS = 5000L
    }

    private val isBlockActive = AtomicBoolean(false)
    private var blockedAppTimes = mutableMapOf<String, Long>()
    private var lastCheckTime = mutableMapOf<String, Long>()
    private var blockedApps: List<String>? = null
    private val handler = Handler(Looper.getMainLooper())
    private var activeProfile: Profile? = null
    private var serviceStarted = false
    private var maxUsageTimeMs: Long = 0
    private var lastForegroundApp: String? = null
    private lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        db = AppDatabase.getDatabase(this)
        Log.d("AppMonitoringService", "Service created")
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AppMonitoringService", "Service started")
        if (intent?.action == "BLOCK_ACTIVITY_CLOSED") {
            isBlockActive.set(false)
            handler.post(monitoringRunnable)
            return START_STICKY
        }

        if (!serviceStarted) {
            serviceStarted = true
            startForeground(1, createForegroundNotification())
            checkPermissionsAndLoadData(intent)
        }
        return START_STICKY
    }

    private fun checkPermissionsAndLoadData(intent: Intent?) {
        Log.d("AppMonitoringService", "Checking permissions")
        if (!checkPermissions()) {
            Log.d("AppMonitoringService", "Missing permissions")
            val mainIntent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(mainIntent)
            stopSelf()
            return
        }

        val profileId = intent?.getIntExtra("ACTIVE_PROFILE_ID", -1) ?: -1
        Log.d("AppMonitoringService", "Received profile ID: $profileId")
        if (profileId != -1) {
            loadProfileData(profileId)
        } else {
            stopSelf()
        }
    }

    private fun checkPermissions(): Boolean {
        return hasUsageStatsPermission() && isOverlayPermissionGranted()
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun isOverlayPermissionGranted(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    private fun loadProfileData(profileId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val profile = db.profileDao().getProfileById(profileId)
            if (profile != null) {
                activeProfile = profile
                blockedApps = profile.blockedApps
                maxUsageTimeMs = profile.usageTime * 60_000L
                Log.d("AppMonitoringService", "Loaded profile: $profileId, blockedApps: $blockedApps, timeLimit: $maxUsageTimeMs ms")
                withContext(Dispatchers.Main) {
                    handler.post(monitoringRunnable)
                }
            } else {
                stopSelf()
            }
        }
    }

    private val monitoringRunnable = object : Runnable {
        override fun run() {
            checkRunningApps()
            handler.postDelayed(this, MONITOR_INTERVAL_MS)
        }
    }

    private fun checkRunningApps() {
        val foregroundApp = getForegroundApp()
        Log.v("AppMonitoringService", "Detected via queryUsageStats: $foregroundApp")
        Log.d("AppMonitoringService", "Foreground app: $foregroundApp")
        if (foregroundApp == null) return

        if (lastForegroundApp != foregroundApp) {
            Log.d("AppMonitoringService", "App changed from $lastForegroundApp to $foregroundApp")
            lastForegroundApp = foregroundApp
        }

        val now = System.currentTimeMillis()
        val lastTime = lastCheckTime[foregroundApp] ?: now
        val elapsed = now - lastTime
        lastCheckTime[foregroundApp] = now

        if (blockedApps?.contains(foregroundApp) == true) {
            blockedAppTimes[foregroundApp] = (blockedAppTimes[foregroundApp] ?: 0) + elapsed
        }

        val totalBlockedTime = blockedAppTimes.filterKeys { blockedApps?.contains(it) == true }
            .values.sum()

        Log.d("AppMonitoringService", "App $foregroundApp used for $elapsed ms (total: ${blockedAppTimes[foregroundApp] ?: 0} ms)")
        Log.d("AppMonitoringService", "Total blocked usage time across apps: $totalBlockedTime ms")

        if (totalBlockedTime >= maxUsageTimeMs && isBlockActive.compareAndSet(false, true)) {
            Log.d("AppMonitoringService", "Total usage limit exceeded, launching BlockedAppActivity")

            val selectedBook = getSharedPreferences("book_settings", MODE_PRIVATE)
                .getString("activeBook", "") ?: ""
            Log.d("AppMonitoringService", "Selected book from SharedPreferences: $selectedBook")

            showBlockScreen(foregroundApp, activeProfile?.id ?: -1, selectedBook)
        }
    }

    private fun getForegroundApp(): String? {
        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()

        val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 30_000, time)
        val mostRecent = stats?.maxByOrNull { it.lastTimeUsed }
        if (mostRecent?.lastTimeUsed ?: 0 > 0) {
            return mostRecent?.packageName
        }

        val events = usm.queryEvents(time - 30_000, time)
        val event = UsageEvents.Event()
        var lastApp: String? = null
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                lastApp = event.packageName
            }
        }
        return lastApp
    }

    private fun showBlockScreen(packageName: String, profileId: Int, selectedBookTitle: String?) {
        val intent = Intent(this, BlockedAppActivity::class.java).apply {
            putExtra("BLOCKED_APP", packageName)
            putExtra("PROFILE_ID", profileId)
            putExtra("SELECTED_BOOK_TITLE", selectedBookTitle ?: "")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        Log.d("AppMonitoringService", "Launching BlockedAppActivity for $packageName with selectedBookTitle=$selectedBookTitle")
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createForegroundNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.monitoring_service_notification))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(monitoringRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
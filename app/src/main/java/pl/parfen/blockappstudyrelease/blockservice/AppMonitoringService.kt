package pl.parfen.blockappstudyrelease.blockservice

import android.app.*
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.*
import android.provider.Settings
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private var blockedApp: String? = null
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
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "BLOCK_ACTIVITY_CLOSED") {
            clearBlockState()
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
        if (!checkPermissions()) {
            val mainIntent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(mainIntent)
            stopSelf()
            return
        }
        if (intent != null && intent.hasExtra("ACTIVE_PROFILE_ID")) {
            val profileId = intent.getIntExtra("ACTIVE_PROFILE_ID", -1)
            if (profileId != -1) {
                loadProfileData(profileId)
            } else {
                stopSelf()
            }
        } else {
            stopSelf()
        }
    }

    private fun checkPermissions(): Boolean {
        val usagePermission = hasUsageStatsPermission()
        val overlayPermission = isOverlayPermissionGranted()
        return usagePermission && overlayPermission
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        }
        return mode == android.app.AppOpsManager.MODE_ALLOWED
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
        if (foregroundApp == null) return

        if (isBlockActive.get() && blockedApps?.contains(foregroundApp) == true) {
            showBlockScreen(blockedApp ?: foregroundApp, activeProfile?.id ?: -1)
            return
        }

        if (lastForegroundApp != foregroundApp) {
            lastForegroundApp = foregroundApp
        }

        if (blockedApps?.contains(foregroundApp) == true) {
            val now = System.currentTimeMillis()
            val lastTime = lastCheckTime[foregroundApp] ?: now
            val elapsed = now - lastTime
            lastCheckTime[foregroundApp] = now
            blockedAppTimes[foregroundApp] = (blockedAppTimes[foregroundApp] ?: 0) + elapsed

            if ((blockedAppTimes[foregroundApp] ?: 0) >= maxUsageTimeMs && isBlockActive.compareAndSet(false, true)) {
                blockedApp = foregroundApp
                showBlockScreen(foregroundApp, activeProfile?.id ?: -1)
            }
        }
    }

    private fun getForegroundApp(): String? {
        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 10000, time)
        val mostRecent = stats?.maxByOrNull { it.lastTimeUsed }
        return mostRecent?.packageName
    }

    private fun showBlockScreen(packageName: String, profileId: Int) {
        val intent = Intent(this, BlockedAppActivity::class.java).apply {
            putExtra("BLOCKED_APP", packageName)
            putExtra("PROFILE_ID", profileId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
    }

    private fun clearBlockState() {
        isBlockActive.set(false)
        blockedApp = null
        blockedAppTimes.clear()
        lastCheckTime.clear()
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

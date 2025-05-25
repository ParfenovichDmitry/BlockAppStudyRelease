package pl.parfen.blockapp2.service

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import pl.parfen.blockappstudyrelease.blockservice.BlockStateManager
import pl.parfen.blockappstudyrelease.blockservice.IntentHandler
import pl.parfen.blockappstudyrelease.blockservice.MonitoringNotificationHelper
import pl.parfen.blockappstudyrelease.blockservice.ProfileRepository
import pl.parfen.blockappstudyrelease.data.model.Profile

class UsageMonitorManager(
    private val context: Context,
    private val blockStateManager: BlockStateManager,
    private val intentHandler: IntentHandler,
    private val profileRepository: ProfileRepository,
    private val notificationHelper: MonitoringNotificationHelper
) {
    companion object {
        private const val MONITOR_INTERVAL_MS = 5000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private var activeProfile: Profile? = null
    private var blockedApps: List<String>? = null
    private var maxUsageTimeMs: Long = 0
    private var lastForegroundApp: String? = null
    private var isMonitoring = false

    private val monitoringRunnable = object : Runnable {
        override fun run() {
            checkRunningApps()
            if (isMonitoring) {
                handler.postDelayed(this, MONITOR_INTERVAL_MS)
            }
        }
    }

    fun startMonitoring(profile: Profile) {
        this.activeProfile = profile
        this.blockedApps = profile.blockedApps
        this.maxUsageTimeMs = profile.usageTime * 60_000L
        this.isMonitoring = true
        handler.postDelayed(monitoringRunnable, MONITOR_INTERVAL_MS)
    }

    fun stopMonitoring() {
        isMonitoring = false
        handler.removeCallbacks(monitoringRunnable)
        activeProfile = null
    }

    private fun checkRunningApps() {
        val foregroundApp = getForegroundApp()
        if (foregroundApp == null) return

        if (blockedApps?.contains(foregroundApp) == true) {
            val shouldBlock = blockStateManager.onAppUsed(foregroundApp, maxUsageTimeMs)
            if (shouldBlock) {
                intentHandler.showBlockScreen(foregroundApp, activeProfile?.id ?: -1, null)
            }
        }
        lastForegroundApp = foregroundApp
    }

    private fun getForegroundApp(): String? {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 10000, time)
        if (stats != null && stats.isNotEmpty()) {
            val mostRecent = stats.maxByOrNull { it.lastTimeUsed }
            return mostRecent?.packageName
        }
        return null
    }
}

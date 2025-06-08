package pl.parfen.blockappstudyrelease

import android.Manifest
import android.app.ActivityManager
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.runBlocking
import pl.parfen.blockappstudyrelease.blockservice.AppMonitoringService
import pl.parfen.blockappstudyrelease.data.model.ai.ChatGPTManager
import pl.parfen.blockappstudyrelease.data.repository.BookRepositoryImpl
import pl.parfen.blockappstudyrelease.data.repository.blockapp.AiTextRepositoryImpl
import pl.parfen.blockappstudyrelease.data.repository.blockapp.ProfileRepository
import pl.parfen.blockappstudyrelease.domain.GetNextBookUseCase
import pl.parfen.blockappstudyrelease.domain.usecase.CheckSubscriptionUseCase
import pl.parfen.blockappstudyrelease.domain.usecase.GetTextForReadingUseCase
import pl.parfen.blockappstudyrelease.domain.usecase.ValidatePasswordUseCase
import pl.parfen.blockappstudyrelease.ui.blockapp.BlockedAppScreen
import pl.parfen.blockappstudyrelease.ui.theme.BlockAppStudyReleaseTheme
import pl.parfen.blockappstudyrelease.viewmodel.blockapp.BlockedAppViewModel
import pl.parfen.blockappstudyrelease.viewmodel.blockapp.BlockedAppViewModelFactory
import java.util.*

class BlockedAppActivity : BaseActivity() {

    private lateinit var viewModel: BlockedAppViewModel

    override fun attachBaseContext(newBase: Context) {
        val profileId = newBase.getSharedPreferences("blocked_app", Context.MODE_PRIVATE)
            .getInt("profile_id", -1)

        val profileRepository = ProfileRepository(newBase)
        val language = runBlocking {
            profileRepository.getProfileById(profileId)?.profileLanguage ?: "en"
        }

        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = newBase.resources.configuration
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this, null)

        val profileId = intent.getIntExtra("PROFILE_ID", -1)
        val blockedApp = intent.getStringExtra("BLOCKED_APP") ?: "Unknown App"
        val fileUri = intent.getStringExtra("fileUri")?.let { Uri.parse(it) }
        val requiredCompliance = intent.getFloatExtra("REQUIRED_COMPLIANCE", 80f)
        val selectedBookTitle = intent.getStringExtra("SELECTED_BOOK_TITLE") ?: ""

        Log.d("BlockedApp", "Выбранная книга: $selectedBookTitle")

        getSharedPreferences("blocked_app", Context.MODE_PRIVATE).edit()
            .putInt("profile_id", profileId).apply()

        val bookRepository = BookRepositoryImpl(applicationContext)
        val profileRepository = ProfileRepository(applicationContext)
        val aiTextRepository = AiTextRepositoryImpl(ChatGPTManager(Handler(Looper.getMainLooper())))
        val getNextBookUseCase = GetNextBookUseCase(bookRepository)
        val checkSubscriptionUseCase = CheckSubscriptionUseCase(applicationContext)
        val getTextForReadingUseCase = GetTextForReadingUseCase(
            bookRepository,
            profileRepository,
            aiTextRepository,
            getNextBookUseCase,
            checkSubscriptionUseCase
        )
        val validatePasswordUseCase = ValidatePasswordUseCase(profileRepository)

        viewModel = ViewModelProvider(
            this,
            BlockedAppViewModelFactory(
                application,
                blockedApp,
                profileId,
                fileUri,
                getTextForReadingUseCase,
                validatePasswordUseCase,
                requiredCompliance,
                selectedBookTitle
            )
        )[BlockedAppViewModel::class.java]

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(
                    this@BlockedAppActivity,
                    "Чтобы снять блокировку, прочитайте текст или введите пароль",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        setContent {
            BlockAppStudyReleaseTheme {
                val uiState = viewModel.uiState.collectAsState().value

                BlockedAppScreen(
                    uiState = uiState,
                    onStartReading = { viewModel.startReading() },
                    onStopReading = { viewModel.stopReading() },
                    onShowPasswordDialog = { viewModel.showPasswordDialog() },
                    onPasswordEntered = { viewModel.checkPassword(it) },
                    onUnlock = { unlockApp() },
                    onCancelPasswordDialog = { viewModel.cancelPasswordDialog() }
                )
            }
        }
    }

    private fun unlockApp() {
        try {
            val serviceIntent = Intent(this, AppMonitoringService::class.java).apply {
                action = "BLOCK_ACTIVITY_CLOSED"
            }
            startService(serviceIntent)
        } catch (e: Exception) {
            Log.e("BlockedAppActivity", "Ошибка при разблокировке: ${e.message}")
            Toast.makeText(this, "Ошибка: не удалось снять блокировку", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        moveTaskToBack(false)
    }

    @RequiresPermission(Manifest.permission.REORDER_TASKS)
    override fun onPause() {
        super.onPause()
        moveTaskToFront()
    }

    @RequiresPermission(Manifest.permission.REORDER_TASKS)
    private fun moveTaskToFront() {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.moveTaskToFront(taskId, 0)
    }
}

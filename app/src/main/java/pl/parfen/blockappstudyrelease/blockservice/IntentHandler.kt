package pl.parfen.blockappstudyrelease.blockservice

import android.content.Context
import android.content.Intent
import pl.parfen.blockappstudyrelease.BlockedAppActivity
import pl.parfen.blockappstudyrelease.MainActivity

class IntentHandler(private val context: Context) {

    fun showBlockScreen(packageName: String, profileId: Int, fileUri: String? = null) {
        val intent = Intent(context, BlockedAppActivity::class.java).apply {
            putExtra("BLOCKED_APP", packageName)
            putExtra("PROFILE_ID", profileId)
            fileUri?.let { putExtra("fileUri", it) }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        context.startActivity(intent)
    }

    fun openMainActivity() {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

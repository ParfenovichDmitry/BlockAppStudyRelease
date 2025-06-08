package pl.parfen.blockappstudyrelease.domain.usecase

import android.content.Context
import android.content.SharedPreferences

class CheckSubscriptionUseCase(
    context: Context
) {
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("subscription_prefs", Context.MODE_PRIVATE)
    operator fun invoke(): Boolean {
        // Временно включаем подписку для всех пользователей
        return true
    }
}

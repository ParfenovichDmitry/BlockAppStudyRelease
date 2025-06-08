package pl.parfen.blockappstudyrelease.domain.usecase

import pl.parfen.blockappstudyrelease.data.repository.blockapp.ProfileRepository

class ValidatePasswordUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profileId: Int, password: String): Boolean {
        val profile = profileRepository.getProfileById(profileId) ?: return false
        return profile.password == password
    }
}

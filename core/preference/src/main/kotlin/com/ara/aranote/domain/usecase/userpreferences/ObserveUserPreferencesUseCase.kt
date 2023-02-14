package com.ara.aranote.domain.usecase.userpreferences

import com.ara.aranote.data.repository.UserPreferencesRepository
import javax.inject.Inject

class ObserveUserPreferencesUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    operator fun invoke() =
        userPreferencesRepository.observe()
}

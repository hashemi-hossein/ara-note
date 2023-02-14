package com.ara.aranote.domain.usecase.userpreferences

import com.ara.aranote.data.repository.UserPreferencesRepository
import javax.inject.Inject

class ReadUserPreferencesUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke() =
        userPreferencesRepository.read()
}

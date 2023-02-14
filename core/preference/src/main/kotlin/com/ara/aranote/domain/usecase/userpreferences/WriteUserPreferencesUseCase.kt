package com.ara.aranote.domain.usecase.userpreferences

import com.ara.aranote.data.datastore.UserPreferences
import com.ara.aranote.data.repository.UserPreferencesRepository
import javax.inject.Inject
import kotlin.reflect.KProperty1

class WriteUserPreferencesUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun <T> invoke(kProperty: KProperty1<UserPreferences, T>, value: T) =
        userPreferencesRepository.write(kProperty, value)
}

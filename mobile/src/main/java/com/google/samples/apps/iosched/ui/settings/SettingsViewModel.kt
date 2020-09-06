/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.iosched.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.iosched.model.Theme
import com.google.samples.apps.iosched.shared.di.SharedDependencyModule
import com.google.samples.apps.iosched.shared.domain.prefs.NotificationsPrefSaveActionUseCase
import com.google.samples.apps.iosched.shared.domain.settings.GetAnalyticsSettingUseCase
import com.google.samples.apps.iosched.shared.domain.settings.GetAvailableThemesUseCase
import com.google.samples.apps.iosched.shared.domain.settings.GetNotificationsSettingUseCase
import com.google.samples.apps.iosched.shared.domain.settings.GetThemeUseCase
import com.google.samples.apps.iosched.shared.domain.settings.GetTimeZoneUseCase
import com.google.samples.apps.iosched.shared.domain.settings.SetAnalyticsSettingUseCase
import com.google.samples.apps.iosched.shared.domain.settings.SetThemeUseCase
import com.google.samples.apps.iosched.shared.domain.settings.SetTimeZoneUseCase
import com.google.samples.apps.iosched.shared.result.Event
import com.google.samples.apps.iosched.shared.result.data
import com.google.samples.apps.iosched.shared.result.successOr
import com.google.samples.apps.iosched.shared.result.updateOnSuccess
import com.wada811.dependencyproperty.dependencyModule
import kotlinx.coroutines.launch

class SettingsViewModel @JvmOverloads constructor(
    application: Application,
    val setTimeZoneUseCase: SetTimeZoneUseCase = application.dependencyModule<SharedDependencyModule>().setTimeZoneUseCase,
    getTimeZoneUseCase: GetTimeZoneUseCase = application.dependencyModule<SharedDependencyModule>().getTimeZoneUseCase,
    val notificationsPrefSaveActionUseCase: NotificationsPrefSaveActionUseCase = application.dependencyModule<SharedDependencyModule>().notificationsPrefSaveActionUseCase,
    getNotificationsSettingUseCase: GetNotificationsSettingUseCase = application.dependencyModule<SharedDependencyModule>().getNotificationsSettingUseCase,
    val setAnalyticsSettingUseCase: SetAnalyticsSettingUseCase = application.dependencyModule<SharedDependencyModule>().setAnalyticsSettingUseCase,
    getAnalyticsSettingUseCase: GetAnalyticsSettingUseCase = application.dependencyModule<SharedDependencyModule>().getAnalyticsSettingUseCase,
    val setThemeUseCase: SetThemeUseCase = application.dependencyModule<SharedDependencyModule>().setThemeUseCase,
    getThemeUseCase: GetThemeUseCase = application.dependencyModule<SharedDependencyModule>().getThemeUseCase,
    getAvailableThemesUseCase: GetAvailableThemesUseCase = application.dependencyModule<SharedDependencyModule>().getAvailableThemesUseCase
) : AndroidViewModel(application) {

    // Time Zone setting
    private val _preferConferenceTimeZone = MutableLiveData<Boolean>()
    val preferConferenceTimeZone: LiveData<Boolean>
        get() = _preferConferenceTimeZone

    // Notifications setting
    private val _enableNotifications = MutableLiveData<Boolean>()
    val enableNotifications: LiveData<Boolean>
        get() = _enableNotifications

    // Analytics setting
    private val _sendUsageStatistics = MutableLiveData<Boolean>()
    val sendUsageStatistics: LiveData<Boolean>
        get() = _sendUsageStatistics

    // Theme setting
    val theme: LiveData<Theme> = liveData {
        emit(getThemeUseCase(Unit).successOr(Theme.SYSTEM))
    }

    // Theme setting
    val availableThemes: LiveData<List<Theme>> = liveData {
        emit(getAvailableThemesUseCase(Unit).successOr(emptyList()))
    }

    private val _navigateToThemeSelector = MutableLiveData<Event<Unit>>()
    val navigateToThemeSelector: LiveData<Event<Unit>>
        get() = _navigateToThemeSelector

    init {
        // Executing use cases in parallel
        viewModelScope.launch {
            _preferConferenceTimeZone.value = getTimeZoneUseCase(Unit).data ?: true
        }

        viewModelScope.launch {
            _sendUsageStatistics.value = getAnalyticsSettingUseCase(Unit).data ?: false
        }
        viewModelScope.launch {
            _enableNotifications.value = getNotificationsSettingUseCase(Unit).data ?: false
        }
    }

    fun toggleTimeZone(checked: Boolean) {
        viewModelScope.launch {
            setTimeZoneUseCase(checked).updateOnSuccess(_preferConferenceTimeZone)
        }
    }

    fun toggleSendUsageStatistics(checked: Boolean) {
        viewModelScope.launch {
            setAnalyticsSettingUseCase(checked).updateOnSuccess(_sendUsageStatistics)
        }
    }

    fun toggleEnableNotifications(checked: Boolean) {
        viewModelScope.launch {
            notificationsPrefSaveActionUseCase(checked).updateOnSuccess(_enableNotifications)
        }
    }

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            setThemeUseCase(theme)
        }
    }

    fun onThemeSettingClicked() {
        _navigateToThemeSelector.value = Event(Unit)
    }
}

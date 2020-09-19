/*
 * Copyright 2019 Google LLC
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

package com.google.samples.apps.iosched.ui.theme

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.samples.apps.iosched.model.Theme
import com.google.samples.apps.iosched.shared.domain.settings.GetThemeUseCase
import com.google.samples.apps.iosched.shared.domain.settings.ObserveThemeModeUseCase
import com.google.samples.apps.iosched.shared.result.Result.Success
import com.google.samples.apps.iosched.shared.result.successOr
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

/**
 * Interface to implement activity theming via a ViewModel.
 */
interface ThemedActivityDelegate {
    /**
     * Allows observing of the current theme
     */
    val theme: LiveData<Theme>

    /**
     * Allows querying of the current theme synchronously
     */
    val currentTheme: Theme
}

class ThemedActivityDelegateImpl(
    private val observeThemeUseCase: ObserveThemeModeUseCase,
    private val getThemeUseCase: GetThemeUseCase
) : ThemedActivityDelegate {

    @InternalCoroutinesApi
    @ExperimentalCoroutinesApi
    override val theme: LiveData<Theme> = liveData {
        observeThemeUseCase(Unit).collect {
            emit(it.successOr(Theme.SYSTEM))
        }
    }

    override val currentTheme: Theme
        get() = runBlocking { // Using runBlocking to execute this coroutine synchronously
            getThemeUseCase(Unit).let {
                if (it is Success) it.data else Theme.SYSTEM
            }
        }
}

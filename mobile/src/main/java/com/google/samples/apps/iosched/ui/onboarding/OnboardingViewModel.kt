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

package com.google.samples.apps.iosched.ui.onboarding

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.iosched.di.AppDependencyModule
import com.google.samples.apps.iosched.shared.di.SharedDependencyModule
import com.google.samples.apps.iosched.shared.domain.prefs.OnboardingCompleteActionUseCase
import com.google.samples.apps.iosched.shared.result.Event
import com.google.samples.apps.iosched.ui.signin.SignInViewModelDelegate
import com.wada811.dependencyproperty.dependencyModule
import kotlinx.coroutines.launch

/**
 * Records that onboarding has been completed and navigates user onward.
 */
class OnboardingViewModel @JvmOverloads constructor(
    application: Application,
    private val onboardingCompleteActionUseCase: OnboardingCompleteActionUseCase = application.dependencyModule<SharedDependencyModule>().onboardingCompleteActionUseCase,
    signInViewModelDelegate: SignInViewModelDelegate = application.dependencyModule<AppDependencyModule>().signInViewModelDelegate
) : ViewModel(), SignInViewModelDelegate by signInViewModelDelegate {

    private val _navigateToMainActivity = MutableLiveData<Event<Unit>>()
    val navigateToMainActivity: LiveData<Event<Unit>> = _navigateToMainActivity

    private val _navigateToSignInDialogAction = MutableLiveData<Event<Unit>>()
    val navigateToSignInDialogAction: LiveData<Event<Unit>> = _navigateToSignInDialogAction

    fun getStartedClick() {
        viewModelScope.launch {
            onboardingCompleteActionUseCase(true)
            _navigateToMainActivity.postValue(Event(Unit))
        }
    }

    fun onSigninClicked() {
        _navigateToSignInDialogAction.value = Event(Unit)
    }
}

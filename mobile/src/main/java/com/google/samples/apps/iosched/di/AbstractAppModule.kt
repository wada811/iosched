/*
 * Copyright 2020 Google LLC
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

package com.google.samples.apps.iosched.di

import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.google.samples.apps.iosched.shared.analytics.AnalyticsHelper
import com.google.samples.apps.iosched.shared.di.AbstractSharedModule
import com.google.samples.apps.iosched.shared.di.CoroutineModule
import com.google.samples.apps.iosched.shared.di.SharedModule
import com.google.samples.apps.iosched.ui.filters.FiltersViewModelDelegate
import com.google.samples.apps.iosched.ui.filters.FiltersViewModelDelegateImpl
import com.google.samples.apps.iosched.ui.map.LoadGeoJsonFeaturesUseCase
import com.google.samples.apps.iosched.ui.messages.SnackbarMessageManager
import com.google.samples.apps.iosched.ui.sessioncommon.DefaultEventActionsViewModelDelegate
import com.google.samples.apps.iosched.ui.sessioncommon.EventActionsViewModelDelegate
import com.google.samples.apps.iosched.ui.signin.FirebaseSignInViewModelDelegate
import com.google.samples.apps.iosched.ui.signin.SignInViewModelDelegate
import com.google.samples.apps.iosched.ui.theme.ThemedActivityDelegate
import com.google.samples.apps.iosched.ui.theme.ThemedActivityDelegateImpl
import com.google.samples.apps.iosched.util.FirebaseAnalyticsHelper
import com.google.samples.apps.iosched.util.signin.SignInHandler
import com.google.samples.apps.iosched.util.wifi.WifiInstaller
import com.wada811.dependencyproperty.DependencyModule
import com.wada811.dependencyproperty.dependencyModule

abstract class AbstractAppModule(protected val application: Application) : DependencyModule {
    protected val sharedModule: AbstractSharedModule by lazy { application.dependencyModule<SharedModule>() }
    private val coroutineModule: CoroutineModule by lazy { application.dependencyModule<CoroutineModule>() }
    private val wifiManager: WifiManager
        get() = application.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val connectivityManager: ConnectivityManager
        get() = application.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val clipboardManager: ClipboardManager
        get() = application.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    abstract val signInHandler: SignInHandler
    val signInViewModelDelegate: SignInViewModelDelegate by lazy {
        FirebaseSignInViewModelDelegate(
            sharedModule.observeUserAuthStateUseCase,
            sharedModule.notificationsPrefIsShownUseCase,
            coroutineModule.ioDispatcher,
            coroutineModule.mainDispatcher,
            sharedModule.featureFlags.isReservationFeatureEnabled
        )
    }
    val analyticsHelper: AnalyticsHelper by lazy {
        FirebaseAnalyticsHelper(
            sharedModule.applicationScope,
            application,
            signInViewModelDelegate,
            sharedModule.preferenceStorage
        )
    }
    val filtersViewModelDelegate: FiltersViewModelDelegate get() = FiltersViewModelDelegateImpl()
    val themedActivityDelegate: ThemedActivityDelegate
        get() = ThemedActivityDelegateImpl(
            sharedModule.observeThemeModeUseCase,
            sharedModule.getThemeUseCase
        )
    val snackbarMessageManager: SnackbarMessageManager by lazy {
        SnackbarMessageManager(
            sharedModule.preferenceStorage
        )
    }
    val wifiInstaller: WifiInstaller
        get() = WifiInstaller(
            wifiManager,
            clipboardManager
        )
    val loadGeoJsonFeaturesUseCase: LoadGeoJsonFeaturesUseCase
        get() = LoadGeoJsonFeaturesUseCase(
            application,
            coroutineModule.ioDispatcher
        )
    val eventActionsViewModelDelegate: EventActionsViewModelDelegate
        get() = DefaultEventActionsViewModelDelegate(
            signInViewModelDelegate,
            sharedModule.starEventAndNotifyUseCase,
            snackbarMessageManager,
            sharedModule.applicationScope,
            coroutineModule.mainDispatcher
        )
}

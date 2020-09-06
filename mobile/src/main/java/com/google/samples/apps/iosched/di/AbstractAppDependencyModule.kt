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

import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.google.samples.apps.iosched.shared.analytics.AnalyticsHelper
import com.google.samples.apps.iosched.shared.di.AbstractSharedDependencyModule
import com.google.samples.apps.iosched.shared.di.CoroutinesDependencyModule
import com.google.samples.apps.iosched.shared.domain.internal.IOSchedHandler
import com.google.samples.apps.iosched.shared.domain.internal.IOSchedMainHandler
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

abstract class AbstractAppDependencyModule(
    protected val context: Context,
    protected val sharedDependencyModule: AbstractSharedDependencyModule,
    private val coroutinesDependencyModule: CoroutinesDependencyModule = CoroutinesDependencyModule()
) : DependencyModule {
    val wifiManager: WifiManager
        get() = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val connectivityManager: ConnectivityManager
        get() = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val clipboardManager: ClipboardManager
        get() = context.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val mainThreadHandler: IOSchedHandler by lazy { IOSchedMainHandler() }
    abstract val signInHandler: SignInHandler
    val signInViewModelDelegate: SignInViewModelDelegate by lazy {
        FirebaseSignInViewModelDelegate(
            sharedDependencyModule.observeUserAuthStateUseCase,
            sharedDependencyModule.notificationsPrefIsShownUseCase,
            coroutinesDependencyModule.ioDispatcher,
            coroutinesDependencyModule.mainDispatcher,
            sharedDependencyModule.featureFlags.isReservationFeatureEnabled
        )
    }
    val analyticsHelper: AnalyticsHelper by lazy {
        FirebaseAnalyticsHelper(
            sharedDependencyModule.applicationScope,
            context,
            signInViewModelDelegate,
            sharedDependencyModule.preferenceStorage
        )
    }
    val filtersViewModelDelegate: FiltersViewModelDelegate get() = FiltersViewModelDelegateImpl()
    val themedActivityDelegate: ThemedActivityDelegate
        get() = ThemedActivityDelegateImpl(
            sharedDependencyModule.observeThemeModeUseCase,
            sharedDependencyModule.getThemeUseCase
        )
    val snackbarMessageManager: SnackbarMessageManager by lazy {
        SnackbarMessageManager(
            sharedDependencyModule.preferenceStorage
        )
    }
    val wifiInstaller: WifiInstaller
        get() = WifiInstaller(
            wifiManager,
            clipboardManager
        )
    val loadGeoJsonFeaturesUseCase: LoadGeoJsonFeaturesUseCase
        get() = LoadGeoJsonFeaturesUseCase(
            context,
            coroutinesDependencyModule.ioDispatcher
        )
    val eventActionsViewModelDelegate: EventActionsViewModelDelegate
        get() = DefaultEventActionsViewModelDelegate(
            signInViewModelDelegate,
            sharedDependencyModule.starEventAndNotifyUseCase,
            snackbarMessageManager,
            sharedDependencyModule.applicationScope,
            coroutinesDependencyModule.mainDispatcher
        )
}

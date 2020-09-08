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

package com.google.samples.apps.iosched

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy.Builder
import com.google.samples.apps.iosched.di.AppModule
import com.google.samples.apps.iosched.shared.analytics.AnalyticsHelper
import com.google.samples.apps.iosched.shared.di.SharedModule
import com.google.samples.apps.iosched.util.CrashlyticsTree
import com.jakewharton.threetenabp.AndroidThreeTen
import com.wada811.dependencyproperty.DependencyModules
import com.wada811.dependencyproperty.DependencyModulesHolder
import com.wada811.dependencyproperty.dependency
import timber.log.Timber

/**
 * Initialization of libraries.
 */
class MainApplication : Application(), DependencyModulesHolder {
    private val sharedModule = SharedModule(this)
    override val dependencyModules: DependencyModules by dependencyModules(
        AppModule(this, sharedModule),
        sharedModule
    )

    override fun onCreate() {
        // Even if the var isn't used, needs to be initialized at application startup.
        val analyticsHelper = dependency<AppModule, AnalyticsHelper> { it.analyticsHelper }.value
        analyticsHelper.toString()

        // ThreeTenBP for times and dates, called before super to be available for objects
        AndroidThreeTen.init(this)

        // Enable strict mode
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )
    }
}

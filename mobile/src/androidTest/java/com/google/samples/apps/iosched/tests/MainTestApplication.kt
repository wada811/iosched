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

package com.google.samples.apps.iosched.tests

import android.app.Application
import com.google.samples.apps.iosched.di.AppModule
import com.google.samples.apps.iosched.shared.di.SharedModule
import com.google.samples.apps.iosched.tests.di.TestCoroutineDispatchers
import com.jakewharton.threetenabp.AndroidThreeTen
import com.wada811.dependencyproperty.DependencyModules
import com.wada811.dependencyproperty.DependencyModulesHolder
import timber.log.Timber

/**
 * Used as a base application to run instrumented tests through the [CustomTestRunner].
 */
open class MainTestApplication : Application(), DependencyModulesHolder {
    private val coroutinesModule = TestCoroutineDispatchers()
    private val sharedDependencyModule: SharedModule = SharedModule(this, coroutinesModule)
    override val dependencyModules: DependencyModules by dependencyModules(AppModule(this, sharedDependencyModule, coroutinesModule), sharedDependencyModule)
    override fun onCreate() {
        // ThreeTenBP for times and dates, called before super to be available for objects
        AndroidThreeTen.init(this)
        Timber.plant(Timber.DebugTree())
        super.onCreate()
    }
}

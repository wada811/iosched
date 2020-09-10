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

package com.google.samples.apps.iosched.tests.di

import android.os.AsyncTask
import com.google.samples.apps.iosched.shared.di.CoroutineModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher

class TestCoroutineModule : CoroutineModule() {
    override val defaultDispatcher: CoroutineDispatcher = AsyncTask.THREAD_POOL_EXECUTOR.asCoroutineDispatcher()
    override val ioDispatcher: CoroutineDispatcher = AsyncTask.THREAD_POOL_EXECUTOR.asCoroutineDispatcher()
    override val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    override val mainImmediateDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
}

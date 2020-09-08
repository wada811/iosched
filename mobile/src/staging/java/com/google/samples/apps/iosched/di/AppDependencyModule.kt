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

import android.content.Context
import com.google.samples.apps.iosched.shared.di.AbstractSharedDependencyModule
import com.google.samples.apps.iosched.shared.di.CoroutinesDependencyModule
import com.google.samples.apps.iosched.util.signin.SignInHandler
import com.google.samples.apps.iosched.util.signin.StagingAuthenticatedUser
import com.google.samples.apps.iosched.util.signin.StagingSignInHandler

class AppDependencyModule(
    context: Context,
    sharedDependencyModule: AbstractSharedDependencyModule,
    coroutinesDependencyModule: CoroutinesDependencyModule = CoroutinesDependencyModule()
) : AbstractAppDependencyModule(
    context,
    sharedDependencyModule,
    coroutinesDependencyModule
) {
    override val signInHandler: SignInHandler
        get() = StagingSignInHandler(StagingAuthenticatedUser(context))
}

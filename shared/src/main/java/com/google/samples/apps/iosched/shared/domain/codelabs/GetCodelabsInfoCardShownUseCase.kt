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

package com.google.samples.apps.iosched.shared.domain.codelabs

import com.google.samples.apps.iosched.shared.data.prefs.PreferenceStorage
import com.google.samples.apps.iosched.shared.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher

open class GetCodelabsInfoCardShownUseCase(
    private val preferenceStorage: PreferenceStorage,
    dispatcher: CoroutineDispatcher
) : UseCase<Unit, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Unit) = preferenceStorage.codelabsInfoShown
}

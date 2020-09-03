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

package com.google.samples.apps.iosched.ui.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.samples.apps.iosched.shared.di.SharedDependencyModule
import com.google.samples.apps.iosched.shared.domain.feed.LoadAnnouncementsUseCase
import com.google.samples.apps.iosched.shared.domain.settings.GetTimeZoneUseCase
import com.google.samples.apps.iosched.shared.result.Result.Loading
import com.google.samples.apps.iosched.shared.result.successOr
import com.google.samples.apps.iosched.shared.time.TimeProvider
import com.google.samples.apps.iosched.shared.util.TimeUtils
import com.wada811.dependencyproperty.dependencyModule
import org.threeten.bp.ZoneId

class AnnouncementsViewModel @JvmOverloads constructor(
    application: Application,
    private val loadAnnouncementsUseCase: LoadAnnouncementsUseCase = application.dependencyModule<SharedDependencyModule>().loadAnnouncementsUseCase,
    private val getTimeZoneUseCase: GetTimeZoneUseCase = application.dependencyModule<SharedDependencyModule>().getTimeZoneUseCase,
    private val timeProvider: TimeProvider = application.dependencyModule<SharedDependencyModule>().timeProvider
) : AndroidViewModel(application) {


    val announcements: LiveData<List<Any>> = liveData {
        val loadAnnouncementsResult = loadAnnouncementsUseCase(timeProvider.now())
        if (loadAnnouncementsResult is Loading) {
            emit(listOf(LoadingIndicator))
        } else {
            val items = loadAnnouncementsResult.successOr(emptyList())
            if (items.isNotEmpty()) {
                emit(items)
            } else {
                emit(listOf(AnnouncementsEmpty))
            }
        }
    }

    val timeZoneId: LiveData<ZoneId> = liveData {
        val timeZoneResult = getTimeZoneUseCase(Unit)
        if (timeZoneResult.successOr(true)) {
            emit(TimeUtils.CONFERENCE_TIMEZONE)
        } else {
            emit(ZoneId.systemDefault())
        }
    }
}

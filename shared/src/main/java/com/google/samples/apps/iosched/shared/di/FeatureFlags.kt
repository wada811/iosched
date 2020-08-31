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

package com.google.samples.apps.iosched.shared.di

import com.google.samples.apps.iosched.shared.data.config.AppConfigDataSource


class FeatureFlags(val appConfigDataSource: AppConfigDataSource) {
    val isExploreArFeatureEnabled: Boolean = appConfigDataSource.isExploreArFeatureEnabled()
    val isMapFeatureEnabled: Boolean get() = appConfigDataSource.isMapFeatureEnabled()
    val isCodelabsFeatureEnabled: Boolean get() = appConfigDataSource.isCodelabsFeatureEnabled()
    val isSearchScheduleFeatureEnabled: Boolean get() = appConfigDataSource.isSearchScheduleFeatureEnabled()
    val isSearchUsingRoomFeatureEnabled: Boolean get() = appConfigDataSource.isSearchUsingRoomFeatureEnabled()
    val isAssistantAppFeatureEnabled: Boolean get() = appConfigDataSource.isAssistantAppFeatureEnabled()
    val isReservationFeatureEnabled: Boolean get() = appConfigDataSource.isReservationFeatureEnabled()
}

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

import com.google.samples.apps.iosched.shared.data.BootstrapConferenceDataSource
import com.google.samples.apps.iosched.shared.data.ConferenceDataSource
import com.google.samples.apps.iosched.shared.data.ar.ArDebugFlagEndpoint
import com.google.samples.apps.iosched.shared.data.ar.DefaultArDebugFlagEndpoint
import com.google.samples.apps.iosched.shared.data.config.AppConfigDataSource
import com.google.samples.apps.iosched.shared.data.config.RemoteAppConfigDataSource
import com.google.samples.apps.iosched.shared.data.feed.AnnouncementDataSource
import com.google.samples.apps.iosched.shared.data.feed.FirestoreAnnouncementDataSource
import com.google.samples.apps.iosched.shared.data.feed.FirestoreMomentDataSource
import com.google.samples.apps.iosched.shared.data.feed.MomentDataSource
import com.google.samples.apps.iosched.shared.data.feedback.DefaultFeedbackEndpoint
import com.google.samples.apps.iosched.shared.data.feedback.FeedbackEndpoint
import com.google.samples.apps.iosched.shared.fcm.FcmTopicSubscriber
import com.google.samples.apps.iosched.shared.fcm.TopicSubscriber

class SharedDependencyModule(
    coroutinesDependencyModule: CoroutinesDependencyModule
) : AbstractSharedDependencyModule(
    coroutinesDependencyModule
) {
    override val bootstrapConfDataSource: ConferenceDataSource by lazy { BootstrapConferenceDataSource }
    override val announcementDataSource: AnnouncementDataSource by lazy {
        FirestoreAnnouncementDataSource(firebaseFirestore)
    }
    override val momentsDataSource: MomentDataSource by lazy {
        FirestoreMomentDataSource(firebaseFirestore)
    }
    override val feedbackEndpoint: FeedbackEndpoint by lazy {
        DefaultFeedbackEndpoint(firebaseFunctions)
    }
    override val arDebugFlagEndpoint: ArDebugFlagEndpoint by lazy {
        DefaultArDebugFlagEndpoint(firebaseFunctions)
    }
    override val topicSubscriber: TopicSubscriber by lazy {
        FcmTopicSubscriber()
    }
    override val appConfigDataSource: AppConfigDataSource by lazy {
        RemoteAppConfigDataSource(firebaseRemoteConfig, coroutinesDependencyModule.ioDispatcher)
    }
}
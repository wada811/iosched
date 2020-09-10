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

import android.content.Context
import com.google.samples.apps.iosched.shared.data.ConferenceDataSource
import com.google.samples.apps.iosched.shared.data.FakeAnnouncementDataSource
import com.google.samples.apps.iosched.shared.data.FakeAppConfigDataSource
import com.google.samples.apps.iosched.shared.data.FakeConferenceDataSource
import com.google.samples.apps.iosched.shared.data.FakeFeedbackEndpoint
import com.google.samples.apps.iosched.shared.data.ar.ArDebugFlagEndpoint
import com.google.samples.apps.iosched.shared.data.ar.FakeArDebugFlagEndpoint
import com.google.samples.apps.iosched.shared.data.config.AppConfigDataSource
import com.google.samples.apps.iosched.shared.data.feed.AnnouncementDataSource
import com.google.samples.apps.iosched.shared.data.feed.FakeMomentDataSource
import com.google.samples.apps.iosched.shared.data.feed.MomentDataSource
import com.google.samples.apps.iosched.shared.data.feedback.FeedbackEndpoint
import com.google.samples.apps.iosched.shared.data.login.datasources.StagingAuthStateUserDataSource
import com.google.samples.apps.iosched.shared.data.login.datasources.StagingRegisteredUserDataSource
import com.google.samples.apps.iosched.shared.data.signin.datasources.AuthIdDataSource
import com.google.samples.apps.iosched.shared.data.signin.datasources.AuthStateUserDataSource
import com.google.samples.apps.iosched.shared.data.signin.datasources.RegisteredUserDataSource
import com.google.samples.apps.iosched.shared.data.userevent.FakeUserEventDataSource
import com.google.samples.apps.iosched.shared.data.userevent.UserEventDataSource
import com.google.samples.apps.iosched.shared.fcm.StagingTopicSubscriber
import com.google.samples.apps.iosched.shared.fcm.TopicSubscriber

class SharedModule(
    context: Context,
    coroutineDispatchers: CoroutineDispatchers = CoroutineDispatchers()
) : AbstractSharedModule(
    context,
    coroutineDispatchers
) {
    override val remoteConfDataSource: ConferenceDataSource by lazy { FakeConferenceDataSource }
    override val bootstrapConfDataSource: ConferenceDataSource by lazy { FakeConferenceDataSource }
    override val announcementDataSource: AnnouncementDataSource by lazy { FakeAnnouncementDataSource }
    override val momentsDataSource: MomentDataSource by lazy { FakeMomentDataSource }
    override val userEventDataSource: UserEventDataSource by lazy { FakeUserEventDataSource }
    override val feedbackEndpoint: FeedbackEndpoint by lazy { FakeFeedbackEndpoint }
    override val arDebugFlagEndpoint: ArDebugFlagEndpoint by lazy { FakeArDebugFlagEndpoint }
    override val topicSubscriber: TopicSubscriber by lazy { StagingTopicSubscriber() }
    override val appConfigDataSource: AppConfigDataSource by lazy { FakeAppConfigDataSource() }
    override val registeredUserDataSource: RegisteredUserDataSource by lazy {
        StagingRegisteredUserDataSource(true)
    }
    override val authStateUserDataSource: AuthStateUserDataSource by lazy {
        StagingAuthStateUserDataSource(
            isRegistered = true,
            isSignedIn = true,
            context = context,
            userId = "StagingTest",
            notificationAlarmUpdater = notificationAlarmUpdater
        )
    }
    override val authIdDataSource: AuthIdDataSource by lazy {
        object : AuthIdDataSource {
            override fun getUserId() = "StagingTest"
        }
    }
}

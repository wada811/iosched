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

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.samples.apps.iosched.shared.R
import com.google.samples.apps.iosched.shared.data.ConferenceDataSource
import com.google.samples.apps.iosched.shared.data.ar.ArDebugFlagEndpoint
import com.google.samples.apps.iosched.shared.data.config.AppConfigDataSource
import com.google.samples.apps.iosched.shared.data.feed.AnnouncementDataSource
import com.google.samples.apps.iosched.shared.data.feed.DefaultFeedRepository
import com.google.samples.apps.iosched.shared.data.feed.FeedRepository
import com.google.samples.apps.iosched.shared.data.feed.MomentDataSource
import com.google.samples.apps.iosched.shared.data.feedback.FeedbackEndpoint
import com.google.samples.apps.iosched.shared.data.userevent.FirestoreUserEventDataSource
import com.google.samples.apps.iosched.shared.data.userevent.UserEventDataSource
import com.google.samples.apps.iosched.shared.fcm.TopicSubscriber
import com.google.samples.apps.iosched.shared.time.DefaultTimeProvider
import com.google.samples.apps.iosched.shared.time.TimeProvider
import com.wada811.dependencyproperty.DependencyModule

abstract class AbstractSharedDependencyModule(
    private val coroutinesDependencyModule: CoroutinesDependencyModule
) : DependencyModule {
    abstract val remoteConfDataSource: ConferenceDataSource
    abstract val bootstrapConfDataSource: ConferenceDataSource
    val firebaseFirestore: FirebaseFirestore by lazy {
        Firebase.firestore.apply {
            // This is to enable the offline data
            // https://firebase.google.com/docs/firestore/manage-data/enable-offline
            firestoreSettings = firestoreSettings { isPersistenceEnabled = true }
        }
    }
    val firebaseFunctions: FirebaseFunctions by lazy { Firebase.functions }
    abstract val announcementDataSource: AnnouncementDataSource
    abstract val momentsDataSource: MomentDataSource
    val feedRepository: FeedRepository by lazy {
        DefaultFeedRepository(announcementDataSource, momentsDataSource)
    }
    val userEventDataSource: UserEventDataSource by lazy {
        FirestoreUserEventDataSource(firebaseFirestore, coroutinesDependencyModule.ioDispatcher)
    }
    abstract val feedbackEndpoint: FeedbackEndpoint
    abstract val arDebugFlagEndpoint: ArDebugFlagEndpoint
    abstract val topicSubscriber: TopicSubscriber
    private val firebaseRemoteConfigSettings: FirebaseRemoteConfigSettings by lazy {
        if (BuildConfig.DEBUG) {
            remoteConfigSettings { minimumFetchIntervalInSeconds = 0 }
        } else {
            remoteConfigSettings { }
        }
    }
    val firebaseRemoteConfig: FirebaseRemoteConfig by lazy {
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(firebaseRemoteConfigSettings)
            setDefaultsAsync(R.xml.remote_config_defaults)
        }
    }
    abstract val appConfigDataSource: AppConfigDataSource
    val timeProvider: TimeProvider by lazy {
        DefaultTimeProvider
    }
}
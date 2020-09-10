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

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
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
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.samples.apps.iosched.shared.BuildConfig
import com.google.samples.apps.iosched.shared.R
import com.google.samples.apps.iosched.shared.data.ConferenceDataRepository
import com.google.samples.apps.iosched.shared.data.ConferenceDataSource
import com.google.samples.apps.iosched.shared.data.agenda.AgendaRepository
import com.google.samples.apps.iosched.shared.data.agenda.DefaultAgendaRepository
import com.google.samples.apps.iosched.shared.data.ar.ArDebugFlagEndpoint
import com.google.samples.apps.iosched.shared.data.codelabs.CodelabsRepository
import com.google.samples.apps.iosched.shared.data.config.AppConfigDataSource
import com.google.samples.apps.iosched.shared.data.db.AppDatabase
import com.google.samples.apps.iosched.shared.data.feed.AnnouncementDataSource
import com.google.samples.apps.iosched.shared.data.feed.DefaultFeedRepository
import com.google.samples.apps.iosched.shared.data.feed.FeedRepository
import com.google.samples.apps.iosched.shared.data.feed.MomentDataSource
import com.google.samples.apps.iosched.shared.data.feedback.FeedbackEndpoint
import com.google.samples.apps.iosched.shared.data.prefs.PreferenceStorage
import com.google.samples.apps.iosched.shared.data.prefs.SharedPreferenceStorage
import com.google.samples.apps.iosched.shared.data.session.DefaultSessionRepository
import com.google.samples.apps.iosched.shared.data.session.SessionRepository
import com.google.samples.apps.iosched.shared.data.signin.datasources.AuthIdDataSource
import com.google.samples.apps.iosched.shared.data.signin.datasources.AuthStateUserDataSource
import com.google.samples.apps.iosched.shared.data.signin.datasources.RegisteredUserDataSource
import com.google.samples.apps.iosched.shared.data.tag.TagRepository
import com.google.samples.apps.iosched.shared.data.userevent.DefaultSessionAndUserEventRepository
import com.google.samples.apps.iosched.shared.data.userevent.SessionAndUserEventRepository
import com.google.samples.apps.iosched.shared.data.userevent.UserEventDataSource
import com.google.samples.apps.iosched.shared.domain.RefreshConferenceDataUseCase
import com.google.samples.apps.iosched.shared.domain.agenda.LoadAgendaUseCase
import com.google.samples.apps.iosched.shared.domain.ar.LoadArDebugFlagUseCase
import com.google.samples.apps.iosched.shared.domain.auth.ObserveUserAuthStateUseCase
import com.google.samples.apps.iosched.shared.domain.codelabs.GetCodelabsInfoCardShownUseCase
import com.google.samples.apps.iosched.shared.domain.codelabs.LoadCodelabsUseCase
import com.google.samples.apps.iosched.shared.domain.codelabs.SetCodelabsInfoCardShownUseCase
import com.google.samples.apps.iosched.shared.domain.feed.GetConferenceStateUseCase
import com.google.samples.apps.iosched.shared.domain.feed.LoadAnnouncementsUseCase
import com.google.samples.apps.iosched.shared.domain.feed.LoadCurrentMomentUseCase
import com.google.samples.apps.iosched.shared.domain.logistics.LoadWifiInfoUseCase
import com.google.samples.apps.iosched.shared.domain.prefs.MarkScheduleUiHintsShownUseCase
import com.google.samples.apps.iosched.shared.domain.prefs.MyLocationOptedInUseCase
import com.google.samples.apps.iosched.shared.domain.prefs.NotificationsPrefIsShownUseCase
import com.google.samples.apps.iosched.shared.domain.prefs.NotificationsPrefSaveActionUseCase
import com.google.samples.apps.iosched.shared.domain.prefs.NotificationsPrefShownActionUseCase
import com.google.samples.apps.iosched.shared.domain.prefs.OnboardingCompleteActionUseCase
import com.google.samples.apps.iosched.shared.domain.prefs.OnboardingCompletedUseCase
import com.google.samples.apps.iosched.shared.domain.prefs.OptIntoMyLocationUseCase
import com.google.samples.apps.iosched.shared.domain.prefs.ScheduleUiHintsShownUseCase
import com.google.samples.apps.iosched.shared.domain.prefs.StopSnackbarActionUseCase
import com.google.samples.apps.iosched.shared.domain.search.FtsMatchStrategy
import com.google.samples.apps.iosched.shared.domain.search.LoadSearchFiltersUseCase
import com.google.samples.apps.iosched.shared.domain.search.SessionSearchUseCase
import com.google.samples.apps.iosched.shared.domain.search.SessionTextMatchStrategy
import com.google.samples.apps.iosched.shared.domain.search.SimpleMatchStrategy
import com.google.samples.apps.iosched.shared.domain.sessions.LoadPinnedSessionsJsonUseCase
import com.google.samples.apps.iosched.shared.domain.sessions.LoadScheduleUserSessionsUseCase
import com.google.samples.apps.iosched.shared.domain.sessions.LoadSessionOneShotUseCase
import com.google.samples.apps.iosched.shared.domain.sessions.LoadStarredAndReservedSessionsUseCase
import com.google.samples.apps.iosched.shared.domain.sessions.LoadUserSessionOneShotUseCase
import com.google.samples.apps.iosched.shared.domain.sessions.LoadUserSessionUseCase
import com.google.samples.apps.iosched.shared.domain.sessions.LoadUserSessionsUseCase
import com.google.samples.apps.iosched.shared.domain.sessions.NotificationAlarmUpdater
import com.google.samples.apps.iosched.shared.domain.sessions.ObserveConferenceDataUseCase
import com.google.samples.apps.iosched.shared.domain.sessions.StarReserveNotificationAlarmUpdater
import com.google.samples.apps.iosched.shared.domain.settings.GetAnalyticsSettingUseCase
import com.google.samples.apps.iosched.shared.domain.settings.GetAvailableThemesUseCase
import com.google.samples.apps.iosched.shared.domain.settings.GetNotificationsSettingUseCase
import com.google.samples.apps.iosched.shared.domain.settings.GetThemeUseCase
import com.google.samples.apps.iosched.shared.domain.settings.GetTimeZoneUseCase
import com.google.samples.apps.iosched.shared.domain.settings.ObserveThemeModeUseCase
import com.google.samples.apps.iosched.shared.domain.settings.SetAnalyticsSettingUseCase
import com.google.samples.apps.iosched.shared.domain.settings.SetThemeUseCase
import com.google.samples.apps.iosched.shared.domain.settings.SetTimeZoneUseCase
import com.google.samples.apps.iosched.shared.domain.speakers.LoadSpeakerUseCase
import com.google.samples.apps.iosched.shared.domain.users.FeedbackUseCase
import com.google.samples.apps.iosched.shared.domain.users.ReservationActionUseCase
import com.google.samples.apps.iosched.shared.domain.users.StarEventAndNotifyUseCase
import com.google.samples.apps.iosched.shared.domain.users.SwapActionUseCase
import com.google.samples.apps.iosched.shared.fcm.TopicSubscriber
import com.google.samples.apps.iosched.shared.notifications.SessionAlarmManager
import com.google.samples.apps.iosched.shared.time.DefaultTimeProvider
import com.google.samples.apps.iosched.shared.time.TimeProvider
import com.google.samples.apps.iosched.shared.util.NetworkUtils
import com.wada811.dependencyproperty.DependencyModule
import com.wada811.dependencyproperty.dependencyModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

abstract class AbstractSharedModule(protected val application: Application) : DependencyModule {
    protected val coroutineModule: CoroutineModule by lazy { application.dependencyModule<CoroutineModule>() }
    val networkUtils: NetworkUtils
        get() = NetworkUtils(application)
    abstract val remoteConfDataSource: ConferenceDataSource
    abstract val bootstrapConfDataSource: ConferenceDataSource
    private val appDatabase: AppDatabase by lazy {
        AppDatabase.buildDatabase(application)
    }
    private val conferenceDataRepository: ConferenceDataRepository by lazy {
        ConferenceDataRepository(remoteConfDataSource, bootstrapConfDataSource, appDatabase)
    }
    abstract val announcementDataSource: AnnouncementDataSource
    abstract val momentsDataSource: MomentDataSource
    private val feedRepository: FeedRepository by lazy {
        DefaultFeedRepository(announcementDataSource, momentsDataSource)
    }
    private val sessionRepository: SessionRepository by lazy {
        DefaultSessionRepository(conferenceDataRepository)
    }
    abstract val userEventDataSource: UserEventDataSource
    abstract val feedbackEndpoint: FeedbackEndpoint
    private val sessionAndUserEventRepository: SessionAndUserEventRepository by lazy {
        DefaultSessionAndUserEventRepository(userEventDataSource, sessionRepository)
    }
    val firebaseFirestore: FirebaseFirestore by lazy {
        Firebase.firestore.apply {
            // This is to enable the offline data
            // https://firebase.google.com/docs/firestore/manage-data/enable-offline
            firestoreSettings = firestoreSettings { isPersistenceEnabled = true }
        }
    }
    val firebaseFunctions: FirebaseFunctions by lazy { Firebase.functions }
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
    val featureFlags: FeatureFlags by lazy {
        FeatureFlags(appConfigDataSource)
    }
    private val sessionTextMatchStrategy: SessionTextMatchStrategy by lazy {
        if (featureFlags.isSearchUsingRoomFeatureEnabled) FtsMatchStrategy(appDatabase) else SimpleMatchStrategy
    }
    private val agendaRepository: AgendaRepository by lazy {
        DefaultAgendaRepository(appConfigDataSource)
    }
    val gson: Gson by lazy {
        GsonBuilder().create()
    }
    val applicationScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + coroutineModule.defaultDispatcher)
    }
    abstract val registeredUserDataSource: RegisteredUserDataSource
    val firebaseAuth: FirebaseAuth by lazy {
        Firebase.auth
    }
    val sessionAlarmManager: SessionAlarmManager by lazy {
        SessionAlarmManager(application)
    }
    val notificationAlarmUpdater: NotificationAlarmUpdater by lazy {
        NotificationAlarmUpdater(sessionAlarmManager, sessionAndUserEventRepository, applicationScope)
    }
    abstract val authStateUserDataSource: AuthStateUserDataSource
    abstract val authIdDataSource: AuthIdDataSource
    val observeUserAuthStateUseCase: ObserveUserAuthStateUseCase by lazy {
        ObserveUserAuthStateUseCase(
            registeredUserDataSource,
            authStateUserDataSource,
            topicSubscriber,
            applicationScope,
            coroutineModule.ioDispatcher
        )
    }
    val preferenceStorage: PreferenceStorage by lazy {
        SharedPreferenceStorage(application)
    }
    val notificationsPrefIsShownUseCase: NotificationsPrefIsShownUseCase by lazy {
        NotificationsPrefIsShownUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    }
    val observeThemeModeUseCase: ObserveThemeModeUseCase
        get() = ObserveThemeModeUseCase(
            preferenceStorage,
            coroutineModule.defaultDispatcher
        )
    val getThemeUseCase: GetThemeUseCase
        get() = GetThemeUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val loadAgendaUseCase: LoadAgendaUseCase
        get() = LoadAgendaUseCase(
            agendaRepository,
            coroutineModule.ioDispatcher
        )
    val getTimeZoneUseCase: GetTimeZoneUseCase
        get() = GetTimeZoneUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    private val codelabsRepository: CodelabsRepository by lazy {
        CodelabsRepository(conferenceDataRepository)
    }
    val loadCodelabsUseCase: LoadCodelabsUseCase
        get() = LoadCodelabsUseCase(
            codelabsRepository,
            coroutineModule.ioDispatcher
        )
    val getCodelabsInfoCardShownUseCase: GetCodelabsInfoCardShownUseCase
        get() = GetCodelabsInfoCardShownUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val setCodelabsInfoCardShownUseCase: SetCodelabsInfoCardShownUseCase
        get() = SetCodelabsInfoCardShownUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val loadAnnouncementsUseCase: LoadAnnouncementsUseCase
        get() = LoadAnnouncementsUseCase(
            feedRepository,
            coroutineModule.ioDispatcher
        )
    val loadCurrentMomentUseCase: LoadCurrentMomentUseCase
        get() = LoadCurrentMomentUseCase(
            feedRepository,
            coroutineModule.ioDispatcher
        )
    val loadStarredAndReservedSessionsUseCase: LoadStarredAndReservedSessionsUseCase
        get() = LoadStarredAndReservedSessionsUseCase(
            sessionAndUserEventRepository,
            coroutineModule.ioDispatcher
        )
    val getConferenceStateUseCase: GetConferenceStateUseCase
        get() = GetConferenceStateUseCase(
            timeProvider,
            coroutineModule.mainDispatcher
        )
    val loadWifiInfoUseCase: LoadWifiInfoUseCase by lazy {
        LoadWifiInfoUseCase(
            appConfigDataSource,
            coroutineModule.ioDispatcher
        )
    }
    val optIntoMyLocationUseCase: OptIntoMyLocationUseCase
        get() = OptIntoMyLocationUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val myLocationOptedInUseCase: MyLocationOptedInUseCase
        get() = MyLocationOptedInUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val onboardingCompleteActionUseCase: OnboardingCompleteActionUseCase
        get() = OnboardingCompleteActionUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val stopSnackbarActionUseCase: StopSnackbarActionUseCase
        get() = StopSnackbarActionUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val loadUserSessionUseCase: LoadUserSessionUseCase
        get() = LoadUserSessionUseCase(
            sessionAndUserEventRepository,
            coroutineModule.ioDispatcher
        )
    private val starReserveNotificationAlarmUpdater: StarReserveNotificationAlarmUpdater by lazy {
        StarReserveNotificationAlarmUpdater(sessionAlarmManager)
    }
    val reservationActionUseCase: ReservationActionUseCase
        get() = ReservationActionUseCase(
            sessionAndUserEventRepository,
            starReserveNotificationAlarmUpdater,
            coroutineModule.ioDispatcher
        )
    val swapActionUseCase: SwapActionUseCase
        get() = SwapActionUseCase(
            sessionAndUserEventRepository,
            coroutineModule.ioDispatcher
        )
    val markScheduleUiHintsShownUseCase: MarkScheduleUiHintsShownUseCase
        get() = MarkScheduleUiHintsShownUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val loadScheduleUserSessionsUseCase: LoadScheduleUserSessionsUseCase
        get() = LoadScheduleUserSessionsUseCase(
            sessionAndUserEventRepository,
            coroutineModule.ioDispatcher
        )
    val starEventAndNotifyUseCase: StarEventAndNotifyUseCase
        get() = StarEventAndNotifyUseCase(
            sessionAndUserEventRepository,
            starReserveNotificationAlarmUpdater,
            coroutineModule.ioDispatcher
        )
    val scheduleUiHintsShownUseCase: ScheduleUiHintsShownUseCase
        get() = ScheduleUiHintsShownUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val refreshConferenceDataUseCase: RefreshConferenceDataUseCase
        get() = RefreshConferenceDataUseCase(
            conferenceDataRepository,
            coroutineModule.ioDispatcher
        )
    val observeConferenceDataUseCase: ObserveConferenceDataUseCase
        get() = ObserveConferenceDataUseCase(
            conferenceDataRepository,
            coroutineModule.ioDispatcher
        )
    val loadUserSessionsUseCase: LoadUserSessionsUseCase
        get() = LoadUserSessionsUseCase(
            sessionAndUserEventRepository,
            coroutineModule.ioDispatcher
        )
    val feedbackUseCase: FeedbackUseCase
        get() = FeedbackUseCase(
            feedbackEndpoint,
            sessionAndUserEventRepository,
            coroutineModule.ioDispatcher
        )
    val setTimeZoneUseCase: SetTimeZoneUseCase
        get() = SetTimeZoneUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val notificationsPrefSaveActionUseCase: NotificationsPrefSaveActionUseCase
        get() = NotificationsPrefSaveActionUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val getNotificationsSettingUseCase: GetNotificationsSettingUseCase
        get() = GetNotificationsSettingUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val setAnalyticsSettingUseCase: SetAnalyticsSettingUseCase
        get() = SetAnalyticsSettingUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val getAnalyticsSettingUseCase: GetAnalyticsSettingUseCase
        get() = GetAnalyticsSettingUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val setThemeUseCase: SetThemeUseCase
        get() = SetThemeUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val getAvailableThemesUseCase: GetAvailableThemesUseCase
        get() = GetAvailableThemesUseCase(
            coroutineModule.mainImmediateDispatcher
        )
    val notificationsPrefShownActionUseCase: NotificationsPrefShownActionUseCase
        get() = NotificationsPrefShownActionUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val loadSpeakerUseCase: LoadSpeakerUseCase
        get() = LoadSpeakerUseCase(
            conferenceDataRepository,
            coroutineModule.ioDispatcher
        )
    val onboardingCompletedUseCase: OnboardingCompletedUseCase
        get() = OnboardingCompletedUseCase(
            preferenceStorage,
            coroutineModule.ioDispatcher
        )
    val loadPinnedSessionsJsonUseCase: LoadPinnedSessionsJsonUseCase
        get() = LoadPinnedSessionsJsonUseCase(
            sessionAndUserEventRepository,
            gson,
            coroutineModule.ioDispatcher
        )
    val loadArDebugFlagUseCase: LoadArDebugFlagUseCase
        get() = LoadArDebugFlagUseCase(
            arDebugFlagEndpoint,
            coroutineModule.ioDispatcher
        )
    val sessionSearchUseCase: SessionSearchUseCase
        get() = SessionSearchUseCase(
            sessionAndUserEventRepository,
            sessionTextMatchStrategy,
            coroutineModule.ioDispatcher
        )
    val loadSearchFiltersUseCase: LoadSearchFiltersUseCase
        get() = LoadSearchFiltersUseCase(
            conferenceDataRepository,
            TagRepository(conferenceDataRepository),
            coroutineModule.ioDispatcher
        )
    val loadUserSessionOneShotUseCase: LoadUserSessionOneShotUseCase
        get() = LoadUserSessionOneShotUseCase(
            sessionAndUserEventRepository,
            coroutineModule.ioDispatcher
        )
    val loadSessionOneShotUseCase: LoadSessionOneShotUseCase
        get() = LoadSessionOneShotUseCase(
            sessionRepository,
            coroutineModule.ioDispatcher
        )
}

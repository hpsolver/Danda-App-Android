package com.sammyekaran.danda.di

import com.sammyekaran.danda.DandaApp
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.utils.Validations
import com.sammyekaran.danda.viewmodel.*
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

object AppModule {
    fun appModule(): Module = module {
        single { SharedPref(get()) }
        single { WebServices() }
        single { Validations() }
        single { CommonUtils() }
        single { DandaApp() }
        viewModel { LoginViewModel(get ()) }
        viewModel { RegisterViewModel() }
        viewModel { HomeViewModel(get()) }
        viewModel { AllTrendingViewModel(get()) }
        viewModel { ProfileViewModel() }
        viewModel { SettingViewModel(get()) }
        viewModel { NotificationViewModel() }
        viewModel { SearchViewModel(get() )}
        viewModel { StartActivityViewModel() }
        viewModel { EditViewModel(get ()) }
        viewModel { PostDetailViewModel(get()) }
        viewModel { FollowUnfollowViewModel(get()) }
        viewModel { CommentViewModel() }
        viewModel { UploadFeedViewModel() }
        viewModel { FriendsViewModel() }
        viewModel { ChangePasswordViewModel(get(),get()) }
        viewModel { SupportViewModel() }
        viewModel { ReportViewModel() }
        viewModel { BlockedViewModel() }
    }
}
package com.sammyekaran.danda.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sammyekaran.danda.viewmodel.*

@Suppress("UNCHECKED_CAST")
class BaseContext(var mBaseActivity: BaseActivity) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(tClass: Class<T>): T {
        return when (tClass) {
            ExploreViewModel::class.java -> ExploreViewModel(mBaseActivity) as T
            ForgotViewModel::class.java -> ForgotViewModel(mBaseActivity) as T
            TrendingViewModel::class.java -> TrendingViewModel(mBaseActivity) as T
            else -> super.create(tClass)
        }
    }
}
package com.wolkowiczmateusz.biometricjetpack

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import com.wolkowiczmateusz.biometricjetpack.mainview.MainViewModel
import com.wolkowiczmateusz.biometricjetpack.mainview.ShowGeneralMessages
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mainViewModel: MainViewModel
    lateinit var activityShowGeneralMessages: ShowGeneralMessages

    override fun  onAttach(context: Context) {
        super.onAttach(context)
        try {
            activityShowGeneralMessages = context as ShowGeneralMessages
        } catch (castException: java.lang.ClassCastException) {
            /** The activity does not implement the listener.  */
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get()
    }
}
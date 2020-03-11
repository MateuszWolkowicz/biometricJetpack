package com.wolkowiczmateusz.biometricjetpack.mainview

import android.os.Build
import android.os.Bundle
import androidx.biometric.BiometricManager
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.wolkowiczmateusz.biometricjetpack.R
import com.wolkowiczmateusz.biometricjetpack.infrastructure.security.BiometricErrorType
import com.wolkowiczmateusz.biometricjetpack.notification.NotificationData
import com.wolkowiczmateusz.biometricjetpack.notification.SimpleNotificationManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.login_screen_fragment.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : ShowGeneralMessages, DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var simpleNotificationManager: SimpleNotificationManager

    private lateinit var mainViewModel: MainViewModel

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get()
        navController = findNavController(R.id.host_nav_fragment)

        mainViewModel.countdownNotification.observe(this, Observer {
            it.getContentIfNotHandled()?.let { shouldShowNotification ->
                if (shouldShowNotification) {
                    showNotification()
                }
            }
        })
        mainViewModel.startNotificationTimer()

        mainViewModel.biometricEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let { isPossibleToCheckBiometrics ->
                if (isPossibleToCheckBiometrics) {
                    mainViewModel.isNewBiometric()
                }
            }
        })

        mainViewModel.isBiometricStillAvailable.observe(this, Observer {
            it.getContentIfNotHandled()?.let { biometricCryptoObject ->
                if (biometricCryptoObject.biometricCryptoObject == null) {
                    when (biometricCryptoObject.biometricErrorType) {
                        BiometricErrorType.KEY_PERMANENTLY_INVALIDATED -> {
                            mainViewModel.clearAllData()
                            showSnackBarWithCloseButton(getString(R.string.new_biometric))
                        }
                        else -> {
                        }
                    }
                }
            }
        })

        mainViewModel.logoutEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let { isLogout ->
                if (isLogout) {
                    navigateToLoginScreen()
                }
            }
        })
        networkStateListener()
    }

    private fun networkStateListener() {
        mainViewModel.internetState.observe(this, Observer { isConnected ->
            if (!isConnected) {
                showSnackBar(getString(R.string.internet_connection_problem))
            }
        })
    }

    override fun showSnackBarWithCloseButton(message: String) {
        val snackbar =
            Snackbar.make(container, message, Snackbar.LENGTH_INDEFINITE)
                .apply {
                    setAction(getString(R.string.ok)) { dismiss() }
                }
        snackbar.show()
    }

    override fun showSnackBar(message: String) {
        val snackbar =
            Snackbar.make(container, message, Snackbar.LENGTH_INDEFINITE)
                .apply {
                    setAction(getString(R.string.ok)) { dismiss() }
                }
        snackbar.show()
    }

    override fun onResume() {
        super.onResume()
        observeInternetConnectivityGlobally()
        checkIfBiometricIsAvailable()
    }

    private fun observeInternetConnectivityGlobally() {
        mainViewModel.observeInternetState()
    }

    private fun showNotification() {
        val notificationId = 1123134324
        val simpleNotification = NotificationData(
            getString(R.string.notification_channel_name),
            getString(R.string.notification_channel_description),
            getString(R.string.notification_biometric_jetpack_title),
            getString(R.string.notification_description)
        )
        val notification =
            simpleNotificationManager.createSimpleNotification(this, simpleNotification)
        Timber.d("Notification should be shown")

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, notification)
        }
    }

    private fun checkIfBiometricIsAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val biometricManager = BiometricManager.from(application)
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    mainViewModel.tryToUseBiometric()
                }
                else -> {
                }
            }
        }
    }

    private fun navigateToLoginScreen() {
        navController.navigate(R.id.action_global_login_screen)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.stopNotificationTimer()
    }

}

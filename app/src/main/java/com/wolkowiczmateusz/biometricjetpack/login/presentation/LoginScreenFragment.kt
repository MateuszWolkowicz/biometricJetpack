package com.wolkowiczmateusz.biometricjetpack.login.presentation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.wolkowiczmateusz.biometricjetpack.BaseFragment
import com.wolkowiczmateusz.biometricjetpack.R
import com.wolkowiczmateusz.biometricjetpack.infrastructure.extensions.openSecuritySettings
import com.wolkowiczmateusz.biometricjetpack.infrastructure.extensions.showToast
import com.wolkowiczmateusz.biometricjetpack.infrastructure.security.BiometricErrorType
import com.wolkowiczmateusz.biometricjetpack.infrastructure.security.BiometricState
import com.wolkowiczmateusz.biometricjetpack.login.model.ErrorType
import com.wolkowiczmateusz.biometricjetpack.login.model.LoginFormState
import com.wolkowiczmateusz.biometricjetpack.login.model.LoginResult
import kotlinx.android.synthetic.main.login_screen_fragment.*
import java.util.concurrent.Executor


class LoginScreenFragment : BaseFragment() {

    private lateinit var loginViewModel: LoginScreenViewModel
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executor = ContextCompat.getMainExecutor(context)
        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_screen_fragment, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        tryToUseBiometric()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is LoginResult.Loading -> handleLoading()
                is LoginResult.Success -> handleSuccess(state.loginFormState)
                is LoginResult.Error -> handleError(state.loginFormState)
            }
        })

        loginViewModel.biometricEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { biometricState ->
                when (biometricState) {
                    BiometricState.READY_TO_USE -> showBiometricOrClearDataIfNewExist()
                    BiometricState.USER_REGISTERED -> showBiometricOrClearDataIfNewExist()
                    BiometricState.USER_NOT_REGISTERED -> showNotRegisteredUserMessage()
                    BiometricState.BIOMETRIC_ERROR -> showBiometricErrorMessage()
                    BiometricState.BIOMETRIC_NOT_SETUP -> setUpBiometric()
                }
            }
        })

        loginViewModel.isBiometricStillAvailable.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { biometricCryptoObject ->
                if (biometricCryptoObject.biometricCryptoObject != null) {
                    biometricLogin(biometricCryptoObject.biometricCryptoObject!!)
                } else {
                    when (biometricCryptoObject.biometricErrorType) {
                        BiometricErrorType.KEY_PERMANENTLY_INVALIDATED -> {
                            username.setText("")
                            username.isEnabled = true
                            loginViewModel.clearAllData()
                            activityShowGeneralMessages.showSnackBarWithCloseButton(getString(R.string.new_biometric))
                        }
                        BiometricErrorType.OTHER_BIOMETRIC_ERROR -> showBiometricErrorMessage()
                    }
                }
            }
        })

        loginViewModel.getRegisteredUser()

        login.setOnClickListener {
            loginViewModel.login(username.text.toString(), password.text.toString())
        }

        biometric_login.setOnClickListener {
            checkIfBiometricIsAvailable()
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.logoutUser()
    }

    private fun showBiometricErrorMessage() {
        getString(R.string.biometric_error).showToast(activity!!)
    }

    private fun showNotRegisteredUserMessage() {
        getString(R.string.no_registered_user).showToast(activity!!)
    }

    private fun setUpBiometric() {
        Snackbar.make(container, R.string.sign_up_snack_message, Snackbar.LENGTH_LONG)
            .setAction(R.string.sign_up_snack_action) { context!!.openSecuritySettings() }
            .show()
    }

    private fun handleError(loginFormState: LoginFormState?) {
        loading.visibility = View.GONE

        loginFormState?.errorType?.let {
            val internalErrorMessage = convertToMessage(it)
            internalErrorMessage.showToast(activity!!)
        }

        loginFormState?.emailError?.let {
            username.error = getString(it)
        }

        loginFormState?.passwordError?.let {
            password.error = getString(it)
        }
    }

    private fun convertToMessage(error: ErrorType): String {
        return when (error) {
            ErrorType.APP_INTERNAL -> getString(R.string.error_other)
            ErrorType.APP_OTHER -> getString(R.string.error_other)
            ErrorType.SERVER_CONNECTION -> getString(R.string.error_connection)
            ErrorType.SERVER_INTERNAL -> getString(R.string.error_server)
            ErrorType.SERVER_TIMEOUT -> getString(R.string.error_limit)
            ErrorType.SERVER_OTHER -> getString(R.string.error_other)
        }
    }

    private fun handleSuccess(loginFormState: LoginFormState?) {
        loading.visibility = View.GONE
        loginFormState?.let {
            if (!it.userEmail.isNullOrEmpty()) {
                username.setText(it.userEmail)
                username.isEnabled = false
                return
            }
        }
        username.isEnabled = true
        navigateToRepoList()
    }

    private fun navigateToRepoList() {
        mainViewModel.logInUser()
        val repoList =
            LoginScreenFragmentDirections.actionLoginScreenToGithubReposScreen()
        findNavController().navigate(repoList)
    }

    private fun handleLoading() {
        loading.visibility = View.VISIBLE
    }

    private fun biometricLogin(biometricCryptoObject: BiometricPrompt.CryptoObject) {
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        context,
                        getString(R.string.authentication_error) + "$errString", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    navigateToRepoList()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                        getString(R.string.authentication_failed).showToast(activity!!)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_login_biometric_jetpack))
            .setSubtitle(getString(R.string.login_using_biometric))
            .setNegativeButtonText(getString(R.string.use_account_password))
            .build()

        biometricPrompt.authenticate(promptInfo, biometricCryptoObject)
    }

    private fun checkIfBiometricIsAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val biometricManager = BiometricManager.from(activity!!.application)
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    loginViewModel.tryToUseBiometric()
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    showBiometricHardwareErrorMessage()
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    showBiometricHardwareErrorMessage()
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    setUpBiometric()
                }
                else -> {
                    showBiometricErrorMessage()
                }
            }
        } else {
            getString(R.string.biometric_not_available_on_device).showToast(activity!!)
        }
    }

    private fun showBiometricHardwareErrorMessage() {
        getString(R.string.biometric_hardware_error).showToast(activity!!)
    }

    private fun tryToUseBiometric() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val biometricManager = BiometricManager.from(activity!!.application)
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    loginViewModel.tryToUseBiometric()
                }
                else -> {
                }
            }
        }
    }

    private fun showBiometricOrClearDataIfNewExist() {
        loginViewModel.isNewBiometric()
    }

}

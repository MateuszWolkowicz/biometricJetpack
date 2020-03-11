package com.wolkowiczmateusz.biometricjetpack.login.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.wolkowiczmateusz.biometricjetpack.NEW_EMPTY_USER
import com.wolkowiczmateusz.biometricjetpack.NEW_VALID_USER
import com.wolkowiczmateusz.biometricjetpack.infrastructure.InternalErrorConverter
import com.wolkowiczmateusz.biometricjetpack.infrastructure.security.BiometricState
import com.wolkowiczmateusz.biometricjetpack.login.data.LoginRepository
import com.wolkowiczmateusz.biometricjetpack.login.model.LoginFormState
import com.wolkowiczmateusz.biometricjetpack.login.model.LoginResult
import com.wolkowiczmateusz.biometricjetpack.repos.data.GithubRepository
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LoginScreenViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var loginRepository: LoginRepository

    @MockK
    lateinit var githubRepository: GithubRepository

    @MockK
    lateinit var internalErrorConverter: InternalErrorConverter

    @InjectMockKs
    private lateinit var loginScreenViewModel: LoginScreenViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }

    @Test
    fun `should create new user if data are correct`() {
        val newUser = NEW_VALID_USER
        val testEmail = "test@wp.pl"
        val testPassword = "12345678"
        every { loginRepository.login(testEmail,testPassword) } returns Single.just(newUser)
        loginScreenViewModel.login(testEmail,testPassword)
        loginScreenViewModel.loginResult.observeForever {
            assertThat(it is LoginResult.Success).isTrue()
            assertThat((it as LoginResult.Success).loginFormState).isEqualTo(LoginFormState(isDataValid = true))
        }
        verify(exactly = 1) { loginRepository.login(testEmail,testPassword) }
    }

    @Test
    fun `if password to short should show error`() {
        val newUser = NEW_VALID_USER
        val testEmail = "test@wp.pl"
        val testPassword = "123"
        every { loginRepository.login(testEmail,testPassword) } returns Single.just(newUser)
        loginScreenViewModel.login(testEmail,testPassword)
        loginScreenViewModel.loginResult.observeForever {
            assertThat(it is LoginResult.Error).isTrue()
            assertThat((it as LoginResult.Error).loginFormState!!.isDataValid).isFalse()
            assertThat(it.loginFormState!!.passwordError).isNotNull()
            assertThat(it.loginFormState!!.emailError).isNull()
            assertThat(it.loginFormState!!.userEmail).isNull()
        }
        verify(exactly = 0) { loginRepository.login(testEmail,testPassword) }
    }

    @Test
    fun `if empty user show error`() {
        val newUser = NEW_EMPTY_USER
        val testEmail = "test@wp.pl"
        val testPassword = "12345678"
        every { loginRepository.login(testEmail,testPassword) } returns Single.just(newUser)
        loginScreenViewModel.login(testEmail,testPassword)
        loginScreenViewModel.loginResult.observeForever {
            assertThat(it is LoginResult.Error).isTrue()
        }
    }

    @Test
    fun `if user not registered yet and trying to use biometric show not registered`() {
        val newUser = NEW_EMPTY_USER
        every { loginRepository.getRegisteredUser() } returns Single.just(newUser)
        loginScreenViewModel.tryToUseBiometric()
        loginScreenViewModel.loginResult.observeForever {
            assertThat(it is LoginResult.Error).isTrue()
        }
        loginScreenViewModel.biometricEvent.observeForever {
            assertThat(it.getContentIfNotHandled()).isEqualTo(BiometricState.USER_NOT_REGISTERED)
        }
    }

    @Test
    fun `if user registered and trying to use biometric show registered`() {
        val newUser = NEW_VALID_USER
        every { loginRepository.getRegisteredUser() } returns Single.just(newUser)
        every { loginRepository.createBiometricKey() } just Runs
        loginScreenViewModel.tryToUseBiometric()
        loginScreenViewModel.loginResult.observeForever {
            assertThat(it is LoginResult.Error).isTrue()
        }
        loginScreenViewModel.biometricEvent.observeForever {
            assertThat(it.getContentIfNotHandled()).isEqualTo(BiometricState.USER_REGISTERED)
        }
    }

    @Test
    fun `if user registered show its data`() {
        val newUser = NEW_VALID_USER
        every { loginRepository.getRegisteredUser() } returns Single.just(newUser)
        loginScreenViewModel.getRegisteredUser()
        loginScreenViewModel.loginResult.observeForever {
            assertThat(it is LoginResult.Success).isTrue()
            assertThat((it as LoginResult.Success).loginFormState!!.userEmail).isEqualTo(NEW_VALID_USER.name)
        }
    }
}
package com.example.findyou.ui.login

import android.content.Intent
import android.util.Patterns
import android.view.WindowManager
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.findyou.R
import com.example.findyou.data.LoginRepository
import com.example.findyou.data.Result
import com.example.findyou.registration.Registration

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

        // can be launched in a separate asynchronous job
        fun registration(intent: Intent) {


            //per Fragment
//            FragmentActivity.requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//            fragmentTransaction.replace(R.id.container, Registration())
//            fragmentTransaction.addToBackStack(null)
//            fragmentTransaction.commit()
        }


    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
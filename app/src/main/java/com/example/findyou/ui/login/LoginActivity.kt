package com.example.findyou.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.findyou.R
import com.example.findyou.databinding.ActivityLoginBinding
import com.example.findyou.registration.Registration
import com.facebook.*
import com.facebook.CallbackManager.Factory.create
import com.facebook.appevents.AppEventsLogger


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private var callbackManager = create()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        callbackManager : CallbackManager.Factory.create();

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val registration = binding.registrationButton
        val loading = binding.loading

        val loginButtonFacebook = binding.loginButtonFacebook

        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(application)


         callbackManager = CallbackManager.Factory.create();

        // Set the initial permissions to request from the user while logging in
        if (loginButtonFacebook != null) {
            loginButtonFacebook.permissions= listOf(EMAIL, USER_POSTS)
        }
        if (loginButtonFacebook != null) {
            loginButtonFacebook.authType= AUTH_TYPE
        }

        // Callback registration
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        // Callback registration
        if (loginButtonFacebook != null) {
            loginButtonFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                fun onSuccess(loginResult: LoginResult) {
                    // App code
                }

                override fun onCancel() {
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    // App code
                }
            })
        }

        if (loginButtonFacebook != null) {
            loginButtonFacebook.registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        setResult(RESULT_OK)
                        finish()
                    }

                    override fun onCancel() {
                        setResult(RESULT_CANCELED)
                        finish()
                    }

                    override fun onError(error: FacebookException) {
                        // Handle exception
                    }
                })
        }

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }

            registration?.setOnClickListener {

                val intent = Intent(applicationContext, Registration::class.java)
                startActivity(intent)

            }
        }


    }


    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
//         TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
//        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
//}

    /**
     * Extension function to simplify setting an afterTextChanged action to EditText components.
     */
    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    companion object {
        private const val EMAIL = "email"
        private const val USER_POSTS = "user_posts"
        private const val AUTH_TYPE = "rerequest"
    }
}



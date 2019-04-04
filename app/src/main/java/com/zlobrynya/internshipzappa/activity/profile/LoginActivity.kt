package com.zlobrynya.internshipzappa.activity.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.authDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.authRespDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    var canClickRegisterButton: Boolean = true
    var canClickForgotButton: Boolean = true
    var canClickLoginButton: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.zlobrynya.internshipzappa.R.layout.activity_login)

        supportActionBar!!.title = "Вход"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setBackgroundDrawable(resources.getDrawable(com.zlobrynya.internshipzappa.R.drawable.actionbar))
        supportActionBar!!.elevation = 0F

        val newAuth = authDTO()


        btnLinkToRegisterActivity.setOnClickListener {
            if (canClickRegisterButton) {
                canClickRegisterButton = false
                val i = Intent(this, RegisterActivity::class.java)
                startActivity(i)
            }
        }

        val icon = resources.getDrawable(com.zlobrynya.internshipzappa.R.drawable.error)

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        log_email.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (!hasFocus) {
                    hideKeyboard(v!!)
                }
            }
        }

        log_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                log_email.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val email = log_email_input_layout.editText!!.text.toString()
                        val validateEmail = validateEmail(email)

                        if (!hasFocus) {
                            hideKeyboard(v!!)
                        }

                        if (!hasFocus && !validateEmail) {
                            log_email_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_email)
                            log_email.setCompoundDrawables(null, null, icon, null)
                        } else {
                            log_email_input_layout.isErrorEnabled = false
                            log_email.setCompoundDrawables(null, null, null, null)
                        }
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        log_password.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (!hasFocus) {
                    hideKeyboard(v!!)
                }
            }
        }

        log_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                log_password.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val password = log_password_input_layout.editText!!.text.toString()
                        val validatePassword = validatePassword(password)

                        if (!hasFocus) {
                            hideKeyboard(v!!)
                        }

                        if (!hasFocus && !validatePassword) {
                            log_password_input_layout.error =
                                getString(com.zlobrynya.internshipzappa.R.string.error_password)
                            log_password.setCompoundDrawables(null, null, icon, null)
                        } else {
                            log_password_input_layout.isErrorEnabled = false
                            log_password.setCompoundDrawables(null, null, null, null)
                        }
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        forgot_password.setOnClickListener {
            if (canClickForgotButton) {
                canClickForgotButton = false
                val intent = Intent(this, PasswordRecovery::class.java)
                startActivity(intent)
            }
        }

        btnLogin.setOnClickListener {
            if (canClickLoginButton) {
                canClickLoginButton = false
                login(newAuth, icon)
            }
        }
    }

    private fun login(newAuth: authDTO, icon: Drawable) {
        val email = log_email_input_layout.editText!!.text.toString()
        val password = log_password_input_layout.editText!!.text.toString()

        val validateEmail = validateEmail(email)
        val validatePassword = validatePassword(password)

        newAuth.email = email
        newAuth.password = password
        if (validateEmail && validatePassword) {

            RetrofitClientInstance.getInstance()
                .postAuthData(newAuth)
                .subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : Observer<Response<authRespDTO>> {

                    override fun onComplete() {}

                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(t: Response<authRespDTO>) {
                        if (t.isSuccessful) {
                            val sharedPreferencesStat = applicationContext.getSharedPreferences(
                                applicationContext.getString(com.zlobrynya.internshipzappa.R.string.user_info),
                                Context.MODE_PRIVATE
                            )
                            val savedEmail =
                                applicationContext.getString(com.zlobrynya.internshipzappa.R.string.user_email)
                            val access_token =
                                applicationContext.getString(com.zlobrynya.internshipzappa.R.string.access_token)
                            val editor = sharedPreferencesStat.edit()
                            editor.putString(savedEmail, t.body()!!.email)
                            editor.putString(access_token, t.body()!!.access_token)
                            editor.apply()

                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            log_email_input_layout.error =
                                getString(com.zlobrynya.internshipzappa.R.string.wrong_password_email)
                            log_email.setCompoundDrawables(null, null, icon, null)
                            log_password_input_layout.error =
                                getString(com.zlobrynya.internshipzappa.R.string.wrong_password_email)
                            log_password.setCompoundDrawables(null, null, icon, null)
                            Toast.makeText(this@LoginActivity, "Неверный пароль или E-mail", Toast.LENGTH_SHORT)
                                .show()
                            canClickLoginButton = true
                        }
                    }

                    override fun onError(e: Throwable) {
                        showNoInternetConnectionAlert(newAuth, icon)
                    }

                })

        } else canClickLoginButton = true
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        return password.matches("((?=.*[a-z0-9]).{4,20})".toRegex())
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed() {
        /*super.onBackPressed()
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()*/

        super.onBackPressed()
        // Закрываем активити с кодом RESULT_CANCELED если юзер закрыл авторизацию
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        // Закрываем активити с кодом RESULT_CANCELED если юзер закрыл авторизацию
        setResult(Activity.RESULT_CANCELED)
        finish()
        return true
    }

    override fun onResume() {
        canClickRegisterButton = true
        canClickLoginButton = true
        canClickForgotButton = true
        alertIsShown = false
        super.onResume()
    }

    /**
     * Обновляет список броней юзера
     */
    private fun updateUserBookingList() {
        val fragment = supportFragmentManager.findFragmentByTag("USER_BOOKING_LIST")
        if (fragment != null) {
            fragment.onResume()
        }
    }

    private var alertIsShown = false
    /**
     * Выводит диалоговое окно с сообщением об отсутствии интернета
     */
    private fun showNoInternetConnectionAlert(authDTO: authDTO, icon: Drawable) {
        if (!alertIsShown) {
            alertIsShown = true
            val builder: AlertDialog.Builder =
                AlertDialog.Builder(this, com.zlobrynya.internshipzappa.R.style.AlertDialogCustom)
            builder.setTitle("Ошибка соединения")
                .setMessage("Без подключения к сети невозможно продолжить бронирование.\nПроверьте соединение и попробуйте снова")
                .setCancelable(false)
                .setPositiveButton("ПОВТОРИТЬ") { dialog, which ->
                    run {
                        dialog.dismiss()
                        login(authDTO, icon)
                        alertIsShown = false
                    }
                }
                .setNegativeButton("ОТМЕНА") { dialog, which ->
                    run {
                        dialog.dismiss()
                        alertIsShown = false
                        canClickLoginButton = true
                    }
                }
            val alert = builder.create()
            alert.show()
            alert.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(com.zlobrynya.internshipzappa.R.color.color_accent))
            alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(com.zlobrynya.internshipzappa.R.color.color_accent))
        }
    }
}

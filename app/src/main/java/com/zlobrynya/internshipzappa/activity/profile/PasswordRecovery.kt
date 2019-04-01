package com.zlobrynya.internshipzappa.activity.profile

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyEmailDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyRespDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_password_recovery.*
import retrofit2.Response

class PasswordRecovery : AppCompatActivity() {

    var canClick: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_recovery)
        supportActionBar!!.title = "Восстановление пароля"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val icon: Drawable = resources.getDrawable(com.zlobrynya.internshipzappa.R.drawable.error)

        val newVerify = verifyEmailDTO()

        register_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                register_email.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = register_email_input_layout.editText!!.text.toString()
                val validateEmail = validateEmail(email)

                if (!validateEmail) {
                    register_email_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_email)
                    register_email.setCompoundDrawables(null, null, icon, null)
                    btnSendPassChange.background = resources.getDrawable(R.drawable.btn_not_click)
                } else {
                    register_email_input_layout.isErrorEnabled = false
                    register_email.setCompoundDrawables(null, null, null, null)
                    btnSendPassChange.background = resources.getDrawable(R.drawable.btn_can_click)
                }
            }

        })

        btnSendPassChange.setOnClickListener {
            val email = register_email_input_layout.editText!!.text.toString()
            val validateEmail = validateEmail(email)
            if (validateEmail) {
                newVerify.email = email
                btnSendPassChange.background = resources.getDrawable(R.drawable.btn_can_click)
                if (canClick) {
                    canClick = false
                    checkExistenceEmail(newVerify, icon)
                }
            } else {
                Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show()
                btnSendPassChange.background = resources.getDrawable(R.drawable.btn_not_click)
            }
/*
            val intent = Intent(this, PasswordChange::class.java)
            finish()
            startActivity(intent)
*/
        }
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    private fun checkExistenceEmail(newVerify: verifyEmailDTO, icon: Drawable) {
        progress_spinner_recovery.visibility = View.VISIBLE
        RetrofitClientInstance.getInstance()
            .getEmailExistence(newVerify.email)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<respDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<respDTO>) {
                    if (t.isSuccessful) {
                        verifyEmail(newVerify, icon)
                    } else {
                        register_email_input_layout.error =
                            getString(com.zlobrynya.internshipzappa.R.string.user_not_exist)
                        register_email.setCompoundDrawables(null, null, icon, null)
                        progress_spinner_recovery.visibility = View.GONE
                        canClick = true
                    }
                }

                override fun onError(e: Throwable) {
                    progress_spinner_recovery.visibility = View.GONE
                    showNoInternetConnectionAlert(newVerify, icon)
                }
            })
    }

    private fun verifyEmail(newVerify: verifyEmailDTO, icon: Drawable) {
        progress_spinner_recovery.visibility = View.VISIBLE
        RetrofitClientInstance.getInstance()
            .postVerifyData(newVerify)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<verifyRespDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<verifyRespDTO>) {
                    if (passwordRecoveryActivityIsRunning) {
                        if (t.isSuccessful) {
                            val intent = Intent(applicationContext, PasswordChange::class.java)
                            intent.putExtra("email", newVerify.email)
                            startActivity(intent)
                        }
                        progress_spinner_recovery.visibility = View.GONE
                    }
                }

                override fun onError(e: Throwable) {
                    progress_spinner_recovery.visibility = View.GONE
                    showNoInternetConnectionAlert(newVerify, icon)
                }

            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        canClick = true
        passwordRecoveryActivityIsRunning = true
    }

    override fun onStop() {
        super.onStop()
        passwordRecoveryActivityIsRunning = false
    }

    companion object {
        var passwordRecoveryActivityIsRunning: Boolean = false
    }

    private var alertIsShown = false
    /**
     * Выводит диалоговое окно с сообщением об отсутствии интернета
     */
    private fun showNoInternetConnectionAlert(newVerify: verifyEmailDTO, icon: Drawable) {
        if (!alertIsShown) {
            alertIsShown = true
            val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle("Ошибка соединения")
                .setMessage("Без подключения к сети невозможно продолжить бронирование.\nПроверьте соединение и попробуйте снова")
                .setCancelable(false)
                .setPositiveButton("ПОВТОРИТЬ") { dialog, which ->
                    run {
                        dialog.dismiss()
                        alertIsShown = false
                        checkExistenceEmail(newVerify, icon)
                    }
                }
                .setNegativeButton("ОТМЕНА") { dialog, which ->
                    run {
                        dialog.dismiss()
                        alertIsShown = false
                        canClick = true
                    }
                }
            val alert = builder.create()
            alert.show()
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.color_accent))
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.color_accent))
        }
    }
}
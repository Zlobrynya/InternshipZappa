package com.zlobrynya.internshipzappa.activity.profile

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.CheckDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyEmailDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyRespDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_password_recovery.*
import kotlinx.android.synthetic.main.activity_register.*
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
                    Log.i("checkEmailExistence", t.code().toString())
                    if (t.isSuccessful) {
                        verifyEmail(newVerify)
                    } else {
                        register_email_input_layout.error =
                            getString(com.zlobrynya.internshipzappa.R.string.user_not_exist)
                        register_email.setCompoundDrawables(null, null, icon, null)
                        progress_spinner_recovery.visibility = View.GONE
                        canClick = true
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i("checkReg", "that's not fineIn")
                }
            })
    }

    private fun verifyEmail(newVerify: verifyEmailDTO) {

        progress_spinner_recovery.visibility = View.VISIBLE
        RetrofitClientInstance.getInstance()
            .postVerifyData(newVerify)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<verifyRespDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<verifyRespDTO>) {
                    Log.i("checkCode", "${t.code()}")
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
                    Log.i("check", "that's not fineIn")
                }

            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        Log.d("BOOP", "password start")
        super.onStart()
        canClick = true
        passwordRecoveryActivityIsRunning = true
    }

    override fun onStop() {
        Log.d("BOOP", "password stop")
        super.onStop()
        passwordRecoveryActivityIsRunning = false
    }

    companion object {
        var passwordRecoveryActivityIsRunning: Boolean = false
    }
}
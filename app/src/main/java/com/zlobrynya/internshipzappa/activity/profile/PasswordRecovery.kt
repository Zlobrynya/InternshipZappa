package com.zlobrynya.internshipzappa.activity.profile

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.CheckDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyEmailDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_password_recovery.*
import retrofit2.Response

class PasswordRecovery: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_recovery)
        supportActionBar!!.title = "Восстановление пароля"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val icon = resources.getDrawable(com.zlobrynya.internshipzappa.R.drawable.error)

        val newVerify = verifyEmailDTO()

        register_email.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                register_email.onFocusChangeListener = object : View.OnFocusChangeListener{
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        /*
                        val email = register_email_input_layout.editText!!.text.toString()
                        val validateEmail = validateEmail(email)

                        if (!validateEmail) {
                            register_email_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_email)
                            register_email.setCompoundDrawables(null, null, icon, null)
                        } else {
                            register_email_input_layout.isErrorEnabled = false
                            register_email.setCompoundDrawables(null, null, null, null)
                            btnSendPassChange.background = resources.getDrawable(R.drawable.btn_can_click)

                        }
                        */
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
                checkUserExist(newVerify.email, icon)
            } else {
                Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show()
                btnSendPassChange.background = resources.getDrawable(R.drawable.btn_not_click)
            }

            val intent = Intent(this, PasswordChange::class.java)
            finish()
            startActivity(intent)

        }
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun checkUserExist(userEmail: String, icon: Drawable){
        RetrofitClientInstance.getInstance()
            .getUserExists(userEmail)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<respDTO>>{
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Response<respDTO>) {
                    if (t.isSuccessful){
                        register_email_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.user_not_exist)
                        register_email.setCompoundDrawables(null, null, icon, null)
                    }
                }

                override fun onError(e: Throwable) {

                }

            })
    }
}
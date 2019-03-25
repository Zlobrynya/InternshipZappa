package com.zlobrynya.internshipzappa.activity.profile

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.Menu2Activity
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.regDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyEmailDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.Observer
import kotlinx.android.synthetic.main.activity_personal_info.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Response
import java.util.*

class RegisterActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar!!.title = "Регистрация"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setBackgroundDrawable(resources.getDrawable(R.drawable.actionbar))
        supportActionBar!!.elevation = 0F

        val icon = resources.getDrawable(com.zlobrynya.internshipzappa.R.drawable.error)

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        reg_username.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                reg_username.onFocusChangeListener = object: View.OnFocusChangeListener{
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        if (!hasFocus) {
                            val name = reg_username_input_layout.editText!!.text.toString()
                            val validateName = validateName(name)

                            if (!validateName) {
                                reg_username_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_name)
                                reg_username.setCompoundDrawables(null, null, icon, null)
                            } else {
                                reg_username_input_layout.isErrorEnabled = false
                                reg_username.setCompoundDrawables(null, null, null, null)
                            }
                        }
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        reg_phone_number.addTextChangedListener(object: TextWatcher{

            private var mFormatting: Boolean = false
            private var mAfter: Int = 0

            override fun afterTextChanged(s: Editable?) {
                reg_phone_number.onFocusChangeListener = object: View.OnFocusChangeListener{
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        if (!mFormatting) {
                            mFormatting = true
                            if (mAfter != 0) {
                                val num = s.toString()
                                val data = PhoneNumberUtils.formatNumber(num, "RU")
                                if (data != null) {
                                    s!!.clear()
                                    s.append(data)
                                }
                            }
                            mFormatting = false
                        }

                        val phone = reg_phone_number_input_layout.editText!!.text.toString()
                        val validatePhone = validatePhone(phone)

                        if (!validatePhone) {
                            reg_phone_number_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_phone)
                            reg_phone_number.setCompoundDrawables(null, null, icon, null)
                        } else {
                            reg_phone_number_input_layout.isErrorEnabled = false
                            reg_phone_number.setCompoundDrawables(null, null, null, null)
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                mAfter  = after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        reg_email.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                reg_email.onFocusChangeListener = object : View.OnFocusChangeListener{
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val email = reg_email_input_layout.editText!!.text.toString()
                        val validateEmail = validateEmail(email)

                        if (!validateEmail) {
                            reg_email_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_email)
                            reg_email.setCompoundDrawables(null, null, icon, null)
                        } else {
                            reg_email_input_layout.isErrorEnabled = false
                            reg_email.setCompoundDrawables(null, null, null, null)
                        }
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        reg_password.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                reg_password.onFocusChangeListener = object : View.OnFocusChangeListener{
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val password = reg_password_input_layout.editText!!.text.toString()
                        val validatePassword = validatePassword(password)

                        if (!validatePassword) {
                            reg_password_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_password)
                            reg_password.setCompoundDrawables(null, null, icon, null)
                        } else {
                            reg_password_input_layout.isErrorEnabled = false
                            reg_password.setCompoundDrawables(null, null, null, null)
                        }
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        reg_confirm_password.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                reg_confirm_password.onFocusChangeListener = object : View.OnFocusChangeListener{
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val password = reg_password_input_layout.editText!!.text.toString()
                        val confirmPassword = reg_confirm_password_input_layout.editText!!.text.toString()
                        val validateConfirmPassword = validateConfirmPassword(password, confirmPassword)

                        if (!validateConfirmPassword) {
                            reg_confirm_password_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_confirm_password)
                            reg_confirm_password.setCompoundDrawables(null, null, icon, null)
                        } else {
                            reg_confirm_password_input_layout.isErrorEnabled = false
                            reg_confirm_password.setCompoundDrawables(null, null, null, null)
                        }
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        btnRegister.setOnClickListener {
           /* val name = reg_username_input_layout.editText!!.text.toString()
            val phone = reg_phone_number_input_layout.editText!!.text.toString()
            val email = reg_email_input_layout.editText!!.text.toString()
            val password = reg_password_input_layout.editText!!.text.toString()
            val confirmPassword = reg_confirm_password_input_layout.editText!!.text.toString()


            val validateName = validateName(name)
            val validatePhone = validatePhone(phone)
            val validateEmail = validateEmail(email)
            val validatePassword = validatePassword(password)
            val validateConfirmPassword = validateConfirmPassword(password, confirmPassword)*/


            /*if (validateName && validatePhone && validateEmail && validatePassword && validateConfirmPassword) {
                //onBackPressed()
                val intent = Intent(this, CodeFEmailActivity::class.java)
                startActivity(intent)
            }*/


            val newVerify = verifyEmailDTO()

            newVerify.email = reg_email.text.toString()

            checkExistenceEmail(newVerify)
        }
    }

    private fun checkExistenceEmail(newVerify: verifyEmailDTO){


        RetrofitClientInstance.getInstance()
            .getEmailExistence(newVerify.email)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<respDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<respDTO>) {
                    Log.i("checkEmailExistence", t.code().toString())
                    if(t.isSuccessful) {
                        Log.i("checkEmailExistence", "${t.code()}")
                        Log.i("checkEmailExistence", t.body()!!.desc)
                    }else{
                        RetrofitClientInstance.getInstance()
                            .postVerifyData(newVerify)
                            .subscribeOn(Schedulers.io())
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.subscribe(object : Observer<Response<respDTO>> {

                                override fun onComplete() {}

                                override fun onSubscribe(d: Disposable) {}

                                override fun onNext(t: Response<respDTO>) {
                                    Log.i("checkCode", "${t.code()}")

                                    if(t.isSuccessful) {
                                        val intent = Intent(applicationContext, CodeFEmailActivity::class.java)
                                        intent.putExtra("name", reg_username.text.toString())
                                        intent.putExtra("phone", reg_phone_number.text.toString())
                                        intent.putExtra("email", reg_email.text.toString())
                                        intent.putExtra("password", reg_password.text.toString())
                                        startActivity(intent)
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    Log.i("check", "that's not fineIn")
                                }

                            })
                    }
                }
                override fun onError(e: Throwable) {
                    Log.i("checkReg", "that's not fineIn")
                }
            })
    }

    private fun validateName(name: String) : Boolean {
        val nameLength = 2
        return name.matches("[a-zA-Zа-яА-ЯёЁ]*".toRegex()) && name.length >= nameLength
    }

    private fun validatePhone(phone: String): Boolean {
        val phoneLength7 = 16
        val phoneLength8 = 17
        val firstChar: Char = phone[0]
        return if (firstChar == '8') {
            phone.length == phoneLength8
        } else {
            phone.length == phoneLength7
        }
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        return password.matches("((?=.*[a-z0-9]).{6,20})".toRegex())
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return confirmPassword.matches("((?=.*[a-z0-9]).{6,20})".toRegex()) && password == confirmPassword
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Menu2Activity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

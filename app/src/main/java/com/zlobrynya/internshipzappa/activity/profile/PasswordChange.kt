package com.zlobrynya.internshipzappa.activity.profile

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.Menu2Activity
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.passwordRecoveryDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyRespDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_change_password.*
import retrofit2.Response

class PasswordChange: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        supportActionBar!!.title = "Восстановление пароля"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setBackgroundDrawable(resources.getDrawable(R.drawable.actionbar))
        supportActionBar!!.elevation = 0F

        val newPassRec = passwordRecoveryDTO()
        newPassRec.email = intent.getStringExtra("email")

        val icon = resources.getDrawable(com.zlobrynya.internshipzappa.R.drawable.error)
        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        reg_password.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                reg_password.onFocusChangeListener = object : View.OnFocusChangeListener{
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        /*
                        val password = reg_password_input_layout.editText!!.text.toString()
                        val validatePassword = validatePassword(password)

                        if (!validatePassword) {
                            reg_password_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_password)
                            reg_password.setCompoundDrawables(null, null, icon, null)
                        } else {
                            reg_password_input_layout.isErrorEnabled = false
                            reg_password.setCompoundDrawables(null, null, null, null)
                        }
                        */
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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

        })

        reg_confirm_password.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                reg_confirm_password.onFocusChangeListener = object : View.OnFocusChangeListener{
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        /*
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
                        */
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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

        })

        btn_change_pass.setOnClickListener {
            val password = reg_password_input_layout.editText!!.text.toString()
            val confirmPassword = reg_confirm_password_input_layout.editText!!.text.toString()

            val validatePassword = validatePassword(password)
            val validateConfirmPassword = validateConfirmPassword(password, confirmPassword)


            if (validatePassword && validateConfirmPassword){

                newPassRec.code = change_password_code_email_layout.editText!!.text.toString()
                newPassRec.password = password
                postNewPass(newPassRec)
            }
        }
    }

    private fun validatePassword(password: String): Boolean {
        return password.matches("((?=.*[a-z0-9]).{4,20})".toRegex())
    }

    private fun postNewPass(newPassRec: passwordRecoveryDTO){
        RetrofitClientInstance.getInstance()
            .postPassRecData(newPassRec)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<respDTO>>{
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: Response<respDTO>){
                    Log.i("checkPassRec", t.code().toString())
                    if(t.isSuccessful){
                        allert(getString(R.string.change_pass_good))
                    }
                }
                override fun onError(e: Throwable) {
                    Log.i("check", "that's not fineIn")
                }
            })
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return confirmPassword.matches("((?=.*[a-z0-9]).{4,20})".toRegex()) && password == confirmPassword
    }

    private fun allert(text: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        builder.setTitle(getString(R.string.pass_changed))
            .setMessage(text)
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.OK)
            ) { dialog, _ ->
                run {
                    Log.i("check", "it's fine")
                    val intent = Intent(this, Menu2Activity::class.java)
                    startActivity(intent)
                }
            }

        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.color_accent))
    }

}
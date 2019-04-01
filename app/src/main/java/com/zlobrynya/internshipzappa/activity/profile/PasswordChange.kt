package com.zlobrynya.internshipzappa.activity.profile

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.MenuActivity
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.passwordRecoveryDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_change_password.*
import retrofit2.Response

class PasswordChange : AppCompatActivity() {

    var canClick: Boolean = true

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

        change_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                change_password.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val password = change_password_input_layout.editText!!.text.toString()
                        val validatePassword = validatePassword(password)
                        val confirmPassword = change_confirm_password_input_layout.editText!!.text.toString()
                        val validateConfirmPassword = validateConfirmPassword(password, confirmPassword)

                        if (!hasFocus && !validatePassword) {
                            /*
                            change_password_input_layout.error =
                                    getString(com.zlobrynya.internshipzappa.R.string.error_password)
                            change_password.setCompoundDrawables(null, null, icon, null)*/
                        } else if (!hasFocus && confirmPassword.isEmpty()) {
                            change_password_input_layout.isErrorEnabled = false
                            change_password.setCompoundDrawables(null, null, null, null)
                            change_confirm_password_input_layout.isErrorEnabled = false
                            change_confirm_password.setCompoundDrawables(null, null, null, null)
                        } else if (!hasFocus && !validateConfirmPassword) {
                            change_confirm_password_input_layout.error =
                                    getString(com.zlobrynya.internshipzappa.R.string.error_confirm_password)
                            change_confirm_password.setCompoundDrawables(null, null, icon, null)

                            change_password_input_layout.error =
                                    getString(com.zlobrynya.internshipzappa.R.string.error_confirm_password)
                            change_password.setCompoundDrawables(null, null, icon, null)
                        } else {
                            change_password_input_layout.isErrorEnabled = false
                            change_password.setCompoundDrawables(null, null, null, null)
                            change_confirm_password_input_layout.isErrorEnabled = false
                            change_confirm_password.setCompoundDrawables(null, null, null, null)
                        }
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        change_confirm_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = change_password_input_layout.editText!!.text.toString()
                val confirmPassword = change_confirm_password_input_layout.editText!!.text.toString()
                val validateConfirmPassword = validateConfirmPassword(password, confirmPassword)

                if (!validateConfirmPassword) {
                    change_confirm_password_input_layout.error =
                            getString(com.zlobrynya.internshipzappa.R.string.error_confirm_password)
                    change_confirm_password.setCompoundDrawables(null, null, icon, null)

                    change_password_input_layout.error =
                            getString(com.zlobrynya.internshipzappa.R.string.error_confirm_password)
                    change_password.setCompoundDrawables(null, null, icon, null)
                } else {
                    change_password_input_layout.isErrorEnabled = false
                    change_password.setCompoundDrawables(null, null, null, null)
                    change_confirm_password_input_layout.isErrorEnabled = false
                    change_confirm_password.setCompoundDrawables(null, null, null, null)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        change_password_code_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = change_password_input_layout.editText!!.text.toString()
                val confirmPassword = change_confirm_password_input_layout.editText!!.text.toString()
                val validateConfirmPassword = validateConfirmPassword(password, confirmPassword)

                if (change_password_code_email.text.toString().length < 5) {
                    change_password_code_email_layout.error =
                        getString(com.zlobrynya.internshipzappa.R.string.code_null)
                    change_password_code_email.setCompoundDrawables(null, null, icon, null)
                    btn_change_pass.background = resources.getDrawable(R.drawable.btn_not_click)
                } else {
                    change_password_code_email_layout.isErrorEnabled = false
                    change_password_code_email.setCompoundDrawables(null, null, null, null)
                    if (validateConfirmPassword(password, confirmPassword)) {
                        btn_change_pass.background = resources.getDrawable(R.drawable.btn_can_click)
                        btn_change_pass.isEnabled = true
                    }
                }
            }

        })

        btn_change_pass.setOnClickListener {
            val password = change_password_input_layout.editText!!.text.toString()
            val confirmPassword = change_confirm_password_input_layout.editText!!.text.toString()

            val validatePassword = validatePassword(password)
            val validateConfirmPassword = validateConfirmPassword(password, confirmPassword)


            if (validatePassword && validateConfirmPassword) {

                newPassRec.code = change_password_code_email_layout.editText!!.text.toString()
                newPassRec.password = password
                if (canClick) {
                    canClick = false
                    postNewPass(newPassRec)
                }
            }
        }
    }

    private fun validatePassword(password: String): Boolean {
        return password.matches("((?=.*[a-z0-9]).{4,20})".toRegex())
    }

    private fun postNewPass(newPassRec: passwordRecoveryDTO) {
        RetrofitClientInstance.getInstance()
            .postPassRecData(newPassRec)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<respDTO>> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: Response<respDTO>) {
                    if (t.isSuccessful) {
                        allert(getString(R.string.change_pass_good))
                    }
                }

                override fun onError(e: Throwable) {

                }
            })
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return confirmPassword.matches("((?=.*[a-zA-Z0-9]).{4,20})".toRegex()) && password == confirmPassword
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
                    val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)
                }
            }

        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.color_accent))
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
        super.onStart()
        canClick = true
    }

}
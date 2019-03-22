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
import kotlinx.android.synthetic.main.activity_change_password.*

class PasswordChange: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        supportActionBar!!.title = "Восстановление пароля"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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
                allert(getString(R.string.change_pass_good))
            }
        }
    }

    private fun validatePassword(password: String): Boolean {
        return password.matches("((?=.*[a-z0-9]).{6,20})".toRegex())
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return confirmPassword.matches("((?=.*[a-z0-9]).{6,20})".toRegex()) && password == confirmPassword
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
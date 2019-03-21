package com.zlobrynya.internshipzappa.activity.profile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.zlobrynya.internshipzappa.activity.Menu2Activity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.zlobrynya.internshipzappa.R.layout.activity_login)
        supportActionBar!!.title = "Вход"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        btnLinkToRegisterActivity.setOnClickListener {
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
        }

        val icon = resources.getDrawable(com.zlobrynya.internshipzappa.R.drawable.error)

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        log_email.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                log_email.onFocusChangeListener = object : View.OnFocusChangeListener{
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val email = log_email_input_layout.editText!!.text.toString()
                        val validateEmail = validateEmail(email)

                        if (!validateEmail) {
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

        log_password.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                log_password.onFocusChangeListener = object : View.OnFocusChangeListener{
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val password = log_password_input_layout.editText!!.text.toString()
                        val validatePassword = validatePassword(password)

                        if (!validatePassword) {
                            log_password_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_password)
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

        btnLogin.setOnClickListener {
            val email = log_email_input_layout.editText!!.text.toString()
            val password = log_password_input_layout.editText!!.text.toString()


            val validateEmail = validateEmail(email)
            val validatePassword = validatePassword(password)

            if (validateEmail && validatePassword) {
                onBackPressed()
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        return password.matches("((?=.*[a-z0-9]).{6,20})".toRegex())
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

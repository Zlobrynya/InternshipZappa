package com.zlobrynya.internshipzappa.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_personal_info.*
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager
import java.util.regex.Matcher
import java.util.regex.Pattern
import android.widget.Toast
import com.zlobrynya.internshipzappa.R
import io.fabric.sdk.android.services.common.CommonUtils.hideKeyboard

class PersonalInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_info)
        supportActionBar!!.title = "Бронирование"

        //val bundle = intent.extras

        //selected_table.text = bundle.getString("table")
        //selected_date.text = bundle.getString("date")
        //selected_time.text = bundle.getString("time")

        btnContinue.setOnClickListener {
            hideKeyboard()

            val icon = resources.getDrawable(R.drawable.error)

            icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

            val name = username_input_layout.editText!!.text.toString()
            val phone = phone_number_input_layout.editText!!.text.toString()
            val email = register_email_input_layout.editText!!.text.toString()

            val validateName = validateName(name)
            val validatePhone = validatePhone(phone)
            val validateEmail = validateEmail(email)

            if (!validateName) {
                username_input_layout.error = getString(R.string.error_name)
                username.setCompoundDrawables(null, null, icon, null)
            } else {
                username_input_layout.isErrorEnabled = false
                username.setCompoundDrawables(null, null, null, null)
            }
            if (!validatePhone) {
                phone_number_input_layout.error = getString(R.string.error_phone)
                phone_number.setCompoundDrawables(null, null, icon, null)
            } else {
                phone_number_input_layout.isErrorEnabled = false
                phone_number.setCompoundDrawables(null, null, null, null)
            }
            if (!validateEmail) {
                register_email_input_layout.error = getString(R.string.error_email)
                register_email.setCompoundDrawables(null, null, icon, null)
            } else {
                register_email_input_layout.isErrorEnabled = false
                register_email.setCompoundDrawables(null, null, null, null)
            }
            if (validateName && validateEmail && validatePhone) {
                doLogin()
            }
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private fun validateName(name: String) : Boolean {
        val nameLength = 3
        return name.length >= nameLength
    }

    private fun validatePhone(phone: String): Boolean {
        val phoneLength = 18
        return android.util.Patterns.PHONE.matcher(phone).matches() && phone.length == phoneLength
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun doLogin() {
        Toast.makeText(applicationContext, "OK! I'm performing login.", Toast.LENGTH_SHORT).show()
        //val intent = Intent(context, ConfirmPersonalInfoActivity::class.java)
        //val username = username_input_layout.editText!!.text.toString()
        //intent.putExtra("username", username)
        //context.startActivity(intent)
    }
}

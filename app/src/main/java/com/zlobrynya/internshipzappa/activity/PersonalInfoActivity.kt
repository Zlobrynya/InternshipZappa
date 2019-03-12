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

            val phone = phone_number_input_layout.editText!!.text.toString()
            val email = register_email_input_layout.editText!!.text.toString()
            if (!validatePhone(phone)) {
                phone_number_input_layout.error = getString(R.string.error_phone)
                phone_number.setCompoundDrawables(null, null, icon, null)
            } else if (!validateEmail(email)) {
                register_email_input_layout.error = getString(R.string.error_email)
                register_email.setCompoundDrawables(null, null, icon, null)
            } else {
                phone_number_input_layout.isErrorEnabled = false
                register_email_input_layout.isErrorEnabled = false
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

    /*private fun validatePhone(phone: String): Boolean {
        boolean check = false
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            if(phone.length() < 6 || phone.length() > 13) {
                check = false
            } else {
                check = true
            }
        } else {
            check = false
        }
        return check
    }

    private val EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$"
    private val pattern = Pattern.compile(EMAIL_PATTERN)
    private var matcher: Matcher? = null

    fun validateEmail(email: String): Boolean {
        matcher = pattern.matcher(email)
        return matcher!!.matches()
    }*/

    private fun validatePhone(phone: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches()
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

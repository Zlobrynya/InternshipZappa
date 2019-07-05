package com.zlobrynya.narogah.activity.profile

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.zlobrynya.narogah.R
import com.zlobrynya.narogah.tools.retrofit.DTOs.accountDTOs.verifyEmailDTO
import com.zlobrynya.narogah.tools.retrofit.DTOs.accountDTOs.verifyRespDTO
import com.zlobrynya.narogah.tools.retrofit.DTOs.respDTO
import com.zlobrynya.narogah.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    var canClickRegisterButton: Boolean = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        reg_phone_number.setOnEditorActionListener(object:TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                return false
            }
        })

        supportActionBar!!.title = "Регистрация"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setBackgroundDrawable(resources.getDrawable(R.drawable.actionbar))
        supportActionBar!!.elevation = 0F

        val icon = resources.getDrawable(com.zlobrynya.narogah.R.drawable.error)

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        registerConstraint.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(hasFocus){
                    hideKeyboard(v!!)
                }
            }
        }

        /*reg_username.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(!hasFocus){
                    hideKeyboard(v!!)
                }
            }
        }*/

        reg_username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                reg_username.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val name = reg_username_input_layout.editText!!.text.toString()
                        val validateName = validateName(name)

                        if (!hasFocus && !validateName) {
                            reg_username_input_layout.error =
                                getString(com.zlobrynya.narogah.R.string.error_name)
                            reg_username.setCompoundDrawables(null, null, icon, null)
                        } else {
                            reg_username_input_layout.isErrorEnabled = false
                            reg_username.setCompoundDrawables(null, null, null, null)
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        /*reg_phone_number.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(!hasFocus){
                    hideKeyboard(v!!)
                }
            }
        }*/

        reg_phone_number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                reg_phone_number.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val phone = reg_phone_number_input_layout.editText!!.text.toString()
                        val validatePhone = validatePhone(phone)

                        if (!hasFocus && !validatePhone) {
                            reg_phone_number_input_layout.error =
                                getString(com.zlobrynya.narogah.R.string.error_phone)
                            reg_phone_number.setCompoundDrawables(null, null, icon, null)
                        } else {
                            reg_phone_number_input_layout.isErrorEnabled = false
                            reg_phone_number.setCompoundDrawables(null, null, null, null)
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        /*reg_email.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(!hasFocus){
                    hideKeyboard(v!!)
                }
            }
        }*/

        reg_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                reg_email.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val email = reg_email_input_layout.editText!!.text.toString()
                        val validateEmail = validateEmail(email)

                        if (!hasFocus && !validateEmail) {
                            reg_email_input_layout.error = getString(com.zlobrynya.narogah.R.string.error_email)
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

        /*reg_password.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(!hasFocus){
                    hideKeyboard(v!!)
                }
            }
        }*/

        reg_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = reg_password_input_layout.editText!!.text.toString()
                val validatePassword = validatePassword(password)
                val confirmPassword = reg_confirm_password_input_layout.editText!!.text.toString()
                val validateConfirmPassword = validateConfirmPassword(password, confirmPassword)

                if (reg_password.length()<8){
                    reg_password_input_layout.isHelperTextEnabled = false
                    reg_password_input_layout.error =
                            getString(com.zlobrynya.narogah.R.string.error_password)
                    reg_password_input_layout.isErrorEnabled = true
                    reg_password.setCompoundDrawables(null, null, icon, null)
                } else if (!validatePassword) {
                    reg_password_input_layout.isHelperTextEnabled = false
                    reg_password_input_layout.error =
                            getString(com.zlobrynya.narogah.R.string.error_password)
                    reg_password_input_layout.isErrorEnabled = true
                } else if (!validateConfirmPassword && confirmPassword.isEmpty()) {
                    reg_password_input_layout.isErrorEnabled = false
                    reg_password.setCompoundDrawables(null, null, null, null)
                    reg_confirm_password_input_layout.isErrorEnabled = false
                    reg_confirm_password.setCompoundDrawables(null, null, null, null)
                } else if (!validateConfirmPassword) {
                    reg_confirm_password_input_layout.error =
                            getString(com.zlobrynya.narogah.R.string.error_confirm_password)
                    reg_confirm_password.setCompoundDrawables(null, null, icon, null)

                    reg_password_input_layout.error =
                            getString(com.zlobrynya.narogah.R.string.error_confirm_password)
                    reg_password.setCompoundDrawables(null, null, icon, null)
                } else {
                    reg_password_input_layout.isHelperTextEnabled = true
                    reg_confirm_password_input_layout.isHelperTextEnabled = true
                    reg_password_input_layout.isErrorEnabled = false
                    reg_password.setCompoundDrawables(null, null, null, null)
                    reg_confirm_password_input_layout.isErrorEnabled = false
                    reg_confirm_password.setCompoundDrawables(null, null, null, null)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        /*reg_confirm_password.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(!hasFocus){
                    hideKeyboard(v!!)
                }
            }
        }*/

        reg_confirm_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = reg_password_input_layout.editText!!.text.toString()
                val confirmPassword = reg_confirm_password_input_layout.editText!!.text.toString()
                val validateConfirmPassword = validateConfirmPassword(password, confirmPassword)

                if (reg_confirm_password.length()<8){
                    reg_confirm_password_input_layout.isHelperTextEnabled = false
                    reg_confirm_password_input_layout.error =
                            getString(com.zlobrynya.narogah.R.string.error_password)
                    reg_confirm_password_input_layout.isErrorEnabled = true
                    reg_confirm_password.setCompoundDrawables(null, null, icon, null)
                } else if (!validatePassword(reg_confirm_password.text.toString())){
                    reg_confirm_password_input_layout.isHelperTextEnabled = false
                    reg_confirm_password_input_layout.error =
                            getString(com.zlobrynya.narogah.R.string.error_password)
                    reg_confirm_password_input_layout.isErrorEnabled = true
                    reg_confirm_password.setCompoundDrawables(null, null, icon, null)
                } else if (!validateConfirmPassword) {
                    reg_confirm_password_input_layout.error =
                        getString(com.zlobrynya.narogah.R.string.error_confirm_password)
                    reg_confirm_password.setCompoundDrawables(null, null, icon, null)

                    reg_password_input_layout.error =
                        getString(com.zlobrynya.narogah.R.string.error_confirm_password)
                    reg_password.setCompoundDrawables(null, null, icon, null)
                } else {
                    reg_password_input_layout.isHelperTextEnabled = true
                    reg_confirm_password_input_layout.isHelperTextEnabled = true
                    reg_password_input_layout.isErrorEnabled = false
                    reg_password.setCompoundDrawables(null, null, null, null)
                    reg_confirm_password_input_layout.isErrorEnabled = false
                    reg_confirm_password.setCompoundDrawables(null, null, null, null)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        btnRegister.setOnClickListener {
            val name = reg_username_input_layout.editText!!.text.toString()
            val phone = reg_phone_number_input_layout.editText!!.text.toString()
            val email = reg_email_input_layout.editText!!.text.toString()
            val password = reg_password_input_layout.editText!!.text.toString()
            val confirmPassword = reg_confirm_password_input_layout.editText!!.text.toString()


            val validateName = validateName(name)
            val validatePhone = validatePhone(phone)
            val validateEmail = validateEmail(email)
            val validatePassword = validatePassword(password)
            val validateConfirmPassword = validateConfirmPassword(password, confirmPassword)


            if (validateName && validatePhone && validateEmail && validatePassword && validateConfirmPassword) {
                val newVerify = verifyEmailDTO()

                newVerify.email = reg_email.text.toString()
                if (canClickRegisterButton) {
                    canClickRegisterButton = false
                    checkExistenceEmail(newVerify)
                }
            } else {
                Toast.makeText(this@RegisterActivity, "Заполните данные", Toast.LENGTH_SHORT)
                    .show()
                canClickRegisterButton = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        canClickRegisterButton = true
        alertIsShown = false
    }

    private fun checkExistenceEmail(newVerify: verifyEmailDTO) {
        progress_spinner.visibility = View.VISIBLE

        RetrofitClientInstance.getInstance()
            .getEmailExistence(newVerify.email)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<respDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<respDTO>) {
                    if (t.isSuccessful) {
                        val icon = resources.getDrawable(com.zlobrynya.narogah.R.drawable.error)
                        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
                        reg_email_input_layout.error = getString(com.zlobrynya.narogah.R.string.exist_email)
                        reg_email.setCompoundDrawables(null, null, icon, null)
                        progress_spinner.visibility = View.GONE
                        canClickRegisterButton = true
                    } else {
                        RetrofitClientInstance.getInstance()
                            .postVerifyData(newVerify)
                            .subscribeOn(Schedulers.io())
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.subscribe(object : Observer<Response<verifyRespDTO>> {

                                override fun onComplete() {}

                                override fun onSubscribe(d: Disposable) {}

                                override fun onNext(t: Response<verifyRespDTO>) {
                                    if (registerActivityIsRunning) {
                                        if (t.isSuccessful) {
                                            val intent = Intent(applicationContext, CodeFEmailActivity::class.java)
                                            intent.putExtra("name", reg_username.text.toString())
                                            intent.putExtra("phone", reg_phone_number.text.toString())
                                            intent.putExtra("email", reg_email.text.toString())
                                            intent.putExtra("password", reg_password.text.toString())
                                            intent.putExtra("code", t.body()!!.email_code)
                                            intent.putExtra("id", "0")
                                            startActivity(intent)
                                        } else canClickRegisterButton = true
                                    }
                                    progress_spinner.visibility = View.GONE
                                }

                                override fun onError(e: Throwable) {
                                    progress_spinner.visibility = View.GONE
                                    showNoInternetConnectionAlert(newVerify)
                                }

                            })
                    }
                }

                override fun onError(e: Throwable) {
                    progress_spinner.visibility = View.GONE
                    showNoInternetConnectionAlert(newVerify)
                }
            })
    }

    /*fun Editable.limitLength(maxLength: Int) {
        filters = arrayOf(InputFilter.LengthFilter(maxLength))
    }*/

    private fun validateName(name: String): Boolean {
        val nameLength = 2
        return name.matches("[a-zA-Zа-яА-ЯёЁ]*".toRegex()) && name.length >= nameLength
    }

    private fun validatePhone(phone: String): Boolean {
        //val phoneLength7 = 16
        //val phoneLength8 = 17
        //val firstChar: Char = phone[0]
        /*return if (firstChar == '8') {
            phone.length == phoneLength8
        } else {
            phone.length == phoneLength7
        }*/
        val phoneLength = 16
        return phone.length == phoneLength
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        return password.matches("^(?=.*\\d)(?=.*[a-zA-Z])[0-9a-zA-Z!@#\$%^&*()\\-_=+{}|?>.<,:;~`’]{8,32}$".toRegex())
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onStart() {
        super.onStart()
        registerActivityIsRunning = true
    }

    override fun onStop() {
        super.onStop()
        registerActivityIsRunning = false
    }

    companion object {
        var registerActivityIsRunning: Boolean = false
    }

    private var alertIsShown = false
    /**
     * Выводит диалоговое окно с сообщением об отсутствии интернета
     */
    private fun showNoInternetConnectionAlert(newVerify: verifyEmailDTO) {
        if (!alertIsShown) {
            alertIsShown = true
            val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle("Ошибка соединения")
                .setMessage("Без подключения к сети невозможно продолжить бронирование.\nПроверьте соединение и попробуйте снова")
                .setCancelable(false)
                .setPositiveButton("ПОВТОРИТЬ") { dialog, which ->
                    run {
                        dialog.dismiss()
                        checkExistenceEmail(newVerify)
                        alertIsShown = false
                    }
                }
                .setNegativeButton("ОТМЕНА") { dialog, which ->
                    run {
                        dialog.dismiss()
                        alertIsShown = false
                        canClickRegisterButton = true
                    }
                }
            val alert = builder.create()
            alert.show()
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.color_accent))
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.color_accent))
        }
    }

}

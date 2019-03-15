package com.zlobrynya.internshipzappa.activity.booking

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_personal_info.*
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.bookingUserDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response





class PersonalInfoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_info)
        supportActionBar!!.title = "Бронирование"

        val bookDateBegin = intent.getStringExtra("book_date_begin")
        val bookTimeBegin = intent.getStringExtra("book_time_begin")
        val bookTimeEnd = intent.getStringExtra("book_time_end")
        val bookDateEnd = intent.getStringExtra("book_date_end")
        val bookTableId = intent.getIntExtra("table_id", 1).toString().toInt()
        val newBooking = bookingUserDTO()

        newBooking.date = bookDateBegin
        newBooking.time_from = bookTimeBegin
        newBooking.time_to = bookTimeEnd
        newBooking.table_id = bookTableId
        newBooking.date_to = bookDateEnd
       /* val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_shared_users), Context.MODE_PRIVATE)
        val savedName = this.getString(R.string.key_user_name)
        val savedPhone = this.getString(R.string.key_user_phone)
        val savedEmail = this.getString(R.string.key_user_email)
        if(sharedPreferences.getString(savedName, "") != "") username_input_layout.editText!!.text = sharedPreferences.getString(savedName, "")
        if(sharedPreferences.getString(savedPhone, "") != "") phone_number_input_layout.editText!!.setText(sharedPreferences.getString(savedPhone, ""))
        if(sharedPreferences.getString(savedEmail, "") != "") register_email_input_layout.editText!!.setText(sharedPreferences.getString(savedEmail, ""))*/

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
                newBooking.name = name
                newBooking.email = email
                newBooking.phone = phone
                networkRxjavaPost(newBooking, it.context)
            }
        }
    }

    private fun hideKeyboard() {
        val view = getCurrentFocus()
        if (view != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                view!!.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    //Пост запрос на размещение личной информации(RxJava2)
    private fun networkRxjavaPost(newBooking: bookingUserDTO, context: Context){
        RetrofitClientInstance.getInstance()
            .postReserve(newBooking)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<respDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<respDTO>) {
                    Log.d("onNextPA", "зашёл")
                    if (t.isSuccessful) {
                        Log.i("check1", "${t.code()}")
                        if (t.body() != null) {
                            /*if(code == 200){
                                val sharedPreferences = context.getSharedPreferences(context.getString(R.string.key_shared_users), Context.MODE_PRIVATE)
                                val savedName = context.getString(R.string.key_user_name)
                                val savedPhone = context.getString(R.string.key_user_phone)
                                val savedEmail = context.getString(R.string.key_user_email)
                                val editor = sharedPreferences.edit()
                                editor.putString(savedName,newBooking.name)
                                editor.putString(savedPhone,newBooking.phone)
                                editor.putString(savedEmail,newBooking.email)
                                editor.apply()
                            }*/
                            val intent = Intent(context, BookingEnd::class.java)
                            intent.putExtra("code", t.code())
                            intent.putExtra("name", newBooking.name)
                            intent.putExtra("phone", newBooking.phone)
                            context.startActivity(intent)
                        }
                    } else {
                        Log.i("check2", "${t.code()}")
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i("check","that's not fineIn")
                }

            })
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
}


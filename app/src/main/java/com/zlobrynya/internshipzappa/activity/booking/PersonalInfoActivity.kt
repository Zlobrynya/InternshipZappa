package com.zlobrynya.internshipzappa.activity.booking

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_personal_info.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat.getSystemService
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.bookingUserDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.tableList
import com.zlobrynya.internshipzappa.tools.retrofit.PostRequest
import io.fabric.sdk.android.services.common.CommonUtils.hideKeyboard
import kotlinx.android.synthetic.main.activity_end_booking.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*





class PersonalInfoActivity : AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.zlobrynya.internshipzappa.R.layout.activity_personal_info)
        supportActionBar!!.title = "Бронирование"
        val bookDateBegin = intent.getStringExtra("book_date_begin")
        val bookTimeBegin = intent.getStringExtra("book_time_begin")
        val bookTimeEnd = intent.getStringExtra("book_time_end")
        val bookDateEnd = intent.getStringExtra("book_date_end")
        val bookTableId = intent.getIntExtra("table_id", 1).toString().toInt()
        val seatCount = intent.getIntExtra("seat_count", 1).toString()
        val seatPosition = intent.getStringExtra("seat_position")
        val seatType = intent.getStringExtra("seat_type")

        val newBooking = bookingUserDTO()
        newBooking.date = bookDateBegin
        newBooking.time_from = bookTimeBegin
        newBooking.time_to = bookTimeEnd
        newBooking.table_id = bookTableId
        newBooking.date_to = bookDateEnd
        val sharedPreferences = this.getSharedPreferences(this.getString(R.string.key_shared_users), Context.MODE_PRIVATE)
        val savedName = this.getString(R.string.key_user_name)
        val savedPhone = this.getString(R.string.key_user_phone)
        val savedEmail = this.getString(R.string.key_user_email)
        if(sharedPreferences.getString(savedName, "") != "") username_input_layout.editText!!.setText(sharedPreferences.getString(savedName, ""))
        if(sharedPreferences.getString(savedPhone, "") != "") {
            phone_number.setText("")
            val change_phone = replaceStartPhone(sharedPreferences.getString(savedPhone, ""))
            phone_number.setMaskedText(change_phone)
        }
        if(sharedPreferences.getString(savedEmail, "") != "") register_email_input_layout.editText!!.setText(sharedPreferences.getString(savedEmail, ""))

        if(seatPosition == "") {
            val textTable = getString(com.zlobrynya.internshipzappa.R.string.table2, seatCount, seatType)
            selected_table.text = textTable
        } else {
            val textTable = getString(com.zlobrynya.internshipzappa.R.string.table3, seatCount, seatPosition, seatType)
            selected_table.text = textTable
        }

        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy")
        val date: Date = inputFormat.parse(bookDateBegin)
        val outputDateStr = outputFormat.format(date)
        selected_date.text = outputDateStr

        val textWoSecI = bookTimeBegin.toString()
        val textWoSecO = bookTimeEnd.toString()
        val woSecI = deleteSecInTime(textWoSecI)
        val woSecO = deleteSecInTime(textWoSecO)
        var text = getString(com.zlobrynya.internshipzappa.R.string.period, woSecI, woSecO)
        selected_time.text = text

        val icon = resources.getDrawable(com.zlobrynya.internshipzappa.R.drawable.error)

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        username.onFocusChangeListener = object: View.OnFocusChangeListener{
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                val name = username_input_layout.editText!!.text.toString()
                val validateName = validateName(name)

                if (!validateName) {
                    username_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_name)
                    username.setCompoundDrawables(null, null, icon, null)
                } else {
                    username_input_layout.isErrorEnabled = false
                    username.setCompoundDrawables(null, null, null, null)
                }
            }

        }

        phone_number.onFocusChangeListener = object: View.OnFocusChangeListener{
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                val phone = phone_number_input_layout.editText!!.text.toString()
                val validatePhone = validatePhone(phone)

                if (!validatePhone) {
                    phone_number_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_phone)
                    phone_number.setCompoundDrawables(null, null, icon, null)
                } else {
                    phone_number_input_layout.isErrorEnabled = false
                    phone_number.setCompoundDrawables(null, null, null, null)
                }
            }

        }


        register_email.onFocusChangeListener = object : View.OnFocusChangeListener{
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                val email = register_email_input_layout.editText!!.text.toString()
                val validateEmail = validateEmail(email)

                if (!validateEmail) {
                    register_email_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_email)
                    register_email.setCompoundDrawables(null, null, icon, null)
                } else {
                    register_email_input_layout.isErrorEnabled = false
                    register_email.setCompoundDrawables(null, null, null, null)
                }
            }

        }

        btnContinue.setOnClickListener {
            hideKeyboard()

            val name = username_input_layout.editText!!.text.toString()
            val phone = phone_number_input_layout.editText!!.text.toString()
            val email = register_email_input_layout.editText!!.text.toString()

            val validateName = validateName(name)
            val validatePhone = validatePhone(phone)
            val validateEmail = validateEmail(email)

            if (validateName && validateEmail && validatePhone) {
                //btnContinue.setBackgroundColor(resources.getColor(R.color.btn_continue))
                newBooking.name = name
                newBooking.email = email
                newBooking.phone = phone
                networkRxjavaPost(newBooking, it.context)
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
                            val code = t.code()
                            if(code == 200){
                                val sharedPreferences = context.getSharedPreferences(context.getString(R.string.key_shared_users), Context.MODE_PRIVATE)
                                val savedName = context.getString(R.string.key_user_name)
                                val savedPhone = context.getString(R.string.key_user_phone)
                                val savedEmail = context.getString(R.string.key_user_email)
                                val editor = sharedPreferences.edit()
                                editor.putString(savedName,newBooking.name)
                                editor.putString(savedPhone,newBooking.phone)
                                editor.putString(savedEmail,newBooking.email)
                                editor.apply()
                            }
                            val intent = Intent(context, BookingEnd::class.java)
                            intent.putExtra("code", t.code())
                            intent.putExtra("name", newBooking.name)
                            intent.putExtra("phone", newBooking.phone)
                            finish()
                            context.startActivity(intent)
                        }
                    } else {
                        Log.i("check2", "${t.code()}")
                        val intent = Intent(context, BookingEnd::class.java)
                        intent.putExtra("code", t.code())
                        intent.putExtra("name", newBooking.name)
                        intent.putExtra("phone", newBooking.phone)
                        finish()
                        context.startActivity(intent)
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i("check","that's not fineIn")
                }

            })
    }

    private fun validateName(name: String) : Boolean {
        val nameLength = 2
        return name.matches("[A-ZА-Я][a-zA-Zа-яА-Я]*".toRegex()) && name.length >= nameLength
    }

    private fun validatePhone(phone: String): Boolean {
        val phoneLength = 18
        return android.util.Patterns.PHONE.matcher(phone).matches() && phone.length == phoneLength
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun deleteSecInTime(str: String): String {
        return str.substring(0, str.length - 3)
    }

    private fun replaceStartPhone(str: String?): String {
        val newstr = str!!.replace(" ", "")
        val newstra = newstr.replace("(", "")
        val newstrb = newstra.replace(")", "")
        val newstrc = newstrb.replace("-", "")
        return newstrc.replace("+7", "")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}


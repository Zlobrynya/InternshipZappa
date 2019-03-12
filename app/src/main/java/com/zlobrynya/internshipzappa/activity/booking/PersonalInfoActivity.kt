package com.zlobrynya.internshipzappa.activity.booking

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_personal_info.*
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.bookingUserDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.tableList
import com.zlobrynya.internshipzappa.tools.retrofit.PostRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PersonalInfoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_info)
        supportActionBar!!.title = "Бронирование"
        val bookDateBegin = intent.getStringExtra("book_date_begin")
        val bookTimeBegin = intent.getStringExtra("book_time_begin")
        val bookTimeEnd = intent.getStringExtra("book_time_end")
        val bookTableId = intent.getIntExtra("table_id", 1).toString().toInt()
        val newBooking = bookingUserDTO()
        newBooking.date = bookDateBegin
        newBooking.time_from = bookTimeBegin
        newBooking.time_to = bookTimeEnd
        newBooking.table_id = bookTableId
        btnContinue.setOnClickListener {
            hideKeyboard()

            val icon = resources.getDrawable(R.drawable.error)

            icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
            val name = username_input_layout.editText!!.text.toString()
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
                newBooking.name = name
                newBooking.email = email
                newBooking.phone = phone
                networkPost(newBooking, it.context)
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
    private fun networkPost(newBooking: bookingUserDTO, context: Context){
        var code1: Int = 0
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://na-rogah-api.herokuapp.com/api/v1/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()
        val apiInterface: PostRequest = retrofit.create(PostRequest::class.java)
        val requestCall = apiInterface.postReserve(newBooking)
        requestCall.enqueue(object : Callback<respDTO> {
            override fun onFailure(call: Call<respDTO>, t: Throwable) {}

            override fun onResponse(call: Call<respDTO>, response: Response<respDTO>) {
                if (response.isSuccessful) {
                    Log.i("check1", "${response.code()}")
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val code = responseBody.code
                        val intent = Intent(context, BookingEnd::class.java)
                        intent.putExtra("code", code)
                        intent.putExtra("name", newBooking.name)
                        intent.putExtra("phone", newBooking.phone)
                        context.startActivity(intent)
                    }
                } else {
                    Log.i("check2", "${response.code()}")
                }
            }
        })
        Log.i("check2", code1.toString())
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
}

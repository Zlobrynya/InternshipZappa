package com.zlobrynya.internshipzappa.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.booking.BookingEnd
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.bookingUserDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_personal_info.view.*
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * Фрагмент персональное инфо
 * Калька с одноименной активити
 * TODO доделать
 */
class PersonalInfoFragment : Fragment() {

    private val blockCharacterSet: String = ".,- "

    private val filter = object : InputFilter {
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            if (source != null && blockCharacterSet.contains(("" + source))) {
                return ""
            }
            return null
        }
    }

    /**
     * Обработчик нажатий на стрелочку в тулбаре
     */
    private val navigationClickListener = View.OnClickListener {
        closeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_personal_info, container, false)
        view = initToolBar(view)

        val bookDateBegin = arguments!!.getString("book_date_begin")
        val bookTimeBegin = arguments!!.getString("book_time_begin")
        val bookTimeEnd = arguments!!.getString("book_time_end")
        val bookDateEnd = arguments!!.getString("book_date_end")
        val bookTableId = arguments!!.getInt("table_id", 1).toString().toInt()
        val seatCount = arguments!!.getInt("seat_count", 1).toString()
        val seatPosition = arguments!!.getString("seat_position")
        val seatType = arguments!!.getString("seat_type")

        val newBooking = bookingUserDTO()
        newBooking.date = bookDateBegin
        newBooking.time_from = bookTimeBegin
        newBooking.time_to = bookTimeEnd
        newBooking.table_id = bookTableId
        newBooking.date_to = bookDateEnd

        // TODO Работа с шаред преференс должна будет быть переделана
        /*val sharedPreferences =
            activity!!.getSharedPreferences(this.getString(R.string.key_shared_users), Context.MODE_PRIVATE)
        val savedName = this.getString(R.string.key_user_name)
        val savedPhone = this.getString(R.string.key_user_phone)
        val savedEmail = this.getString(R.string.key_user_email)
        if (sharedPreferences.getString(
                savedName,
                ""
            ) != ""
        ) username_input_layout.editText!!.setText(sharedPreferences.getString(savedName, ""))
        if (sharedPreferences.getString(savedPhone, "") != "") {
            val change_phone = sharedPreferences.getString(savedPhone, "")
            phone_number_input_layout.editText!!.setText(change_phone)
        }
        if (sharedPreferences.getString(savedEmail, "") != "") register_email_input_layout.editText!!.setText(
            sharedPreferences.getString(savedEmail, "")
        )*/

        if (seatPosition == "" && seatCount == "4") {
            val textTable = getString(com.zlobrynya.internshipzappa.R.string.table4, seatCount, seatType)
            view.selected_table.setText(textTable)
        } else if (seatPosition == "" && seatCount != "4") {
            val textTable = getString(com.zlobrynya.internshipzappa.R.string.table68, seatCount, seatType)
            view.selected_table.setText(textTable)
        } else if (seatPosition != "" && seatCount == "4") {
            val textTable = getString(com.zlobrynya.internshipzappa.R.string.table43, seatCount, seatPosition, seatType)
            view.selected_table.setText(textTable)
        } else {
            val textTable =
                getString(com.zlobrynya.internshipzappa.R.string.table683, seatCount, seatPosition, seatType)
            view.selected_table.setText(textTable)
        }

        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy")
        val date: Date = inputFormat.parse(bookDateBegin)
        val outputDateStr = outputFormat.format(date)
        view.selected_date.text = outputDateStr

        val textWoSecI = bookTimeBegin.toString()
        val textWoSecO = bookTimeEnd.toString()
        val woSecI = deleteSecInTime(textWoSecI)
        val woSecO = deleteSecInTime(textWoSecO)
        var text = getString(com.zlobrynya.internshipzappa.R.string.period, woSecI, woSecO)
        view.selected_time.text = text

        val icon = resources.getDrawable(com.zlobrynya.internshipzappa.R.drawable.error)

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        view.btnContinue.setOnClickListener {
            hideKeyboard()

            // TODO эти данные больше не нужно получать из полей в XML, а получать из ШередПреференс
            val name = view.username_input_layout.text.toString()
            val phone = view.phone_number_input_layout.text.toString()
            val email = view.register_email_input_layout.text.toString()

            // TODO валидация теперь наверно не нужна
            /*val validateName = validateName(name)
            val validatePhone = validatePhone(phone)
            val validateEmail = validateEmail(email)

            if (validateName && validateEmail && validatePhone) {
                //btnContinue.setBackgroundColor(resources.getColor(R.color.btn_continue))
                newBooking.name = name
                newBooking.email = email
                newBooking.phone = phone
                networkRxjavaPost(newBooking, it.context)
            } else {
                Toast.makeText(activity, "Введите корректные данные", Toast.LENGTH_SHORT).show()
            }*/

            newBooking.name = name
            newBooking.email = email
            newBooking.phone = phone
            networkRxjavaPost(newBooking, it.context)
        }

        return view
    }

    private fun hideKeyboard() {
        val view = activity!!.currentFocus
        if (view != null) {
            (activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    //Пост запрос на размещение личной информации(RxJava2)
    private fun networkRxjavaPost(newBooking: bookingUserDTO, context: Context) {
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
                            if (code == 200) {
                                val sharedPreferences = context.getSharedPreferences(
                                    context.getString(R.string.key_shared_users),
                                    Context.MODE_PRIVATE
                                )
                                val savedName = context.getString(R.string.key_user_name)
                                val savedPhone = context.getString(R.string.key_user_phone)
                                val savedEmail = context.getString(R.string.key_user_email)
                                val editor = sharedPreferences.edit()
                                editor.putString(savedName, newBooking.name)
                                editor.putString(savedPhone, newBooking.phone)
                                editor.putString(savedEmail, newBooking.email)
                                editor.apply()
                            }
                            val intent = Intent(context, BookingEnd::class.java)
                            intent.putExtra("code", t.code())
                            intent.putExtra("name", newBooking.name)
                            intent.putExtra("phone", newBooking.phone)
                            context.startActivity(intent)
                        }
                    } else {
                        Log.i("check2", "${t.code()}")
                        val intent = Intent(context, BookingEnd::class.java)
                        intent.putExtra("code", t.code())
                        intent.putExtra("name", newBooking.name)
                        intent.putExtra("phone", newBooking.phone)
                        //finish()
                        closeFragment() // Закроем фрагмент
                        context.startActivity(intent)
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i("check", "that's not fineIn")
                }

            })
    }

    private fun validateName(name: String): Boolean {
        val nameLength = 2
        return name.matches("[a-zA-Zа-яА-ЯёЁ]*".toRegex()) && name.length >= nameLength
    }

    private fun validatePhone(phone: String): Boolean {
        val phoneLength7 = 16
        val phoneLength8 = 17
        val firstChar: Char = phone[0]
        return if (firstChar == '8') {
            phone.length == phoneLength8
        } else {
            phone.length == phoneLength7
        }
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun deleteSecInTime(str: String): String {
        return str.substring(0, str.length - 3)
    }

    /**
     * Настраивает тулбар
     */
    private fun initToolBar(view: View): View {
        view.enter_personal_info.setNavigationIcon(R.drawable.ic_back_button) // Установим иконку в тулбаре
        view.enter_personal_info.setNavigationOnClickListener(navigationClickListener) // Установим обработчик нажатий на тулбар
        return view
    }

    /**
     * Закрывает текущий фрагмент и удаляет его со стека
     */
    private fun closeFragment() {
        val trans = fragmentManager!!.beginTransaction()
        trans.remove(this)
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        trans.commit()
        fragmentManager!!.popBackStack()
    }
}

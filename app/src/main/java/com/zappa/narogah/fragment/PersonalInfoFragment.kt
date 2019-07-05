package com.zappa.narogah.fragment


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.zappa.narogah.R
import com.zappa.narogah.activity.booking.BookingEnd
import com.zappa.narogah.tools.retrofit.DTOs.accountDTOs.userDataDTO
import com.zappa.narogah.tools.retrofit.DTOs.accountDTOs.verifyEmailDTO
import com.zappa.narogah.tools.retrofit.DTOs.bookingDTOs.bookingUserDTO
import com.zappa.narogah.tools.retrofit.DTOs.respDTO
import com.zappa.narogah.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_personal_info.*
import kotlinx.android.synthetic.main.fragment_personal_info.view.*
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_CODE_BOOKING_END: Int = 12

/**
 * Фрагмент персональное инфо
 * Калька с одноименной активити
 * TODO доделать
 */
class PersonalInfoFragment : Fragment() {

    var canClickContinue: Boolean = true

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
        val bookTableId = arguments!!.getInt("table_id", 1)
        val seatCount = arguments!!.getInt("seat_count", 1).toString()
        val seatPosition = arguments!!.getString("seat_position")
        val seatType = arguments!!.getString("seat_type")

        val newBooking = bookingUserDTO()
        newBooking.date = bookDateBegin!!.toString()
        newBooking.time_from = bookTimeBegin!!.toString()
        newBooking.time_to = bookTimeEnd!!.toString()
        newBooking.table_id = bookTableId
        newBooking.date_to = bookDateEnd!!.toString()

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
            val textTable = getString(com.zappa.narogah.R.string.table4, bookTableId, seatCount, seatType)
            view.fm_selected_table.setText(textTable)
        } else if (seatPosition == "" && seatCount != "4") {
            val textTable = getString(com.zappa.narogah.R.string.table68, bookTableId, seatCount, seatType)
            view.fm_selected_table.setText(textTable)
        } else if (seatPosition != "" && seatCount == "4") {
            val textTable = getString(
                com.zappa.narogah.R.string.table43,
                bookTableId,
                seatCount,
                seatPosition,
                seatType
            )
            view.fm_selected_table.setText(textTable)
        } else {
            val textTable =
                getString(
                    com.zappa.narogah.R.string.table683,
                    bookTableId,
                    seatCount,
                    seatPosition,
                    seatType
                )
            view.fm_selected_table.setText(textTable)
        }

        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy")
        val date: Date = inputFormat.parse(bookDateBegin)
        val outputDateStr = outputFormat.format(date)
        view.fm_selected_date.text = outputDateStr

        val textWoSecI = bookTimeBegin.toString()
        val textWoSecO = bookTimeEnd.toString()
        val woSecI = deleteSecInTime(textWoSecI)
        val woSecO = deleteSecInTime(textWoSecO)
        var text = getString(com.zappa.narogah.R.string.period, woSecI, woSecO)
        view.fm_selected_time.text = text

        view.fm_btnContinue.setOnClickListener {

            // TODO эти данные больше не нужно получать из полей в XML, а получать из ШередПреференс
            //val name = view.username_input_layout.text.toString()
            val name = "KEK"
            if (canClickContinue) {
                canClickContinue = false
                networkRxjavaPost(newBooking, it.context)
            }
        }

        return view
    }

    //Пост запрос на размещение личной информации(RxJava2)
    private fun networkRxjavaPost(newBooking: bookingUserDTO, context: Context) {

        val sharedPreferences =
            activity!!.getSharedPreferences(this.getString(R.string.user_info), Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString(this.getString(R.string.access_token), "null")!!.toString()
        newBooking.email = sharedPreferences.getString(this.getString(R.string.user_email), "")!!.toString()

        RetrofitClientInstance.getInstance()
            .postReserve(jwt, newBooking)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<respDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<respDTO>) {
                    if (t.isSuccessful) {
                        if (t.body() != null) {
                            val code = t.code()
                            if (code == 200) {
                                val sharedPreferences = context.getSharedPreferences(
                                    context.getString(R.string.key_shared_users),
                                    Context.MODE_PRIVATE
                                )
                                //преференсы будут не нужны, получать данные о юзере из функции
                                val savedName = context.getString(R.string.key_user_name)
                                val savedPhone = context.getString(R.string.key_user_phone)
                                val savedEmail = context.getString(R.string.key_user_email)
                                val editor = sharedPreferences.edit()
                                editor.putString(savedName, "временное имя")
                                editor.putString(savedPhone, "временный телефон")
                                editor.putString(savedEmail, newBooking.email)
                                editor.apply()
                            }
                            val intent = Intent(context, BookingEnd::class.java)
                            intent.putExtra("code", t.code())
                            intent.putExtra("name", fm_username.text)
                            intent.putExtra("phone", fm_phone.text)
                            //context.startActivity(intent)
                            startActivityForResult(intent, REQUEST_CODE_BOOKING_END)
                        }
                    } else {
                        val intent = Intent(context, BookingEnd::class.java)
                        intent.putExtra("code", t.code())
                        intent.putExtra("name", fm_username.text)
                        intent.putExtra("phone", fm_phone.text)
                        //finish()
                        //closeFragment() // Закроем фрагмент
                        startActivityForResult(intent, REQUEST_CODE_BOOKING_END)
                    }
                }

                override fun onError(e: Throwable) {
                    showNoInternetConnectionAlert(newBooking, context)
                }

            })
    }

    private fun deleteSecInTime(str: String): String {
        return str.substring(0, str.length - 3)
    }

    /**
     * Настраивает тулбар
     */
    private fun initToolBar(view: View): View {
        view.fm_enter_personal_info.setNavigationIcon(R.drawable.ic_back_button) // Установим иконку в тулбаре
        view.fm_enter_personal_info.setNavigationOnClickListener(navigationClickListener) // Установим обработчик нажатий на тулбар
        return view
    }

    /**
     * TODO использовать для получения данных пользователя, то есть тут и в PersonalInfoFragment
     * юзер неавторизирован или ещё какая херня, но запрос выполнен. Посмотреть код t.code() и обработать
     *если 401 запустить активити авторизации, если успешно авторизовался выкинуть обратно сюда и обновить
     *содержимое фрагмента, видимо через отслеживание результата активити опять, хз
     */
    private fun showUserCredentials() {

        val sharedPreferences =
            activity!!.getSharedPreferences(this.getString(R.string.user_info), Context.MODE_PRIVATE)
        val newShowUser = verifyEmailDTO()
        newShowUser.email = sharedPreferences.getString(this.getString(R.string.user_email), "")!!.toString()
        val jwt = sharedPreferences.getString(this.getString(R.string.access_token), "null")!!.toString()

        RetrofitClientInstance.getInstance()
            .postViewUserCredentials(jwt)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<userDataDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<userDataDTO>) {
                    val view = this@PersonalInfoFragment.view
                    if (view != null) {
                        when(t.code()){
                            200 -> {
                                val data = t.body()!!.data
                                fm_username.text = data.name
                                fm_phone.text = data.phone
                            }
                            401 -> {
                                closeFragment()
                                Toast.makeText(context, "Ваша сессия истекла. Пожалуйста, авторизуйтесь.", Toast.LENGTH_SHORT).show()
                                /*val intent = Intent(context, LoginActivity::class.java)
                                startActivity(intent)*/
                            }
                            else -> {
                                Log.i("errorCode", t.code().toString())
                                Toast.makeText(context, "Ошибка соединения с сервером. Пожалуйста, повторите попытку позже", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        /*if (t.isSuccessful) {
                            *//**
                             * TODO при получении проверять, что поля не равны нулл
                             *//*
                            val data = t.body()!!.data
                            fm_username.text = data.name
                            fm_phone.text = data.phone
                        } else {
                            *//**
                             * TODO
                             * юзер неавторизирован или ещё какая херня, но запрос выполнен. Посмотреть код t.code() и обработать
                             *если 401 запустить активити авторизации, если успешно авторизовался выкинуть обратно сюда и обновить
                             *содержимое фрагмента, видимо через отслеживание результата активити опять, хз
                             *//*

                        }*/
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(
                        context,
                        "Проверьте ваше интернет подключение",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    override fun onResume() {
        canClickContinue = true
        showUserCredentials()
        super.onResume()
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

    /**
     * Проверят как завершила работу активити вызванная на результат
     * @param requestCode Код вызова
     * @param resultCode Код результата работы активити
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_BOOKING_END) {
            if (resultCode == Activity.RESULT_OK) {
                val trans = fragmentManager!!.beginTransaction()
                val personalInfoFragment = fragmentManager!!.findFragmentByTag("PERSONAL_INFO")
                val tableSelectFragment = fragmentManager!!.findFragmentByTag("TABLE_SELECT")

                if (personalInfoFragment != null && tableSelectFragment != null) {
                    trans.remove(personalInfoFragment)
                    trans.remove(tableSelectFragment)
                    trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    trans.commit()
                    fragmentManager!!.popBackStack()
                }
            }
        }
    }

    private var alertIsShown = false
    /**
     * Выводит диалоговое окно с сообщением об отсутствии интернета
     */
    private fun showNoInternetConnectionAlert(newBooking: bookingUserDTO, context: Context) {
        if (!alertIsShown) {
            alertIsShown = true
            val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.AlertDialogCustom)
            builder.setTitle("Ошибка соединения")
                .setMessage("Без подключения к сети невозможно продолжить бронирование.\nПроверьте соединение и попробуйте снова")
                .setCancelable(false)
                .setPositiveButton("ПОВТОРИТЬ") { dialog, which ->
                    run {
                        dialog.dismiss()
                        networkRxjavaPost(newBooking, context)
                        alertIsShown = false
                    }
                }
                .setNegativeButton("ОТМЕНА") { dialog, which ->
                    run {
                        dialog.dismiss()
                        canClickContinue = true
                        alertIsShown = false
                    }
                }
            val alert = builder.create()
            alert.show()
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.color_accent))
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.color_accent))
        }
    }
}
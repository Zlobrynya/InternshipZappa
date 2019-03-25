package com.zlobrynya.internshipzappa.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.profile.LoginActivity
import com.zlobrynya.internshipzappa.adapter.booking.AdapterTable

import com.zlobrynya.internshipzappa.adapter.booking.Table
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.bookingDataDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import com.zlobrynya.internshipzappa.util.TableParceling
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_table_select.view.*
import retrofit2.Response

const val REQUEST_CODE: Int = 11

class TableSelectFragment : Fragment(), AdapterTable.OnTableListener {

    /**
     * Объект для POST запроса
     */
    private val newBooking = bookingDataDTO()

    /**
     * Обработчик нажатий на стрелочку в тулбаре
     */
    private val navigationClickListener = View.OnClickListener {
        // Удалим фрагмент со стека
        val trans = fragmentManager!!.beginTransaction()
        trans.remove(this)
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        trans.commit()
        fragmentManager!!.popBackStack()
    }

    /**
     * Список столиков
     */
    private val tableList: ArrayList<Table> = ArrayList()

    /**
     * Вьюшка для фрагмента
     */
    private lateinit var tableView: View

    /**
     * Номер выбранного стола в ресайклер вью
     */
    var selectedPosition: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tableView = inflater.inflate(R.layout.fragment_table_select, container, false)
        initRequest()
        initToolBar()
        initTableList()
        initRecycler()
        return tableView
    }

    /**
     * Заполняет объект для POST запрос
     */
    private fun initRequest() {
        val bookDateBegin: String? = arguments!!.getString("book_date_begin")
        val bookDateEnd: String? = arguments!!.getString("book_date_end")
        val bookTimeBegin: String? = arguments!!.getString("book_time_begin")
        val bookTimeEnd: String? = arguments!!.getString("book_time_end")
        newBooking.date = bookDateBegin
        newBooking.time_from = bookTimeBegin
        newBooking.time_to = bookTimeEnd
        newBooking.date_to = bookDateEnd
    }

    /**
     * Настраивает тулбар
     */
    private fun initToolBar() {
        tableView.toolbar.setNavigationIcon(R.drawable.ic_back_button) // Установим иконку в тулбаре
        tableView.toolbar.setNavigationOnClickListener(navigationClickListener) // Установим обработчик нажатий на тулбар
    }

    /**
     * Заполняет данными список столиков
     */
    private fun initTableList() {
        val tableArray = arguments!!.getParcelable("table_list") as TableParceling
        for (i in 0 until tableArray.tableList.data.size) {
            val tmp = tableArray.tableList.data[i]
            val table = Table(tmp.chair_count, tmp.position, tmp.chair_type, tmp.table_id)
            tableList.add(table)
        }
        arguments!!.remove("table_list")
    }

    /**
     * Настраивает ресайклер вью
     */
    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        tableView.table_recycler.layoutManager = layoutManager
        tableView.table_recycler.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL) // Разделитель элементов внутри ресайклера
        )
        tableView.table_recycler.adapter = AdapterTable(tableList, this)
    }

    /**
     * Реализация интерфейса для обработки нажатий вне адаптера
     * @param position Позиция элемента
     * @param isButtonClick Произошло ли нажатие на кнопку "выбрать"
     */
    override fun onTableClick(position: Int, isButtonClick: Boolean) {
        //val newStatus = checkDTO()
        val sharedPreferencesStat =
            context?.getSharedPreferences(this.getString(R.string.user_info), Context.MODE_PRIVATE)

        val access_token = context?.getString(R.string.access_token)
        val authStatus = sharedPreferencesStat?.getString(access_token, "null").toString()
        //newStatus.uuid = authStatus

        if (isButtonClick) {
            selectedPosition = position
            checkStatus(position)
            /*RetrofitClientInstance.getInstance()
                .postStatusData(newStatus)
                .subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : Observer<Response<respDTO>> {

                    override fun onComplete() {}

                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(t: Response<respDTO>) {
                        Log.d("onNextTA", "зашёл")
                        //responseBodyStatus = t.body()
                        Log.i("check2", "${t.code()}")

                        if(t.code() != 200) {
                             Log.i("check3", t.code().toString())
                             val intent = Intent(context, LoginActivity::class.java)
                             intent.putExtra("table_id", tableList[position].seatId)
                             intent.putExtra("book_date_begin", newBooking.date)
                             intent.putExtra("book_date_end", newBooking.date_to)
                             intent.putExtra("book_time_begin", newBooking.time_from)
                             intent.putExtra("book_time_end", newBooking.time_to)
                             intent.putExtra("seat_count", tableList[position].seatCount)
                             intent.putExtra("seat_position", tableList[position].seatPosition)
                             intent.putExtra("seat_type", tableList[position].seatType)
                             startActivity(intent)
                        }
                        *//*if (t.isSuccessful) {

                        } else {
                            Log.i("check2", "${t.code()}")


                        }*//*
                    }

                    override fun onError(e: Throwable) {
                        Log.i("check", "that's not fineIn")
                    }

                })*/
        }

    }

    /**
     * Загружает фрагмент с персональной инфой
     * @param position Номер выбранного столика
     */
    private fun openPersonalInfo(position: Int) {
        // Подготовим аргументы
        val args = Bundle()
        args.putInt("table_id", tableList[position].seatId)
        args.putString("book_date_begin", newBooking.date)
        args.putString("book_date_end", newBooking.date_to)
        args.putString("book_time_begin", newBooking.time_from)
        args.putString("book_time_end", newBooking.time_to)
        args.putInt("seat_count", tableList[position].seatCount)
        args.putString("seat_position", tableList[position].seatPosition)
        args.putString("seat_type", tableList[position].seatType)

        // Загрузим фрагмент персональной инфы
        val trans = fragmentManager!!.beginTransaction()
        val personalInfoFragment = PersonalInfoFragment()
        personalInfoFragment.arguments = args
        trans.add(R.id.root_frame, personalInfoFragment)
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        trans.addToBackStack(null)
        trans.commit()
    }

    /**
     * Проверяет, авторизован ли юзер
     * @param position Номер выбранного столика
     */
    private fun checkStatus(position: Int) {
        //val newStatus = checkDTO()
        val sharedPreferencesStat =
            context?.getSharedPreferences(this.getString(R.string.user_info), Context.MODE_PRIVATE)
        val access_token = context?.getString(R.string.access_token)
        val authStatus = sharedPreferencesStat?.getString(access_token, "null").toString()
        val savedEmail = context?.getString(R.string.user_email)
        //newStatus.uuid = authStatus
        //newStatus.email = sharedPreferencesStat?.getString(savedEmail, "null").toString()


        Log.i("checkStatusData", authStatus)


        RetrofitClientInstance.getInstance()
            .getStatusData(authStatus)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<respDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<respDTO>) {
                    Log.d("BOOP", "Код ${t.code()}")
                    if (t.isSuccessful) { // Юзер авторизован
                        Log.d("BOOP", "Юзер авторизован")
                        openPersonalInfo(position) // Откроем фрагмент с персональной инфой
                    } else { // Юзер не авторизован
                        Log.d("BOOP", "Не авторизован")
                        openLoginActivity() // Откроем аквтивити авторизации
                    }
                }

                override fun onError(e: Throwable) {
                    Log.d("BOOP", "Вообще ошибка")
                }
            })
    }

    /**
     * Открывает логин активити
     */
    private fun openLoginActivity() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }

    /**
     * Проверят как завершила работу активити вызванная на результат
     * @param requestCode Код вызова
     * @param resultCode Код результата работы активити
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Юзер авторизовался
                openPersonalInfo(selectedPosition) // Откроем фрагмент с персональной инфой
                Log.d("BOOP", "Выбранный стол $selectedPosition")
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Юзер закрыл авторизацию
            }
        }
    }
}

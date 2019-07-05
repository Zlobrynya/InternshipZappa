package com.zappa.narogah.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zappa.narogah.R
import com.zappa.narogah.activity.MenuActivity
import com.zappa.narogah.tools.retrofit.DTOs.accountDTOs.userDataDTO
import com.zappa.narogah.tools.retrofit.DTOs.accountDTOs.verifyEmailDTO
import com.zappa.narogah.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*

import kotlinx.android.synthetic.main.fragment_profile.view.*
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Фрагмент профиля.
 *
 */
class ProfileFragment : Fragment() {

    var lastCLickTime: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.btn_profile_exit.setOnClickListener {
            val sharedPreferencesStat = context!!.getSharedPreferences(
                context!!.getString(R.string.user_info),
                Context.MODE_PRIVATE
            )
            val savedEmail = context!!.getString(R.string.user_email)
            val access_token = context!!.getString(R.string.access_token)
            val editor = sharedPreferencesStat.edit()
            editor.putString(savedEmail, "")
            editor.putString(access_token, "")
            editor.apply()

            val intent = Intent(context, MenuActivity::class.java)
            startActivity(intent)

        }

        view.btn_edit_profile.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastCLickTime < 1000) {
                return@setOnClickListener
            } else {
                lastCLickTime = SystemClock.elapsedRealtime()
                val args = Bundle()
                args.putString("name", profile_username.text.toString())
                if (profile_dob.text.toString() == "Не указана") {
                    args.putString("dob", "")
                } else {
                    args.putString("dob", profile_dob.text.toString())
                }
                args.putString("email", profile_email.text.toString())
                args.putString("phone", profile_phone.text.toString())

                val trans = fragmentManager!!.beginTransaction()
                val editProfileFragment = EditProfileFragment()
                editProfileFragment.arguments = args
                trans.add(R.id.root_frame2, editProfileFragment)
                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                trans.addToBackStack(null)
                trans.commit()
            }
        }

        return view
    }

    override fun onResume() {
        showUserCredentials()
        super.onResume()
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)


    /**
     * TODO использовать для получения данных пользователя, то есть тут и в PersonalInfoFragment
     * юзер неавторизирован или ещё какая херня, но запрос выполнен. Посмотреть код t.code() и обработать
     *если 401 запустить активити авторизации, если успешно авторизовался выкинуть обратно сюда и обновить
     *содержимое фрагмента, видимо через отслеживание результата активити опять, хз
     */
    private fun showUserCredentials() {

        val view = this.view
        if (view != null) {
            view.progress_spinner.visibility = View.VISIBLE // Покажем спиннер загрузки
        }
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

                    if (t.isSuccessful) {
                        /**
                         * TODO при получении проверять, что поля не равны нулл
                         */
                        val data = t.body()!!.data
                        profile_username.text = data.name
                        if (data.birthday != null) {
                            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                            val outputFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy")
                            val date: Date = inputFormat.parse(data.birthday)
                            val outputDateStr = outputFormat.format(date)
                            profile_dob.text = outputDateStr
                        } else {
                            profile_dob.text = "Не указана"
                        }
                        profile_email.text = data.email
                        profile_phone.text = data.phone

                    } else {
                        /**
                         * TODO
                         * юзер неавторизирован или ещё какая херня, но запрос выполнен. Посмотреть код t.code() и обработать
                         *если 401 запустить активити авторизации, если успешно авторизовался выкинуть обратно сюда и обновить
                         *содержимое фрагмента, видимо через отслеживание результата активити опять, хз
                         */

                    }
                    val view = this@ProfileFragment.view
                    if (view != null) {
                        view.progress_spinner.visibility = View.GONE // Скроем спиннер загрузки
                    }
                }

                override fun onError(e: Throwable) {
                    //запрос не выполнен, всё плохо
                }

            })
    }

}

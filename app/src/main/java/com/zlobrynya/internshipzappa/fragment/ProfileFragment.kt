package com.zlobrynya.internshipzappa.fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.userCredentialsDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.userDataDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyEmailDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.deleteBookingDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.fragment_profile.view.*
import retrofit2.Response

/**
 * Фрагмент профиля.
 *
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_profile, container, false)

        showUserCredentials()
        view.profile_exit.setOnClickListener {
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
        }

        view.btnEdit.setOnClickListener {
            val trans = fragmentManager!!.beginTransaction()
            val editProfileFragment = EditProfileFragment()
            trans.add(R.id.root_frame2, editProfileFragment)
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            trans.addToBackStack(null)
            trans.commit()
        }

        return view
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)


    /**
     * TODO использовать для получения данных пользователя, то есть тут и в PersonalInfoFragment
     * юзер неавторизирован или ещё какая херня, но запрос выполнен. Посмотреть код t.code() и обработать
     *если 401 запустить активити авторизации, если успешно авторизовался выкинуть обратно сюда и обновить
     *содержимое фрагмента, видимо через отслеживание результата активити опять, хз
     */
    private fun showUserCredentials(){

        val sharedPreferences =
            activity!!.getSharedPreferences(this.getString(R.string.user_info), Context.MODE_PRIVATE)
        val newShowUser = verifyEmailDTO()
        newShowUser.email = sharedPreferences.getString(this.getString(R.string.user_email), "")!!.toString()
        val jwt = sharedPreferences.getString(this.getString(R.string.access_token), "null")!!.toString()

        RetrofitClientInstance.getInstance()
            .postViewUserCredentials(jwt, newShowUser)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<userDataDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<userDataDTO>) {
                    Log.i("checkMyCredentials", "${t.code()}")

                    if (t.isSuccessful) {
                        /**
                         * TODO при получении проверять, что поля не равны нулл
                         */
                        val data =t.body()!!.data
                        Log.i("checkMyCredentials", t.body().toString())
                        Log.i("checkMyCredentials", data.toString())
                        //Log.i("checkMyCredentials", data.birthday)
                        Log.i("checkMyCredentials", data.email)
                        Log.i("checkMyCredentials", data.name)
                        Log.i("checkMyCredentials", data.phone)
                        Log.i("checkMyCredentials", data.reg_date)
                    } else {
                        /**
                         * TODO
                         * юзер неавторизирован или ещё какая херня, но запрос выполнен. Посмотреть код t.code() и обработать
                         *если 401 запустить активити авторизации, если успешно авторизовался выкинуть обратно сюда и обновить
                         *содержимое фрагмента, видимо через отслеживание результата активити опять, хз
                         */
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i("check", "that's not fineIn")
                    //запрос не выполнен, всё плохо
                }

            })
    }

}

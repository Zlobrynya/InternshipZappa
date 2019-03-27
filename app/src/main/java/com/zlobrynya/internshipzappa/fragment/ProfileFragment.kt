package com.zlobrynya.internshipzappa.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.Menu2Activity
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.userDataDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyEmailDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_profile, container, false)


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

            val intent = Intent(context, Menu2Activity::class.java)
            startActivity(intent)

        }

        view.btnEdit.setOnClickListener {
            val args = Bundle()
            args.putString("name", profile_username.text.toString())
            args.putString("dob", profile_dob.text.toString())
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

        return view
    }

    override fun onResume() {
        Log.d("BOOP", "onResume profileFragment")
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
                        val data = t.body()!!.data
                        Log.i("checkMyCredentials", t.body().toString())
                        Log.i("checkMyCredentials", data.toString())
                        //Log.i("checkMyCredentials", data.birthday)
                        Log.i("checkMyCredentials", data.email)
                        Log.i("checkMyCredentials", data.name)
                        Log.i("checkMyCredentials", data.phone)
                        Log.i("checkMyCredentials", data.reg_date)
                        profile_username.text = data.name
                        if (data.birthday != null) {
                            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                            val outputFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy")
                            val date: Date = inputFormat.parse(data.birthday)
                            val outputDateStr = outputFormat.format(date)
                            profile_dob.text = outputDateStr
                        } else {
                            profile_dob.text = ""
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
                }

                override fun onError(e: Throwable) {
                    Log.i("check", "that's not fineIn")
                    //запрос не выполнен, всё плохо
                }

            })
    }

}

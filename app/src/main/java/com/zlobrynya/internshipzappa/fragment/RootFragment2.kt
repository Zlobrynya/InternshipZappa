package com.zlobrynya.internshipzappa.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * Фрагмент-контейнер для фрагментов связанных с профилем
 */
class RootFragment2 : Fragment() {

    val profileFragment = ProfileFragment() // Фрагмент для авторизованного юзера
    val profileLoginFragment = ProfileLoginFragment() // Фрагмент для неавторизованного юзера

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_root_2, container, false)
        return view
    }

    override fun onResume() {
        Log.d("BOOP", "RootFramgent2 onResume")
        super.onResume()
        checkStatus()
    }

    /**
     * Заменяет фрагмент в контейнере
     * @param fragment Фрагмент, который нужно вставить
     */
    private fun replaceFragment(fragment: Fragment) {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.root_frame2, fragment)
        transaction.commit()
    }

    /**
     * Проверяет, авторизован ли юзер
     */
    private fun checkStatus() {
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
                        replaceFragment(profileFragment)
                    } else { // Юзер не авторизован
                        Log.d("BOOP", "Не авторизован")
                        replaceFragment(profileLoginFragment)
                    }
                }

                override fun onError(e: Throwable) {
                    Log.d("BOOP", "Вообще ошибка")
                }
            })
    }

}

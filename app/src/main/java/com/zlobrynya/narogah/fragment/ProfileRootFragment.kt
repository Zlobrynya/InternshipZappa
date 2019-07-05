package com.zlobrynya.narogah.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.narogah.R
import com.zlobrynya.narogah.tools.retrofit.DTOs.respDTO
import com.zlobrynya.narogah.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * Фрагмент-контейнер для фрагментов связанных с профилем
 */
class ProfileRootFragment : Fragment() {

    val profileFragment = ProfileFragment() // Фрагмент для авторизованного юзера
    val profileLoginFragment = ProfileLoginFragment() // Фрагмент для неавторизованного юзера

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_root, container, false)
    }

    override fun onResume() {
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

        RetrofitClientInstance.getInstance()
            .getStatusData(authStatus)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<respDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<respDTO>) {
                    if (t.isSuccessful) { // Юзер авторизован
                        replaceFragment(profileFragment)
                    } else { // Юзер не авторизован
                        replaceFragment(profileLoginFragment)
                    }
                }

                override fun onError(e: Throwable) {

                }
            })
    }

}

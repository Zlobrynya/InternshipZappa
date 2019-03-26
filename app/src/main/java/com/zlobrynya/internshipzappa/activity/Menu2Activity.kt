package com.zlobrynya.internshipzappa.activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.fragment.BookingFragment
import com.zlobrynya.internshipzappa.fragment.KontaktiFragment
import com.zlobrynya.internshipzappa.fragment.ProfileFragment
import com.zlobrynya.internshipzappa.fragment.menu.MenuFragment
import kotlinx.android.synthetic.main.activity_menu2.*
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.zlobrynya.internshipzappa.activity.profile.LoginActivity
import com.zlobrynya.internshipzappa.fragment.*
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

const val MENU_PAGE: Int = 0
const val BOOKING_PAGE: Int = 1
const val CONTACTS_PAGE: Int = 2
const val PROFILE_PAGE: Int = 3

const val REQUEST_CODE: Int = 11

class Menu2Activity : AppCompatActivity() {

    private val menuFragment: MenuFragment = MenuFragment()
    private val contactsFragment: KontaktiFragment = KontaktiFragment()
    private val rootFragment: RootFragment = RootFragment()
    private val rootFragment2: RootFragment2 = RootFragment2()
    private val profileFragment: ProfileFragment = ProfileFragment()

    internal var prevMenuItem: MenuItem? = null
    private var toolbar: ActionBar? = null

    var mPagerAdapter: SlidePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu2)
        hideToolbar()
        initNavigation()
        initAdapter()
    }

    /**
     * Инициалирует навигацию по вкладкам
     */
    private fun initNavigation() {
        navigation2.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_menu -> {
                    viewpager2.currentItem = MENU_PAGE
                }
                R.id.navigation_booking -> {
                    viewpager2.currentItem = BOOKING_PAGE
                }
                R.id.navigation_profile -> {
                    checkStatus()
                }
            }
            false
        }

        viewpager2.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem!!.isChecked = false
                } else {
                    navigation2.menu.getItem(0).isChecked = false
                }
                Log.d("page", "onPageSelected: $position")
                navigation2.menu.getItem(position).isChecked = true
                prevMenuItem = navigation2.menu.getItem(position)

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        viewpager2.offscreenPageLimit = 3 // Установим отступ для кеширования страниц
    }

    /**
     * Инициализирует адаптер
     */
    private fun initAdapter() {
        mPagerAdapter = SlidePagerAdapter(supportFragmentManager)
        viewpager2.adapter = mPagerAdapter
    }

    /**
     * Скрывает тулбар
     */
    private fun hideToolbar() {
        toolbar = supportActionBar
        toolbar!!.hide()
    }

    /**
     * Кастомный адаптер для постраничной навигации
     */
    inner class SlidePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            return when (position) {
                MENU_PAGE -> menuFragment
                BOOKING_PAGE -> rootFragment // На вкладку "Бронь" положим фрагмент-контейнер
                else -> rootFragment2
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }

    /**
     * Проверяет, авторизован ли юзер
     */
    private fun checkStatus() {
        val sharedPreferencesStat = this.getSharedPreferences(this.getString(R.string.user_info), Context.MODE_PRIVATE)
        val access_token = this.getString(R.string.access_token)
        val authStatus = sharedPreferencesStat?.getString(access_token, "null").toString()

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
                        viewpager2.currentItem = PROFILE_PAGE // Откроем фрагмент rootFragment2
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
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }
}

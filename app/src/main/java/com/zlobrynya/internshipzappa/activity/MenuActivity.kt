package com.zlobrynya.internshipzappa.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.fragment.CategoryFragment
import com.zlobrynya.internshipzappa.tools.json.MenuDish
import com.zlobrynya.internshipzappa.tools.json.ParsJson
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        //Get json data from file
        ParsJson.getInstance().getMenu(this).subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<MenuDish> {
                override fun onComplete() {
                    println("Complete")
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: MenuDish) {
                    viewPagerMenu.adapter = AdapterPageView(supportFragmentManager,6, t)
                }

                override fun onError(e: Throwable) {
                    println(e.toString())
                }

            })

        //val adapter = AdapterPageView(supportFragmentManager,4)

    }

    class AdapterPageView(val gm: FragmentManager, val countF: Int, val menuDish: MenuDish): FragmentPagerAdapter(gm) {
        override fun getItem(position: Int): Fragment? {
            when(position){
                0 -> return CategoryFragment.newInstance(menuDish.hotArray)
                1 -> return CategoryFragment.newInstance(menuDish.saladsArray)
                2 -> return CategoryFragment.newInstance(menuDish.soupArray)
                3 -> return CategoryFragment.newInstance(menuDish.nonalcArray)
                4 -> return CategoryFragment.newInstance(menuDish.burgerArray)
                5 -> return CategoryFragment.newInstance(menuDish.beerArray)
            }
            return Fragment::class.java.newInstance()
        }

        override fun getCount(): Int = countF
    }
}
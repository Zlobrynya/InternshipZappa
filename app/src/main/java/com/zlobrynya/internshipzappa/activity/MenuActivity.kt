package com.zlobrynya.internshipzappa.activity

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.AdapterTab
import com.zlobrynya.internshipzappa.fragment.CategoryFragment
import com.zlobrynya.internshipzappa.tools.json.MenuDish
import com.zlobrynya.internshipzappa.tools.json.ParsJson
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_scrolling.*
import android.support.design.widget.AppBarLayout
import android.util.Log


class MenuActivity: AppCompatActivity() {
    private val imageResId = intArrayOf(R.mipmap.ic_hot, R.mipmap.ic_salad, R.mipmap.ic_broth,
        R.mipmap.ic_soda, R.mipmap.ic_burger, R.mipmap.ic_beer )

    private val stringId = intArrayOf(R.string.hot, R.string.salad, R.string.broth,
        R.string.soda, R.string.burger, R.string.beer )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        val title = getString(R.string.menu_toolbar)

        appBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                Log.i("appBar", scrollRange.toString() + " " + verticalOffset)
                if (scrollRange + verticalOffset == 0) {
                    toolbarLayout.title = title
                    isShow = true
                } else if (isShow) {
                    toolbarLayout.setTitle(" ")//carefull there should a space between double quote otherwise it wont work
                    isShow = false
                }
            }
        })

        //Get json data from file
        ParsJson.getInstance().getMenu(this).subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<MenuDish> {
                override fun onComplete() {
                    println("Complete")
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: MenuDish) {
                    viewPagerMenu.adapter = AdapterTab(supportFragmentManager, t, 6)
                    sliding_tabs.setupWithViewPager(viewPagerMenu)
                    for (i in 0..5){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            //sliding_tabs.getTabAt(i)?.icon = getDrawable(imageResId[i])
                            sliding_tabs.getTabAt(i)?.text = getString(stringId[i])
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    println(e.toString())
                }

            })

    }
}
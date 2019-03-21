package com.zlobrynya.internshipzappa.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zlobrynya.internshipzappa.R
import kotlinx.android.synthetic.main.fragment_booking_container.view.*


/**
 * Фрагмент-контейнер для фрагментов "новая бронь" и "мои брони"
 */
class BookingContainerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_booking_container, container, false)

        setupViewPager(view.viewPagerBooking)
        view.nav_tabs.setupWithViewPager(view.viewPagerBooking)

        return view
    }

    /**
     * Настраивает вьюпейджер
     */
    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = SectionsPageAdapter(fragmentManager)
        adapter.addFragment(BookingFragment(), "Новая бронь")
        adapter.addFragment(MyBookingsFragment(), "Мои брони")
        viewPager.adapter = adapter
    }

    /**
     * Кастомный адаптер для постраничной навигации
     */
    inner class SectionsPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

        private val mFragmentList: ArrayList<Fragment> = ArrayList()
        private val mFragmentTitleList: ArrayList<String> = ArrayList()

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }
    }
}

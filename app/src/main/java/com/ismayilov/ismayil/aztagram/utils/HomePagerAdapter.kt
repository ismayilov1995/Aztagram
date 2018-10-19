package com.ismayilov.ismayil.aztagram.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class HomePagerAdapter (fm:FragmentManager): FragmentPagerAdapter(fm) {

    private var mFragmentList:ArrayList<Fragment> = ArrayList()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    //bizim shexsi funksiyamiz
    fun addFragment(fragment: Fragment){
        mFragmentList.add(fragment)
    }
}
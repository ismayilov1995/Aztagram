package com.ismayilov.ismayil.aztagram.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

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

    fun removeChoosenFragmentFromViewpager(viewGroup: ViewGroup, postion:Int){
        val removedFile = this.instantiateItem(viewGroup,postion)
        this.destroyItem(viewGroup,postion,removedFile)
    }
    fun addChoosenFragmentToViewpager(viewGroup: ViewGroup, postion:Int){
        this.instantiateItem(viewGroup,postion)
    }
}
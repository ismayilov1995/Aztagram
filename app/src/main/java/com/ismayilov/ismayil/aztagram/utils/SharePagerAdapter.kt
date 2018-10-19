package com.ismayilov.ismayil.aztagram.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

class SharePagerAdapter(fragmentManager: FragmentManager, tabNames: ArrayList<String>) : FragmentPagerAdapter(fragmentManager) {

    var mFragmentList:ArrayList<Fragment> = ArrayList()
    var myTabNames:ArrayList<String> = tabNames

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment:Fragment){
        mFragmentList.add(fragment)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return myTabNames[position]
    }

    fun removeChoosenFragmentFromViewpager(viewGroup:ViewGroup, postion:Int){
        val removedFile = this.instantiateItem(viewGroup,postion)
        this.destroyItem(viewGroup,postion,removedFile)
    }
    fun addChoosenFragmentToViewpager(viewGroup:ViewGroup, postion:Int){
        this.instantiateItem(viewGroup,postion)
    }
}
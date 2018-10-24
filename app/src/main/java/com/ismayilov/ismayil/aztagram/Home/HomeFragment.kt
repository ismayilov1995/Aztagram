package com.ismayilov.ismayil.aztagram.Home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment :Fragment() {


    private val ACTIVITY_NO = 0
    private val TAG = "Home Activity"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container,false)


        return view
    }


    override fun onResume() {
        super.onResume()
        setupNavigationView()
    }


    private fun setupNavigationView(){
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewActivity)
        BottomNavigationViewHelper.setupNavigation(this.activity!!,bottomNavigationViewActivity)
        val menu = bottomNavigationViewActivity.menu
        val menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.isChecked = true
    }
}
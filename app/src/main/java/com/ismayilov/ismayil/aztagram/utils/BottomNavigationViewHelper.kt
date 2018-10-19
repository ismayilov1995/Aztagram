package com.ismayilov.ismayil.aztagram.utils

import android.content.Context
import android.content.Intent
import android.support.design.widget.BottomNavigationView
import com.ismayilov.ismayil.aztagram.Home.HomeActivity
import com.ismayilov.ismayil.aztagram.News.NewsActivity
import com.ismayilov.ismayil.aztagram.Profile.ProfileActivity
import com.ismayilov.ismayil.aztagram.R.id
import com.ismayilov.ismayil.aztagram.Search.SearchActivity
import com.ismayilov.ismayil.aztagram.Share.ShareActivity
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

class BottomNavigationViewHelper {

    companion object {
        fun setupBottomNavigationView(bottomnavigationViewEx: BottomNavigationViewEx){

            bottomnavigationViewEx.enableAnimation(false)
            bottomnavigationViewEx.enableItemShiftingMode(false)
            bottomnavigationViewEx.enableShiftingMode(false)
            bottomnavigationViewEx.setTextVisibility(false)
        }

        fun setupNavigation(context: Context, bottomNavigationViewEx: BottomNavigationViewEx){

            bottomNavigationViewEx.onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

                when(item.itemId){

                    id.ic_home ->{
                        val intent = Intent(context,HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        context.startActivity(intent)
                        return@OnNavigationItemSelectedListener true
                    }
                    id.ic_news ->{
                        val intent = Intent(context,NewsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        context.startActivity(intent)
                        return@OnNavigationItemSelectedListener true
                    }
                    id.ic_profile ->{
                        val intent = Intent(context,ProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        context.startActivity(intent)
                        return@OnNavigationItemSelectedListener true
                    }
                    id.ic_search ->{
                        val intent = Intent(context,SearchActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        context.startActivity(intent)
                        return@OnNavigationItemSelectedListener true
                    }
                    id.ic_share ->{
                        val intent = Intent(context,ShareActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        context.startActivity(intent)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
        }
    }
}
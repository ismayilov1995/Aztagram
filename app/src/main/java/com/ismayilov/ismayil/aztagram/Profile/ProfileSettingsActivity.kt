package com.ismayilov.ismayil.aztagram.Profile

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 4
    private val TAG = "Settings Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        setupNavigationView()
        setupToolbar()
        fragmentNavigation()
    }

    override fun onBackPressed() {
        profileSettingsRoot.visibility = View.VISIBLE
        super.onBackPressed()
    }

    private fun fragmentNavigation() {

        tvProfilSettings.setOnClickListener {
             supportFragmentManager.beginTransaction()
                     .replace(R.id.profileSettingsContainer,ProfileEditFragment())
                     .addToBackStack("ProfEditFragAdded")
                     .commit()

            profileSettingsRoot.visibility = View.GONE
        }

        tvSignout.setOnClickListener {
            val dialog = SignoutFragment()
            dialog.show(supportFragmentManager,"sign out dialog")
            //profileSettingsRoot.visibility = View.GONE
        }



    }

    private fun setupToolbar() {
        imgSettingsBack.setOnClickListener {
            onBackPressed()
        }
    }


    fun setupNavigationView(){
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomNavigationViewHelper.setupNavigation(this,bottomNavigationView)
        val menu = bottomNavigationView.menu
        val menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.isChecked = true
    }
}

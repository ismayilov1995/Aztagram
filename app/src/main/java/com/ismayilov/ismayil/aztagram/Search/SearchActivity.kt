package com.ismayilov.ismayil.aztagram.Search

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.ismayilov.ismayil.aztagram.Login.LoginActivity
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 1
    private val TAG = "Search Activity"
    private lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setupAuthListener()
        setupNavigationView()
        mAuth = FirebaseAuth.getInstance()

        searchView.setOnClickListener {
            startActivity(Intent(this@SearchActivity,AlgoliaSearchActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }



    }





    fun setupNavigationView(){
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewActivity)
        BottomNavigationViewHelper.setupNavigation(this,bottomNavigationViewActivity)
        val menu = bottomNavigationViewActivity.menu.getItem(ACTIVITY_NO)
        menu.isChecked = true
    }

    private fun setupAuthListener() {
        mAuthListener = FirebaseAuth.AuthStateListener {
            val user= FirebaseAuth.getInstance().currentUser
            if (user == null){
                startActivity(Intent(this, LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //setupNavigationView()
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}

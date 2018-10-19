package com.ismayilov.ismayil.aztagram.Home

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ismayilov.ismayil.aztagram.Login.LoginActivity
import com.ismayilov.ismayil.aztagram.Model.Users
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.BottomNavigationViewHelper
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import com.ismayilov.ismayil.aztagram.utils.HomePagerAdapter
import com.ismayilov.ismayil.aztagram.utils.UniversalImageLoader
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.EventBus

class HomeActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 0
    private val TAG = "Home Activity"
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mRef: DatabaseReference
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference
        initImageLoader()
        setupNavigationView()
        setupHomeViewPager()
        setupAuthListener()
        //getUserDeatlist()

    }

    private fun getUserDeatlist() {
        mRef.child("users").child(mAuth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value != null){
                    val snapUserDetails = p0.getValue(Users::class.java)
                    //Butun fragmentlere hazirki user haqqinda data gonderisi
                    EventBus.getDefault().postSticky(EventbusDataEvent.BrodcastUserData(snapUserDetails))
                }
            }

        })
    }

    private fun initImageLoader(){
        val universalImageLoader = UniversalImageLoader(this)
        ImageLoader.getInstance().init(universalImageLoader.config)
    }

    private fun setupHomeViewPager() {
        val homePagerAdapter = HomePagerAdapter(supportFragmentManager)
        homePagerAdapter.addFragment(CameraFragment())
        homePagerAdapter.addFragment(HomeFragment())
        homePagerAdapter.addFragment(DmFragment())

        homeVp.adapter = homePagerAdapter
        homeVp.currentItem = 1
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

    fun setupNavigationView(){
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewActivity)
        BottomNavigationViewHelper.setupNavigation(this,bottomNavigationViewActivity)
        val menu = bottomNavigationViewActivity.menu
        val menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.isChecked = true
    }
}

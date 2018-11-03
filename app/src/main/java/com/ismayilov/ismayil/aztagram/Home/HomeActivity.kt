package com.ismayilov.ismayil.aztagram.Home

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ismayilov.ismayil.aztagram.Login.LoginActivity
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import com.ismayilov.ismayil.aztagram.utils.HomePagerAdapter
import com.ismayilov.ismayil.aztagram.utils.UniversalImageLoader
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
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
        setupAuthListener()
        initImageLoader()
        setupHomeViewPager()

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
        //Hansi seyfeden bashlamali oldugunu deyirik
        homeVp.currentItem = 1

        homePagerAdapter.removeChoosenFragmentFromViewpager(homeVp,0)

        homeVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            override fun onPageSelected(position: Int) {
                if (position == 0){
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                    getStorageAndCameraPermission()
                    homePagerAdapter.addChoosenFragmentToViewpager(homeVp,0)
                }
                if (position == 1){
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    homePagerAdapter.removeChoosenFragmentFromViewpager(homeVp,0)
                }
                if (position == 2){
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    homePagerAdapter.removeChoosenFragmentFromViewpager(homeVp,0)
                }
            }
        })
    }



    private fun getStorageAndCameraPermission() {
        Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.CAMERA)
                .withListener(object : PermissionListener{
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        EventBus.getDefault().postSticky(EventbusDataEvent.SendCameraRequestPermission(true))
                    }
                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                        token!!.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        homeVp.currentItem = 1
                    }
                }).check()
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

    override fun onBackPressed() {

        if (homeVp.currentItem == 1){
            homeVp.visibility = View.VISIBLE
            activityHomeContainer.visibility = View.GONE
            super.onBackPressed()
        }else{
            homeVp.currentItem = 1
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


}

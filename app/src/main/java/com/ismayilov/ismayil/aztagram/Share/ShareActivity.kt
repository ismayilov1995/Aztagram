package com.ismayilov.ismayil.aztagram.Share

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.ismayilov.ismayil.aztagram.Login.LoginActivity
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.RequestPermissionViewFragment
import com.ismayilov.ismayil.aztagram.utils.SharePagerAdapter
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 2
    private val TAG = "Share Activity"
    private lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var requestPermViewFragment:RequestPermissionViewFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        mAuth = FirebaseAuth.getInstance()
        setupAuthListener()
        getStorageAndCameraPermission()

    }

    private fun setupShareViewPager() {

        val tabNames = ArrayList<String>()
        tabNames.add("QALERIYA")
        tabNames.add("SEKIL")
        tabNames.add("VIDEO")

        val sharePagerAdaptor = SharePagerAdapter(supportFragmentManager, tabNames)
        sharePagerAdaptor.addFragment(ShareGalleryFragment())
        sharePagerAdaptor.addFragment(ShareCameraFragment())
        sharePagerAdaptor.addFragment(ShareVideoFragment())

        sharePagerAdaptor.removeChoosenFragmentFromViewpager(shareViewPager,1)
        sharePagerAdaptor.removeChoosenFragmentFromViewpager(shareViewPager,2)
        shareViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            override fun onPageSelected(position: Int) {
                if (position == 0){
                    sharePagerAdaptor.removeChoosenFragmentFromViewpager(shareViewPager,1)
                    sharePagerAdaptor.removeChoosenFragmentFromViewpager(shareViewPager,2)
                    sharePagerAdaptor.addChoosenFragmentToViewpager(shareViewPager,0)
                }
                if (position == 1){
                    sharePagerAdaptor.removeChoosenFragmentFromViewpager(shareViewPager,0)
                    sharePagerAdaptor.removeChoosenFragmentFromViewpager(shareViewPager,2)
                    sharePagerAdaptor.addChoosenFragmentToViewpager(shareViewPager,1)
                }
                if (position == 2){
                    sharePagerAdaptor.removeChoosenFragmentFromViewpager(shareViewPager,0)
                    sharePagerAdaptor.removeChoosenFragmentFromViewpager(shareViewPager,1)
                    sharePagerAdaptor.addChoosenFragmentToViewpager(shareViewPager,2)
                }
            }
        })

        shareViewPager.adapter = sharePagerAdaptor
        shareTabLayout.setupWithViewPager(shareViewPager)
    }

    private fun getStorageAndCameraPermission() {
        Dexter.withActivity(this)
                .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.RECORD_AUDIO)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            setupShareViewPager()
                        }
                        if (report!!.isAnyPermissionPermanentlyDenied) {
                            val requestPermViewFragment = RequestPermissionViewFragment()
                            requestPermViewFragment.show(supportFragmentManager, "permIsRequired")
                            requestPermViewFragment.isCancelable = false
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                        AlertDialog.Builder(this@ShareActivity)
                                .setTitle("Icaze lazimdir")
                                .setMessage("Proqramdan istifade ede bilesen deye icaze vermelisen. Icaze verirsiz ?")
                                .setPositiveButton("BELI") { p0, p1 ->
                                    token!!.continuePermissionRequest()
                                    p0.cancel()
                                }
                                .setNegativeButton("XEYIR") { p0, p1 ->
                                    token!!.cancelPermissionRequest()
                                    p0.cancel()
                                    finish()
                                }
                                .show()
                        //Log.e("HATA", "Icazelerden biri redd edildi")
                    }
                })
                .withErrorListener {
                    Log.e("HATA", "ERROR: $it")
                }
                .check()
    }

    override fun onBackPressed() {
        shareRootContainer.visibility = View.VISIBLE
        shareFragmentContainer.visibility = View.GONE
        super.onBackPressed()
    }

    private fun setupAuthListener() {
        mAuthListener = FirebaseAuth.AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                startActivity(Intent(this@ShareActivity, LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
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
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}

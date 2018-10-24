package com.ismayilov.ismayil.aztagram.Profile

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ismayilov.ismayil.aztagram.Login.LoginActivity
import com.ismayilov.ismayil.aztagram.Model.Users
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.BottomNavigationViewHelper
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import com.ismayilov.ismayil.aztagram.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.activity_profile.*
import org.greenrobot.eventbus.EventBus

class ProfileActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 4
    private val TAG = "Profile Activity"
    private lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mRef:DatabaseReference
    lateinit var curUser:FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupToolbar()
        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference
        curUser = mAuth.currentUser!!
        setupFragmentNavigation()
        getUserDeatlist()



    }

    private fun getUserDeatlist() {
        tvEditProfile.isEnabled = false
        imgProfilSettings.isEnabled = false
        mRef.child("users").child(curUser.uid).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value != null){
                    val snapUserDetails = p0.getValue(Users::class.java)
                    //Butun fragmentlere hazirki user haqqinda data gonderisi
                    EventBus.getDefault().postSticky(EventbusDataEvent.BrodcastUserData(snapUserDetails))
                    tvEditProfile.isEnabled = true
                    imgProfilSettings.isEnabled = true
                    tvProfilAdiToolbar.text = snapUserDetails!!.user_name
                    tvProfileRealName.text = snapUserDetails.full_name
                    tvFollower.text = snapUserDetails.users_details!!.follower
                    tvFollowers.text = snapUserDetails.users_details!!.followers
                    if (!snapUserDetails.users_details?.biography.isNullOrEmpty()){
                        tvBiography.text = snapUserDetails.users_details!!.biography
                        tvBiography.visibility = View.VISIBLE
                    }
                    if (!snapUserDetails.users_details?.web_sitie.isNullOrEmpty()){
                        tvWebSities.text = snapUserDetails.users_details!!.web_sitie
                        tvWebSities.visibility = View.VISIBLE
                    }
                    tvPost.text = snapUserDetails.users_details!!.post
                    val imgUrl = snapUserDetails.users_details!!.profile_picture!!
                    UniversalImageLoader.setImage(imgUrl,cicrcleProfileImage,null,"")



                }
            }

        })
    }

    override fun onBackPressed() {
        profileSettingsRoot.visibility = View.VISIBLE
        super.onBackPressed()
    }

    private fun setupFragmentNavigation() {
        tvEditProfile.setOnClickListener {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.ProfileContainer,ProfileEditFragment())
                    .addToBackStack("ProfildenEdite")
                    .commit()
            profileSettingsRoot.visibility = View.GONE
        }
    }

    private fun setupToolbar() {
        imgProfilSettings.setOnClickListener {
            startActivity(Intent(this,ProfileSettingsActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    private fun setupAuthListener() {
        mAuthListener = FirebaseAuth.AuthStateListener {
            val user= FirebaseAuth.getInstance().currentUser
            if (user == null){
                startActivity(Intent(this, LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupNavigationView()
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

    private fun setupNavigationView(){
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomNavigationViewHelper.setupNavigation(this,bottomNavigationView)
        val menu = bottomNavigationView.menu
        val menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.isChecked = true
    }
}

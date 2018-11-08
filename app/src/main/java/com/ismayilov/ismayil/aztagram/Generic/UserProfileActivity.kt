package com.ismayilov.ismayil.aztagram.Generic

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ismayilov.ismayil.aztagram.Login.LoginActivity
import com.ismayilov.ismayil.aztagram.Model.Posts
import com.ismayilov.ismayil.aztagram.Model.UserPosts
import com.ismayilov.ismayil.aztagram.Model.Users
import com.ismayilov.ismayil.aztagram.Profile.ProfileSettingsActivity
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.BottomNavigationViewHelper
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import com.ismayilov.ismayil.aztagram.utils.ProfileListFragmentRecyclerAdapter
import com.ismayilov.ismayil.aztagram.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.greenrobot.eventbus.EventBus

class UserProfileActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 2
    private val TAG = "Profile Activity"
    private lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mRef: DatabaseReference
    lateinit var mUser: FirebaseUser
    lateinit var allPosts: ArrayList<UserPosts>
    lateinit var choosenUserId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        setupButtons()
        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference
        mUser = mAuth.currentUser!!
        allPosts = ArrayList()
        choosenUserId = intent.getStringExtra("choosenUserId")
        getUserDeatlist()
        getUsersPosts(choosenUserId)

    }


    private fun getUserDeatlist() {
        tvFollowUnf.isEnabled = false
        imgProfilSettings.isEnabled = false
        mRef.child("users").child(choosenUserId).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value != null) {
                    val snapUserDetails = p0.getValue(Users::class.java)
                    //Butun fragmentlere hazirki user haqqinda data gonderisi
                    EventBus.getDefault().postSticky(EventbusDataEvent.BrodcastUserData(snapUserDetails))
                    tvFollowUnf.isEnabled = true
                    imgProfilSettings.isEnabled = true
                    tvProfilAdiToolbar.text = snapUserDetails!!.user_name
                    tvProfileRealName.text = snapUserDetails.full_name
                    tvFollower.text = snapUserDetails.users_details!!.follower
                    tvFollowers.text = snapUserDetails.users_details!!.followers
                    if (!snapUserDetails.users_details?.biography.isNullOrEmpty()) {
                        tvBiography.text = snapUserDetails.users_details!!.biography
                        tvBiography.visibility = View.VISIBLE
                    }
                    if (!snapUserDetails.users_details?.web_sitie.isNullOrEmpty()) {
                        tvWebSities.text = snapUserDetails.users_details!!.web_sitie
                        tvWebSities.visibility = View.VISIBLE
                    }
                    tvPost.text = snapUserDetails.users_details!!.post
                    val imgUrl = snapUserDetails.users_details!!.profile_picture!!
                    UniversalImageLoader.setImage(imgUrl, cicrcleProfileImage, null, "")
                }
                getFollowingInfo()
            }
        })
    }

    private fun getFollowingInfo() {
        mRef.child("following/${mUser.uid}").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChild(choosenUserId)) {
                    unfollowButtonOptions()
                } else {
                    followButtonOptions()
                }
            }

        })
    }

    private fun getUsersPosts(uid: String) {
        mRef.child("users/$uid").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val userPhoto = p0.getValue(Users::class.java)!!.users_details!!.profile_picture
                val userName = p0.getValue(Users::class.java)!!.user_name
                mRef.child("posts/$uid").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0!!.hasChildren()) {
                            for (ds in p0.children) {
                                val allIsAddedPosts = UserPosts()
                                allIsAddedPosts.user_id = uid
                                allIsAddedPosts.user_photo = userPhoto
                                allIsAddedPosts.user_name = userName
                                allIsAddedPosts.post_id = ds.getValue(Posts::class.java)!!.post_id
                                allIsAddedPosts.post_url = ds.getValue(Posts::class.java)!!.file_url
                                allIsAddedPosts.post_description = ds.getValue(Posts::class.java)!!.description
                                allIsAddedPosts.post_upload_date = ds.getValue(Posts::class.java)!!.upload_date
                                allPosts.add(allIsAddedPosts)
                            }
                        }
                        setupRecyclerView(true)
                    }
                })
            }
        })
    }

    private fun setupRecyclerView(isGridView: Boolean) {
        val userPostList = profileRecyclerView
        if (isGridView) {
            /*
            userPostList.adapter = ProfilePostRecyclerAdapter(allPosts, this)
            userPostList.layoutManager = GridLayoutManager(this, 3)
            ivGrid.setColorFilter(ContextCompat.getColor(this@UserProfileActivity, R.color.blue_instagram), PorterDuff.Mode.SRC_IN)
            ivList.setColorFilter(ContextCompat.getColor(this@UserProfileActivity, R.color.black), PorterDuff.Mode.SRC_IN)
            */
        } else {
            userPostList.adapter = ProfileListFragmentRecyclerAdapter(this, allPosts)
            userPostList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            ivGrid.setColorFilter(ContextCompat.getColor(this@UserProfileActivity, R.color.black), PorterDuff.Mode.SRC_IN)
            ivList.setColorFilter(ContextCompat.getColor(this@UserProfileActivity, R.color.blue_instagram), PorterDuff.Mode.SRC_IN)
        }
    }

    fun followButtonOptions() {
        tvFollowUnf.text = "İzlə"
        tvFollowUnf.setTextColor(ContextCompat.getColor(this@UserProfileActivity, R.color.white))
        tvFollowUnf.setBackgroundResource(R.drawable.edirtext_blue_background)
        updateFollowerCount()
    }

    fun unfollowButtonOptions() {
        tvFollowUnf.text = "İzlənilir"
        tvFollowUnf.setTextColor(ContextCompat.getColor(this@UserProfileActivity, R.color.black))
        tvFollowUnf.setBackgroundResource(R.drawable.edirtext_white_background)
        updateFollowerCount()
    }


    private fun updateFollowerCount() {
        mRef = FirebaseDatabase.getInstance().reference
        mRef.child("following/${mUser.uid}").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                val myFollowingCount = p0.childrenCount
                mRef.child("follower/$choosenUserId/").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                     val followerCount = p0.childrenCount
                        mRef.child("users/${mUser.uid}/users_details/followers").setValue(myFollowingCount.toString())
                        mRef.child("users/$choosenUserId/users_details/follower").setValue(followerCount.toString())
                    }
                })
            }
        })
    }

    private fun setupButtons() {
        imgProfilSettings.setOnClickListener {
            startActivity(Intent(this, ProfileSettingsActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }
        ivGrid.setOnClickListener {
            setupRecyclerView(true)
        }
        ivList.setOnClickListener {
            setupRecyclerView(false)
        }
        tvFollowUnf.setOnClickListener {
            mRef.child("following/${mUser.uid}").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChild(choosenUserId)) {
                        mRef.child("following/${mUser.uid}/$choosenUserId").removeValue()
                        mRef.child("follower/$choosenUserId/${mUser.uid}").removeValue()
                        followButtonOptions()
                    } else {
                        mRef.child("following/${mUser.uid}/$choosenUserId").setValue(choosenUserId)
                        mRef.child("follower/$choosenUserId/${mUser.uid}").setValue(mUser.uid)
                        unfollowButtonOptions()
                    }
                }
            })
        }
    }

    private fun setupAuthListener() {
        mAuthListener = FirebaseAuth.AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
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
        setupNavigationView()
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

    private fun setupNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomNavigationViewHelper.setupNavigation(this, bottomNavigationView)
        val menu = bottomNavigationView.menu
        val menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.isChecked = true
    }

}

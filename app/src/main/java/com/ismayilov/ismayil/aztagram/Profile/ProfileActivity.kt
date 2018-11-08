package com.ismayilov.ismayil.aztagram.Profile

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hoanganhtuan95ptit.autoplayvideorecyclerview.AutoPlayVideoRecyclerView
import com.ismayilov.ismayil.aztagram.Login.LoginActivity
import com.ismayilov.ismayil.aztagram.Model.Posts
import com.ismayilov.ismayil.aztagram.Model.UserPosts
import com.ismayilov.ismayil.aztagram.Model.Users
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.Search.AlgoliaSearchActivity
import com.ismayilov.ismayil.aztagram.VideoRecycler.CenterLayoutManager
import com.ismayilov.ismayil.aztagram.utils.BottomNavigationViewHelper
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import com.ismayilov.ismayil.aztagram.utils.ProfilePostRecyclerAdapter
import com.ismayilov.ismayil.aztagram.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.activity_profile.*
import org.greenrobot.eventbus.EventBus

class ProfileActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 4
    private val TAG = "Profile Activity"
    private lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mRef: DatabaseReference
    lateinit var curUser: FirebaseUser
    lateinit var allPosts: ArrayList<UserPosts>
    var userPostList: AutoPlayVideoRecyclerView? = null

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
        allPosts = ArrayList<UserPosts>()

        getUsersPosts(curUser.uid)

        ivGrid.setOnClickListener {
            setupRecyclerView(true)
        }

        ivList.setOnClickListener {
            setupRecyclerView(false)
        }

        imgDiscorvePeople.setOnClickListener {
            startActivity(Intent(this,AlgoliaSearchActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }

    }

    private fun getUserDeatlist() {
        tvEditProfile.isEnabled = false
        imgProfilSettings.isEnabled = false
        mRef.child("users").child(curUser.uid).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value != null) {
                    val snapUserDetails = p0.getValue(Users::class.java)
                    //Butun fragmentlere hazirki user haqqinda data gonderisi
                    EventBus.getDefault().postSticky(EventbusDataEvent.BrodcastUserData(snapUserDetails))
                    tvEditProfile.isEnabled = true
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
                        setupRecyclerView(false)
                    }
                })
            }
        })
    }

    private fun setupRecyclerView(isGridView: Boolean) {
        userPostList = profileRecyclerView
        if (isGridView) {
            userPostList!!.adapter = ProfilePostRecyclerAdapter(this, allPosts)
            userPostList!!.layoutManager = GridLayoutManager(this, 3)
            ivGrid.setColorFilter(ContextCompat.getColor(this@ProfileActivity, R.color.blue_instagram), PorterDuff.Mode.SRC_IN)
            ivList.setColorFilter(ContextCompat.getColor(this@ProfileActivity, R.color.black), PorterDuff.Mode.SRC_IN)
        } else {
            userPostList!!.adapter = ProfilePostRecyclerAdapter(this, allPosts)
            userPostList!!.layoutManager = CenterLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            ivGrid.setColorFilter(ContextCompat.getColor(this@ProfileActivity, R.color.black), PorterDuff.Mode.SRC_IN)
            ivList.setColorFilter(ContextCompat.getColor(this@ProfileActivity, R.color.blue_instagram), PorterDuff.Mode.SRC_IN)
        }

    }

    override fun onBackPressed() {
        allProfileLayout.visibility = View.VISIBLE
        super.onBackPressed()
    }

    private fun setupFragmentNavigation() {
        tvEditProfile.setOnClickListener {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.profileContainer, ProfileEditFragment())
                    .addToBackStack("ProfildenEdite")
                    .commit()
            allProfileLayout.visibility = View.GONE
        }
    }

    private fun setupToolbar() {
        imgProfilSettings.setOnClickListener {
            startActivity(Intent(this, ProfileSettingsActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK))
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
        if (userPostList?.handingVideoHolder != null) userPostList!!.handingVideoHolder.playVideo()
        setupNavigationView()
    }


    override fun onPause() {
        super.onPause()
        if (userPostList?.handingVideoHolder != null) userPostList!!.handingVideoHolder.stopVideo()
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

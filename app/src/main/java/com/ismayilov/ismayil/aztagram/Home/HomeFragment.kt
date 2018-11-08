package com.ismayilov.ismayil.aztagram.Home

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hoanganhtuan95ptit.autoplayvideorecyclerview.AutoPlayVideoRecyclerView
import com.ismayilov.ismayil.aztagram.Login.LoginActivity
import com.ismayilov.ismayil.aztagram.Model.Posts
import com.ismayilov.ismayil.aztagram.Model.UserPosts
import com.ismayilov.ismayil.aztagram.Model.Users
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.BottomNavigationViewHelper
import com.ismayilov.ismayil.aztagram.utils.HomeFragmentRecyclerAdapter
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {


    private val activity_no = 0

    lateinit var allPosts: ArrayList<UserPosts>
    lateinit var allMyFollowing: ArrayList<String>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var mRef: DatabaseReference
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    var recView: AutoPlayVideoRecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        setupAuthListener()
        mRef = FirebaseDatabase.getInstance().reference
        allPosts = ArrayList()
        allMyFollowing = ArrayList()

        getAllMyFollower()

        view.imgTabCamera.setOnClickListener {
            (activity as HomeActivity).homeVp.currentItem = 0
        }
        view.imgTabDm.setOnClickListener {
            (activity as HomeActivity).homeVp.currentItem = 2
        }

        return view
    }

    private fun getAllMyFollower() {
        //Timeline da ozumu gore bilim deye
        allMyFollowing.add(mUser.uid)

        mRef.child("following/${mUser.uid}").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value != null) {
                    for (ds in p0.children) {
                        allMyFollowing.add(ds.key!!)
                    }
                    getUsersPosts(mUser.uid)
                } else {
                    getUsersPosts(mUser.uid)
                }
            }

        })
    }

    private fun getUsersPosts(uid: String) {

        mRef = FirebaseDatabase.getInstance().reference
        for (i in 0 until allMyFollowing.size) {

            val allIFollow = allMyFollowing[i]

            mRef.child("users/$allIFollow").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.value != null) {
                        val userPhoto = p0.getValue(Users::class.java)!!.users_details!!.profile_picture
                        val userName = p0.getValue(Users::class.java)!!.user_name

                        mRef.child("posts/$allIFollow").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.hasChildren()) {
                                    //adamin shekilleri varsa bura yoxdursa else
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
                                if (allPosts.size > 0 && i == (allMyFollowing.size - 1)) {
                                    setupRecyclerView()
                                }
                            }
                        })
                    }
                }
            })
        }


    }

    private fun setupRecyclerView() {
        recView = view!!.homeFragmentRecyclerView
        val recAdapter = HomeFragmentRecyclerAdapter(this.activity!!, allPosts)

        recView!!.adapter = recAdapter
        recView!!.layoutManager = LinearLayoutManager(this.activity!!, LinearLayoutManager.VERTICAL, false)
    }

    private fun setupNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewActivity)
        BottomNavigationViewHelper.setupNavigation(this.activity!!, bottomNavigationViewActivity)
        val menu = bottomNavigationViewActivity.menu
        val menuItem = menu.getItem(activity_no)
        menuItem.isChecked = true
    }

    private fun setupAuthListener() {
        mAuthListener = FirebaseAuth.AuthStateListener {
            if (mUser == null) {
                startActivity(Intent(activity!!, LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK))
                activity!!.finish()
            } else {

            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onResume() {
        super.onResume()
        if (recView != null && recView?.handingVideoHolder != null) recView!!.handingVideoHolder.playVideo()
        setupNavigationView()
    }

    override fun onPause() {
        super.onPause()
        if (recView != null && recView!!.handingVideoHolder != null){
            recView!!.handingVideoHolder.stopVideo()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}
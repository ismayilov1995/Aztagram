package com.ismayilov.ismayil.aztagram.utils

import android.content.Context
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ismayilov.ismayil.aztagram.Generic.CommentFragment
import com.ismayilov.ismayil.aztagram.Model.UserPosts
import com.ismayilov.ismayil.aztagram.Profile.ProfileActivity
import com.ismayilov.ismayil.aztagram.R
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.single_post_recycler_view.view.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class ProfileListFragmentRecyclerAdapter(var mContext: Context, var allPosts: ArrayList<UserPosts>) : RecyclerView.Adapter<ProfileListFragmentRecyclerAdapter.MyViewHolder>() {

    var inflater: LayoutInflater = LayoutInflater.from(mContext)

    init {
        allPosts.sortWith(Comparator { p0, p1 ->
            if (p0!!.post_upload_date!! > p1!!.post_upload_date!!) {
                -1
            } else 1
        })
    }

    override fun getItemCount(): Int {
        return allPosts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder((inflater.inflate(R.layout.single_post_recycler_view, parent, false)), mContext)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(position, allPosts[position])

    }

    class MyViewHolder(itemView: View?, myProfileActivity: Context) : RecyclerView.ViewHolder(itemView) {
        val singleView = itemView as ConstraintLayout
        var likeCount = 0L

        val ivProf = singleView.ivPostProfPicUser
        val ivPost = singleView.ivPostPhoto
        val tvPostSharedUser = singleView.tvPostSharedUser
        val tvUsernameDesc = singleView.tvUsernameAndDescription
        val tvPostUploadAgo = singleView.tvUploadAgo
        val ivComment = singleView.ivCommentPost
        val ivLikePost = singleView.ivLikePost
        val profileActivity = myProfileActivity
        val instaLikeView = singleView.instaLikeView
        val postLikeCount = singleView.tvPostLikeCount

        fun setData(position: Int, userPosts: UserPosts) {

            tvPostSharedUser.text = userPosts.user_name
            tvPostUploadAgo.text = TimeAgo.getTimeAgo(userPosts.post_upload_date!!)
            UniversalImageLoader.setImage(userPosts.user_photo!!, ivProf, null, "")
            UniversalImageLoader.setImage(userPosts.post_url!!, ivPost, null, "")
            likeStatus(userPosts)
            val usernameAndDesription = "<strong>" + userPosts.user_name + "</strong>" +" " + userPosts.post_description

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvUsernameDesc.text = Html.fromHtml(usernameAndDesription, Html.FROM_HTML_MODE_LEGACY)
            } else {
                tvUsernameDesc.text = Html.fromHtml(usernameAndDesription)
            }

            ivComment.setOnClickListener {

                EventBus.getDefault().postSticky(EventbusDataEvent.SendPostIdWhereCommenting(userPosts.post_id))
                (profileActivity as ProfileActivity).allProfileLayout.visibility = View.GONE
                profileActivity.profileContainer.visibility = View.VISIBLE
                profileActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.profileContainer, CommentFragment())
                        .addToBackStack("commentFragment")
                        .commit()
            }

            ivLikePost.setOnClickListener {
                val mUser = FirebaseAuth.getInstance().currentUser!!.uid
                val mRef = FirebaseDatabase.getInstance().reference
                mRef.child("likes/${userPosts.post_id}").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.hasChild(mUser)) {
                            mRef.child("likes/${userPosts.post_id}/$mUser").removeValue()
                            likeStatus(userPosts)
                            //ivLikePost.setImageResource(R.drawable.ic_like)
                        } else {
                            mRef.child("likes/${userPosts.post_id}/$mUser").setValue(mUser)
                            //ivLikePost.setImageResource(R.drawable.ic_like_active)
                            likeStatus(userPosts)
                            instaLikeView.start()
                        }
                    }
                })
            }

            var firstClick = 0L
            var lastClick = 0L

            ivPost.setOnClickListener {
                firstClick = lastClick
                lastClick = System.currentTimeMillis()
                if (lastClick - firstClick < 300) {
                    instaLikeView.start()
                    //ivLikePost.setImageResource(R.drawable.ic_like_active)
                    FirebaseDatabase.getInstance().reference.child("likes/${userPosts.post_id}")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .setValue(FirebaseAuth.getInstance().currentUser!!.uid)
                    likeStatus(userPosts)
                    lastClick = 0L
                }
            }

        }

        private fun likeStatus(userPosts: UserPosts) {
            val mRef = FirebaseDatabase.getInstance().reference
            val mUser = FirebaseAuth.getInstance().currentUser!!.uid
            mRef.child("likes/${userPosts.post_id}").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChild(mUser)) {
                        ivLikePost.setImageResource(R.drawable.ic_like_active)
                    } else {
                        ivLikePost.setImageResource(R.drawable.ic_like)
                    }
                    if (p0.value != null){
                        postLikeCount.visibility = View.VISIBLE
                        likeCount = p0.childrenCount
                        postLikeCount.text = likeCount.toString() + " bəyəni"
                    } else postLikeCount.visibility = View.GONE


                }
            })
        }

    }
}
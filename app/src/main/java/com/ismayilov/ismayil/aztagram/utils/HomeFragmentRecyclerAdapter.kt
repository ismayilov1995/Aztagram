package com.ismayilov.ismayil.aztagram.utils

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hoanganhtuan95ptit.autoplayvideorecyclerview.VideoHolder
import com.ismayilov.ismayil.aztagram.Generic.CommentFragment
import com.ismayilov.ismayil.aztagram.Home.HomeActivity
import com.ismayilov.ismayil.aztagram.Model.UserPosts
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.VideoRecycler.Video
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.single_post_recycler_view.view.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class HomeFragmentRecyclerAdapter(var mContext: Context, var allPosts: ArrayList<UserPosts>) : RecyclerView.Adapter<HomeFragmentRecyclerAdapter.MyViewHolder>() {

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
        var isVideo = false
        val filePath = allPosts[position].post_url
        val fileType = filePath!!.substring(filePath.lastIndexOf("."), filePath.lastIndexOf(".")+4)
        if (fileType == ".mp4") {
            holder.videoCamIndicator.start()
            holder.videoPerdesi.visibility = View.VISIBLE
            isVideo = true
        }
        holder.setData(position, allPosts[position], isVideo)
    }

    class MyViewHolder(itemView: View?, myHomeActivity: Context) : VideoHolder(itemView) {

        var createdFileIsVideo = false
        val singleView = itemView as ConstraintLayout
        var likeCount = 0L
        val ivProf = singleView.ivPostProfPicUser
        val ivPost = singleView.ivPostPhoto
        val tvPostSharedUser = singleView.tvPostSharedUser
        val tvUsernameDesc = singleView.tvUsernameAndDescription
        val tvPostUploadAgo = singleView.tvUploadAgo
        val ivComment = singleView.ivCommentPost
        val ivLikePost = singleView.ivLikePost
        private val myHomeActivity = myHomeActivity
        val instaLikeView = singleView.instaLikeView
        val postLikeCount = singleView.tvPostLikeCount
        val tvShowComment = singleView.tvCommentCount
        val myVideo = singleView.videoViewPost
        val videoCamIndicator = singleView.cameraAnimation
        val videoPerdesi = singleView.viewV

        fun setData(position: Int, allPosts: UserPosts, video: Boolean) {
            createdFileIsVideo = video
            if (createdFileIsVideo) {
                myVideo.visibility = View.VISIBLE
                ivPost.visibility = View.GONE
                myVideo.setVideo(Video(allPosts.post_url, 0))
            } else {
                myVideo.visibility = View.GONE
                ivPost.visibility = View.VISIBLE
                UniversalImageLoader.setImage(allPosts.post_url!!, ivPost, null, "")
            }

            tvPostSharedUser.text = allPosts.user_name
            tvPostUploadAgo.text = TimeAgo.getTimeAgo(allPosts.post_upload_date!!)
            UniversalImageLoader.setImage(allPosts.user_photo!!, ivProf, null, "")
            likeStatus(allPosts)
            showAllComments(position, allPosts)
            tvUsernameDesc.text =  allPosts.user_name +" "+ allPosts.post_description

            /*
            val usernameAndDesription = "<strong>" + userPosts.user_name + "</strong>" + " " + userPosts.post_description
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvUsernameDesc.text = Html.fromHtml(usernameAndDesription, Html.FROM_HTML_MODE_LEGACY)
            } else {
                tvUsernameDesc.text = Html.fromHtml(usernameAndDesription)
            }
            */

            tvShowComment.setOnClickListener {
                goToCommentFragment(allPosts)
            }

            ivComment.setOnClickListener {
                goToCommentFragment(allPosts)
            }

            ivLikePost.setOnClickListener {
                val mUser = FirebaseAuth.getInstance().currentUser!!.uid
                val mRef = FirebaseDatabase.getInstance().reference
                mRef.child("likes/${allPosts.post_id}").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.hasChild(mUser)) {
                            mRef.child("likes/${allPosts.post_id}/$mUser").removeValue()
                            likeStatus(allPosts)
                        } else {
                            mRef.child("likes/${allPosts.post_id}/$mUser").setValue(mUser)
                            likeStatus(allPosts)
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
                    FirebaseDatabase.getInstance().reference.child("likes/${allPosts.post_id}")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .setValue(FirebaseAuth.getInstance().currentUser!!.uid)
                    likeStatus(allPosts)
                    lastClick = 0L
                }
            }

            myVideo.setOnClickListener {
                firstClick = lastClick
                lastClick = System.currentTimeMillis()
                if (lastClick - firstClick < 300) {
                    instaLikeView.start()
                    //ivLikePost.setImageResource(R.drawable.ic_like_active)
                    FirebaseDatabase.getInstance().reference.child("likes/${allPosts.post_id}")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .setValue(FirebaseAuth.getInstance().currentUser!!.uid)
                    likeStatus(allPosts)
                    lastClick = 0L
                }
            }

        }

        override fun getVideoLayout(): View? {
            if (createdFileIsVideo) {
                return myVideo
            } else {
                return null
            }
        }

        override fun playVideo() {
            if (createdFileIsVideo) {
                myVideo.play {
                    videoPerdesi.visibility = View.GONE
                    videoCamIndicator.stop()
                }
            }
        }

        override fun stopVideo() {
            if (createdFileIsVideo) {
                myVideo.stop()
                videoCamIndicator.stop()
            }
        }

        private fun goToCommentFragment(userPosts: UserPosts) {
            EventBus.getDefault().postSticky(EventbusDataEvent.SendPostIdWhereCommenting(userPosts.post_id))
            (myHomeActivity as HomeActivity).homeVp.visibility = View.GONE
            myHomeActivity.activityHomeContainer.visibility = View.VISIBLE
            myHomeActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.activityHomeContainer, CommentFragment())
                    .addToBackStack("commentFragment")
                    .commit()
        }

        private fun showAllComments(position: Int, currentUser: UserPosts) {
            FirebaseDatabase.getInstance().reference
                    .child("comments/${currentUser.post_id}").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            var commentCount = 0
                            for (ds in p0.children){
                                if (ds!!.key.toString() != currentUser.post_id){
                                    commentCount++
                                }
                            }
                            if (commentCount >= 1) {
                                tvShowComment.text = commentCount.toString() + " bütün şərhləri gör"
                                tvShowComment.visibility = View.VISIBLE
                            } else {
                                tvShowComment.visibility = View.GONE
                            }

                        }

                    })
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
                    if (p0.value != null) {
                        postLikeCount.visibility = View.VISIBLE
                        likeCount = p0.childrenCount
                        postLikeCount.text = likeCount.toString() + " bəyəni"
                    } else postLikeCount.visibility = View.GONE
                }
            })
        }

    }
}
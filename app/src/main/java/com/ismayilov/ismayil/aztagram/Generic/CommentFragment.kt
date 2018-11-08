package com.ismayilov.ismayil.aztagram.Generic


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hendraanggrian.widget.Mention
import com.hendraanggrian.widget.MentionAdapter
import com.ismayilov.ismayil.aztagram.Model.Comments
import com.ismayilov.ismayil.aztagram.Model.Users
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import com.ismayilov.ismayil.aztagram.utils.TimeAgo
import com.ismayilov.ismayil.aztagram.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.fragment_comment.*
import kotlinx.android.synthetic.main.fragment_comment.view.*
import kotlinx.android.synthetic.main.single_raw_post_comment.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CommentFragment : Fragment() {

    lateinit var postIdIsCommented: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var mRef: DatabaseReference
    lateinit var mAdapter: FirebaseRecyclerAdapter<Comments, CommentViewHolder>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comment, container, false)

        mAuth = FirebaseAuth.getInstance()
        mUser = FirebaseAuth.getInstance().currentUser!!

        setupCommentRecyclerView(view, inflater)
        setupProfilePicture()

        view.tvShareComment.setOnClickListener {
            if (!view.etComment.text.toString().isNullOrEmpty()){
                val newComment = hashMapOf<String, Any>(
                        "user_id" to mUser.uid,
                        "comment" to etComment.text.toString(),
                        "comment_likes" to "0",
                        "comment_date" to ServerValue.TIMESTAMP)

                FirebaseDatabase.getInstance().reference.child("comments").child(postIdIsCommented).push().setValue(newComment)
                etComment.setText("")
            }
        }

        view.imgBack.setOnClickListener {
            activity!!.onBackPressed()
        }

        val mentionAdapter = MentionAdapter(activity!!)

        view.etComment.setMentionTextChangedListener { view, s ->

            FirebaseDatabase.getInstance().reference.child("users").orderByChild("user_name").startAt(s).endAt(s+"\uf8ff")
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                        }
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.value != null){
                                for (i in p0.children){
                                    mentionAdapter.clear()
                                    val readUser = i.getValue(Users::class.java)
                                    val userName = readUser!!.user_name.toString()
                                    val fullName = readUser.full_name.toString()
                                    val photo = readUser.users_details!!.profile_picture
                                    val profPic = if (!photo.isNullOrEmpty()) photo else ""
                                    mentionAdapter.add(Mention(userName,fullName,profPic))
                                }
                            }
                        }

                    })
        }
        view.etComment.mentionAdapter = mentionAdapter

        return view
    }

    private fun setupCommentRecyclerView(view: View?, inflater: LayoutInflater) {
        mRef = FirebaseDatabase.getInstance().reference.child("comments").child(postIdIsCommented)
        val options = FirebaseRecyclerOptions.Builder<Comments>()
                .setQuery(mRef, Comments::class.java)
                .build()

        mAdapter = object : FirebaseRecyclerAdapter<Comments, CommentViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
                val commentViewHolder = inflater.inflate(R.layout.single_raw_post_comment, parent, false)
                return CommentViewHolder(commentViewHolder)
            }

            override fun onBindViewHolder(holder: CommentViewHolder, position: Int, model: Comments) {
                holder.setData(model)

                //ilk comment shekli paylasana aiddirse like gosterilmir
                if (position == 0 && postIdIsCommented == getRef(0).key) {
                    holder.commentLike.visibility = View.INVISIBLE
                    holder.commentViewLine.visibility = View.VISIBLE
                }

                holder.setLikeComment(postIdIsCommented, getRef(position).key)

            }
        }
        view!!.commentRecView.adapter = mAdapter
        view.commentRecView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    private fun setupProfilePicture() {

        mRef = FirebaseDatabase.getInstance().reference.child("users")
        mRef.child(mUser.uid).child("users_details")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val profUrl = p0.child("profile_picture").value.toString()
                        UniversalImageLoader.setImage(profUrl, ivCommentWriterProfile, null, "")
                    }
                })

    }

    class CommentViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val singleLayout = itemView as ConstraintLayout
        val commentedUserPic = singleLayout.ivCommentWriteUser
        val usernameAdnComment = singleLayout.tvUsernameAndComment
        val commentLike = singleLayout.ivLikeComment
        val dateComment = singleLayout.tvCommentDate
        val likeCount = singleLayout.tvLikeCount
        val commentViewLine = singleLayout.viewComment

        fun setData(model: Comments) {
            dateComment.text = TimeAgo.getTimeAgoForComments(model.comment_date!!)
            getUsersData(model.user_id, model.comment)

        }

        private fun getUsersData(user_id: String?, comment: String?) {
            val mRef = FirebaseDatabase.getInstance().reference
            mRef.child("users/$user_id").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val usernameAndComment = "<strong>" + p0.getValue(Users::class.java)!!.user_name + "</strong>" + "  " + comment
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        usernameAdnComment.text = Html.fromHtml(usernameAndComment, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        usernameAdnComment.text = Html.fromHtml(usernameAndComment)
                    }

                    UniversalImageLoader.setImage(p0.getValue(Users::class.java)!!.users_details!!.profile_picture!!, commentedUserPic, null, "")
                }
            })
        }

        fun setLikeComment(postIdIsCommented: String, likedPostId: String?) {
            val curUser = FirebaseAuth.getInstance().currentUser!!.uid
            val mRef = FirebaseDatabase.getInstance().reference
                    .child("comments/$postIdIsCommented/$likedPostId")
            var currentUserIsLiked = false

            //bu senin like edib etmediyini yoxlayir
            mRef.child("liked").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()){
                        val likes = p0.childrenCount.toString()  + " nəfər bəyəndi"
                        likeCount.visibility = View.VISIBLE
                        likeCount.text = likes
                    }else likeCount.visibility = View.INVISIBLE
                    currentUserIsLiked = if (p0.hasChild(curUser)) {
                        commentLike.setImageResource(R.drawable.ic_like_active)
                        true
                    } else {
                        commentLike.setImageResource(R.drawable.ic_like)
                        false
                    }
                }
            })

            commentLike.setOnClickListener {
                if (currentUserIsLiked) {
                    mRef.child("liked").child(curUser).removeValue()
                    commentLike.setImageResource(R.drawable.ic_like_active)
                } else {
                    mRef.child("liked").child(curUser).setValue(curUser)
                    commentLike.setImageResource(R.drawable.ic_like)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

    @Subscribe(sticky = true)
    internal fun onChoosenPost(post: EventbusDataEvent.SendPostIdWhereCommenting) {
        postIdIsCommented = post.postId!!
    }

    override fun onAttach(context: Context?) {
        EventBus.getDefault().register(this)
        super.onAttach(context)
    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
        super.onDetach()
    }

}

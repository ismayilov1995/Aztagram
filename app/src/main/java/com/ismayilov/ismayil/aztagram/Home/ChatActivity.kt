package com.ismayilov.ismayil.aztagram.Home

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ismayilov.ismayil.aztagram.Login.LoginActivity
import com.ismayilov.ismayil.aztagram.Model.Messages
import com.ismayilov.ismayil.aztagram.Model.Users
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.MessagesRecyclerViewAdapter
import com.ismayilov.ismayil.aztagram.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    lateinit var chattedId:String
    lateinit var currentUser:String
    var allMessages: ArrayList<Messages> = ArrayList()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mRef: DatabaseReference
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var myRecyclerViewAdapter:MessagesRecyclerViewAdapter
    lateinit var myMessagesRecyclerView:RecyclerView
    var chattedUsersData:Users? = null
    lateinit var childEventListener:ChildEventListener
    var per_message_each_page = 5
    var page_number = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference
        setupAuthListener()
        //myMessagesRecyclerView = messagesRecyclerView
        currentUser = mAuth.currentUser!!.uid
        if(intent.extras.get("userID") != null){
            val id=intent.extras.get("userID").toString()
            chattedId=id
        }
        getChattedUsersData(chattedId)



        refreshLayout.setOnRefreshListener {
            page_number++
            allMessages.clear()
            mRef.child("messages/$currentUser/$chattedId").removeEventListener(childEventListener)
            getAllMessages()
            refreshLayout.setRefreshing(false)
        }


        tvSendMessage.setOnClickListener {
            val message = etMesaj.text.toString()
            val messageSender = HashMap<String,Any>()
            messageSender["message"] = message
            messageSender["seen"] = true
            messageSender["time"] = ServerValue.TIMESTAMP
            messageSender["type"] = "text"
            messageSender["sender_id"] = currentUser

            val newMessageKey = mRef.child("messages/$currentUser/$chattedId").push().key
            mRef.child("messages/$currentUser/$chattedId/$newMessageKey").setValue(messageSender)

            val messageReciver = HashMap<String,Any>()
            messageReciver["message"] = message
            messageReciver["seen"] = false
            messageReciver["time"] = ServerValue.TIMESTAMP
            messageReciver["type"] = "text"
            messageReciver["sender_id"] = currentUser
            mRef.child("messages/$chattedId/$currentUser/$newMessageKey").setValue(messageReciver)

            val chatMessageSender = HashMap<String,Any>()
            chatMessageSender["time"] = ServerValue.TIMESTAMP
            chatMessageSender["seen"] = true
            chatMessageSender["last_message"] = message

            mRef.child("chats/$currentUser/$chattedId").setValue(chatMessageSender)

            val chatMessageReciver = HashMap<String,Any>()
            chatMessageReciver["time"] = ServerValue.TIMESTAMP
            chatMessageReciver["seen"] = false
            chatMessageReciver["last_message"] = message

            mRef.child("chats/$chattedId/$currentUser").setValue(chatMessageReciver)

            etMesaj.setText("")
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }


    }

    private fun getAllMessages() {
        childEventListener = mRef.child("messages/$currentUser/$chattedId").limitToLast(page_number * per_message_each_page).addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val curentMessage = p0.getValue(Messages::class.java)
                allMessages.add(curentMessage!!)
                myRecyclerViewAdapter.notifyItemInserted(allMessages.size-1)
                myMessagesRecyclerView.scrollToPosition(allMessages.size-1)
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })


    }

    private fun setupMesaagesRecyclerView() {
        val myLayoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        myLayoutManager.stackFromEnd = true
        myMessagesRecyclerView = messagesRecyclerView
        myRecyclerViewAdapter = MessagesRecyclerViewAdapter(allMessages,this,chattedUsersData!!)
        messagesRecyclerView.layoutManager = myLayoutManager
        messagesRecyclerView.adapter = myRecyclerViewAdapter

        getAllMessages()

    }

    private fun getChattedUsersData(chattedId: String) {
        mRef.child("users/$chattedId").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                chattedUsersData = p0.getValue(Users::class.java)
                tvUserName.text = chattedUsersData!!.user_name.toString()
                val imgUrl = chattedUsersData!!.users_details!!.profile_picture
                UniversalImageLoader.setImage(imgUrl!!,ivChatUserProfile,null,"")
                setupMesaagesRecyclerView()

            }

        })

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

}

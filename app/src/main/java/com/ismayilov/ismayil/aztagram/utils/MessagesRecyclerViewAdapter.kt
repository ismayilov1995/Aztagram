package com.ismayilov.ismayil.aztagram.utils

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.ismayilov.ismayil.aztagram.Model.Messages
import com.ismayilov.ismayil.aztagram.Model.Users
import com.ismayilov.ismayil.aztagram.R
import kotlinx.android.synthetic.main.single_raw_message_sender.view.*

class MessagesRecyclerViewAdapter(var allMessages: ArrayList<Messages>, var mContex: Context,var chattedUsersData: Users?):RecyclerView.Adapter<MessagesRecyclerViewAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesRecyclerViewAdapter.MyViewHolder {
        var myView:View? = null
        if (viewType == 1){
            myView = LayoutInflater.from(mContex).inflate(R.layout.single_raw_message_sender,parent,false)
            return MyViewHolder(myView,null)
        }else{
            myView = LayoutInflater.from(mContex).inflate(R.layout.single_raw_message_reciver,parent,false)
            return MyViewHolder(myView,chattedUsersData)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (allMessages[position].sender_id!! == FirebaseAuth.getInstance().currentUser!!.uid){
            1
        }else 2

    }

    override fun getItemCount(): Int {
        return allMessages.size
    }

    override fun onBindViewHolder(holder: MessagesRecyclerViewAdapter.MyViewHolder, position: Int) {
        holder.setData(allMessages[position],position)
    }

    class MyViewHolder(itemView: View?,var chattedUsersData: Users?) : RecyclerView.ViewHolder(itemView) {
        val allLayout = itemView as ConstraintLayout
        val mesajText = allLayout.tvMesaj
        val mesajSeen = allLayout.ivSenn

        fun setData(messages: Messages, position: Int) {
            mesajText.text = messages.message
            if (chattedUsersData != null){
                mesajSeen.visibility = View.VISIBLE
                UniversalImageLoader.setImage(chattedUsersData!!.users_details!!.profile_picture!!,mesajSeen,null,"")
            }

        }


    }
}
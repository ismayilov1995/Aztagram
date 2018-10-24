package com.ismayilov.ismayil.aztagram.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismayilov.ismayil.aztagram.R
import kotlinx.android.synthetic.main.single_raw_gridview.view.*
import org.greenrobot.eventbus.EventBus

class ShareGalleryRecyclerAdapter(var filesInDirectory:ArrayList<String>, var mContex:Context) : RecyclerView.Adapter<ShareGalleryRecyclerAdapter.MyViewHolder>() {

    lateinit var inflater:LayoutInflater

    init {
        inflater = LayoutInflater.from(mContex)
    }

    override fun getItemCount(): Int {
        return filesInDirectory.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(inflater.inflate(R.layout.single_raw_gridview,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val filePath = filesInDirectory[position]
        val fileType = filePath.substring(filePath.lastIndexOf("."))

        if (fileType == ".mp4"){
            holder.videoDuration.visibility = View.VISIBLE
            val retriver = MediaMetadataRetriever()
            retriver.setDataSource(mContex, Uri.parse("file://$filePath"))
            val videoTime = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val videoTimeLong = videoTime.toLong()
            holder.videoDuration.text = convertDuration(videoTimeLong)
            UniversalImageLoader.setImage(filePath, holder.fileImage, holder.progressBar, "file:/")
        }else{
            holder.videoDuration.visibility = View.GONE
            UniversalImageLoader.setImage(filePath, holder.fileImage, holder.progressBar, "file:/")
        }
        holder.singleRawFile.setOnClickListener {
            EventBus.getDefault().post(EventbusDataEvent.SendChoosenFilePath(filePath))
        }

    }

    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val singleRawFile = itemView as ConstraintLayout
        val fileImage = singleRawFile.ivSingleGridImageView
        val videoDuration = singleRawFile.tvTime
        val progressBar = singleRawFile.pbLoadingShareImg


    }

    fun convertDuration(duration:Long):String{
        val second = duration / 1000 % 60
        val minute = duration / (1000 * 60) % 60
        val hour = duration / (1000 * 60 * 60) % 24
        var time = ""
        if (hour > 0){
            time = String.format("%02d:%02d:%02d",hour,minute,second)
        }else{
            time = String.format("%02d:%02d",minute,second)
        }
        return time
    }
}
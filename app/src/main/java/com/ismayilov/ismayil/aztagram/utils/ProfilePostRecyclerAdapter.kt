package com.ismayilov.ismayil.aztagram.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismayilov.ismayil.aztagram.Model.UserPosts
import com.ismayilov.ismayil.aztagram.R
import kotlinx.android.synthetic.main.single_view_profile_recycler.view.*


class ProfilePostRecyclerAdapter(var userPosts: ArrayList<UserPosts>, mContex: Context) : RecyclerView.Adapter<ProfilePostRecyclerAdapter.MyViewHolder>() {

    var inflater: LayoutInflater = LayoutInflater.from(mContex)

    override fun getItemCount(): Int {
        return userPosts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(inflater.inflate(R.layout.single_view_profile_recycler, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val filePath = userPosts[position].post_url
        val indexWithDot = filePath!!.lastIndexOf(".")
        val fileType = filePath!!.substring(indexWithDot,indexWithDot+4)

        if (fileType == ".mp4"){
            holder.videoIcon.visibility = View.VISIBLE
            holder.fileImage.setImageBitmap(getThumbFromVideo(filePath))
            holder.progressBar.visibility = View.GONE
        }else{
            holder.progressBar.visibility = View.VISIBLE
            holder.videoIcon.visibility = View.GONE
            UniversalImageLoader.setImage(filePath, holder.fileImage, holder.progressBar, "")
        }


    }

    @Throws(Throwable::class)
    fun getThumbFromVideo(videoPath: String): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, HashMap())
            else
                mediaMetadataRetriever.setDataSource(videoPath)

            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }


    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val singleRawFile = itemView as ConstraintLayout
        val fileImage = singleRawFile.ivSingleGridImageView
        val videoIcon = singleRawFile.ivVideo
        val progressBar = singleRawFile.pbLoadingShareImg

    }


}
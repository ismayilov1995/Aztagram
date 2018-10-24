package com.ismayilov.ismayil.aztagram.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import com.ismayilov.ismayil.aztagram.R
import kotlinx.android.synthetic.main.single_raw_gridview.view.*

class ShareActivityGridViewAdapter(mContext: Context?, resource: Int, var filesInFolder: ArrayList<String>) : ArrayAdapter<String>(mContext, resource, filesInFolder) {

    var inflater: LayoutInflater
    var singleRawImg: View? = null
    lateinit var viewHolder: ViewHolder

    init {
        inflater = LayoutInflater.from(mContext)
    }

    inner class ViewHolder() {
        lateinit var imageView: GridImageView
        lateinit var progressBar: ProgressBar
        lateinit var tvTime: TextView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        singleRawImg = convertView

        if (singleRawImg == null) {
            singleRawImg = inflater.inflate(R.layout.single_raw_gridview, parent, false)
            viewHolder = ViewHolder()
            viewHolder.imageView = singleRawImg!!.ivSingleGridImageView
            viewHolder.progressBar = singleRawImg!!.pbLoadingShareImg
            viewHolder.tvTime = singleRawImg!!.tvTime
            singleRawImg!!.tag = viewHolder
        } else {
            viewHolder = singleRawImg!!.tag as ViewHolder
        }
        var filePath = filesInFolder[position]
        val fileType = filePath.substring(filePath.lastIndexOf("."))
        if (fileType == ".mp4"){
            viewHolder.tvTime.visibility = View.VISIBLE
            var retriver = MediaMetadataRetriever()
            retriver.setDataSource(context,Uri.parse("file://$filePath"))
            var videoTime = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val videoTimeLong = videoTime.toLong()
            viewHolder.tvTime.text = convertDuration(videoTimeLong)
            UniversalImageLoader.setImage(filesInFolder[position], viewHolder.imageView, viewHolder.progressBar, "file:/")
        }else{
            viewHolder.tvTime.visibility = View.GONE
            UniversalImageLoader.setImage(filesInFolder[position], viewHolder.imageView, viewHolder.progressBar, "file:/")
        }

        return singleRawImg!!
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
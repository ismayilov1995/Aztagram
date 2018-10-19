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

    fun convertDuration(duration: Long): String {
        var out: String? = null
        var hours: Long = 0
        try {
            hours = duration / 3600000
        } catch (e: Exception) {
            e.printStackTrace()
            return out!!
        }
        val remaining_minutes = (duration - hours * 3600000) / 60000
        var minutes = remaining_minutes.toString()
        if (minutes == "0") {
            minutes = "00"
        }
        val remaining_seconds = duration - hours * 3600000 - remaining_minutes * 60000
        var seconds = remaining_seconds.toString()
        if (seconds.length < 2) {
            seconds = "00"
        } else {
            seconds = seconds.substring(0, 2)
        }
        if (hours > 0) {
            out = hours.toString() + ":" + minutes + ":" + seconds
        } else {
            out = "$minutes:$seconds"
        }
        return out
    }

}
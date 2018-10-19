package com.ismayilov.ismayil.aztagram.Share

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismayilov.ismayil.aztagram.R
import com.otaliastudios.cameraview.CameraView
import kotlinx.android.synthetic.main.fragment_share_video.view.*


class ShareVideoFragment : Fragment() {

    lateinit var videoView: CameraView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_share_video, container, false)
        videoView = view.videoView


        return view
    }

    override fun onResume() {
        super.onResume()
        videoView.start()
    }

    override fun onPause() {
        super.onPause()
        videoView.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.destroy()
    }
}

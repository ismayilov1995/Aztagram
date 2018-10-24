package com.ismayilov.ismayil.aztagram.Share

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.Gesture
import com.otaliastudios.cameraview.GestureAction
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_video.view.*
import org.greenrobot.eventbus.EventBus
import java.io.File


class ShareVideoFragment : Fragment() {

    lateinit var videoView: CameraView
    var fragmentIsCreated = false
    var handler = Handler()
    var runnable = Runnable {}
    var cnt = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_share_video, container, false)
        val createdVideoFileName = System.currentTimeMillis()
        val createdVideoFilePath = File(Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Aztagram/$createdVideoFileName.mp4")
        val createdVideoFolder = File(Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Aztagram")

        videoView = view.videoView
        videoView.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        videoView.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)

        val t: CountDownTimer
        t = object : CountDownTimer(java.lang.Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                cnt += 1
                val millis: Long = cnt.toLong()
                view.tvTimer.text = String.format("%02d", millis)
            }

            override fun onFinish() {}
        }

        view.ivRecVideo.setOnTouchListener(View.OnTouchListener { p0, p1 ->
            if (p1!!.action == MotionEvent.ACTION_DOWN) {
                if (createdVideoFolder.isDirectory || createdVideoFolder.mkdirs()){
                    t.start()
                    view.ivRecVideo.setImageResource(R.drawable.rec_video)
                    videoView.startCapturingVideo(createdVideoFilePath)
                    return@OnTouchListener true
                }
            } else if (p1!!.action == MotionEvent.ACTION_UP) {
                t.cancel()
                Toast.makeText(activity!!, "Video yaddasa yazildi", Toast.LENGTH_SHORT).show()
                view.ivRecVideo.setImageResource(R.drawable.shoot_photo)
                videoView.stopCapturingVideo()
                return@OnTouchListener true
            }
            false
        })

        videoView.addCameraListener(object : CameraListener() {
            override fun onVideoTaken(video: File?) {
                super.onVideoTaken(video)
                activity!!.shareRootContainer.visibility = View.GONE
                activity!!.shareFragmentContainer.visibility = View.VISIBLE
                val transaction = activity!!.supportFragmentManager.beginTransaction()
                EventBus.getDefault().postSticky(EventbusDataEvent.SharedImagePath(video!!.path, false))
                transaction.replace(R.id.shareFragmentContainer, ShareNextFragment())
                        .addToBackStack("Fragment Next")
                        .commit()
                view.tvTimer.text = "00"

            }
        })

        view.imgBack.setOnClickListener {
            activity!!.onBackPressed()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        fragmentIsCreated = true
        videoView.start()
    }

    override fun onPause() {
        super.onPause()
        videoView.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fragmentIsCreated) {
            Log.e("HATA", "VIDEO DESTROYED ISHE DUSHDU")
            videoView.destroy()
        }
    }
}

package com.ismayilov.ismayil.aztagram.Share


import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismayilov.ismayil.aztagram.R
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.Gesture
import com.otaliastudios.cameraview.GestureAction
import kotlinx.android.synthetic.main.fragment_share_camera.view.*
import java.io.File
import java.io.FileOutputStream

class ShareCameraFragment : Fragment() {

    lateinit var cameraView: CameraView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_share_camera, container, false)
        cameraView = view.cameraView
        cameraView.mapGesture(Gesture.PINCH,GestureAction.ZOOM)
        cameraView.mapGesture(Gesture.TAP,GestureAction.FOCUS_WITH_MARKER)

        view.ivShootPhoto.setOnClickListener {
            cameraView.capturePicture()
        }

        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                super.onPictureTaken(jpeg)
                val shootedPhotoName = System.currentTimeMillis()
                val shootedPhoto = File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/Aztagram/$shootedPhotoName.jpg")
                val createFile = FileOutputStream(shootedPhoto)
                createFile.write(jpeg)
                createFile.close()

            }
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        cameraView.start()
    }

    override fun onPause() {
        super.onPause()
        cameraView.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraView.destroy()
    }

}

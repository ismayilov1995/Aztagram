package com.ismayilov.ismayil.aztagram.Home

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.Gesture
import com.otaliastudios.cameraview.GestureAction
import kotlinx.android.synthetic.main.fragment_camera.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CameraFragment : Fragment() {

    private var mCamera: CameraView? = null
    private var isCameraPermissonGranted = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        mCamera=view!!.cameraView
        mCamera!!.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        mCamera!!.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)

        return view
    }



    override fun onResume() {
        super.onResume()
        Log.e("HATA","Camera Fragmentt onResume ishleyir")
        if (isCameraPermissonGranted == true){
            mCamera!!.start()
            Log.e("HATA","Camera Fragmentt start oldu")
        }
    }

    override fun onPause() {
        super.onPause()
        mCamera!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mCamera != null)
            mCamera!!.destroy()
    }

    @Subscribe(sticky = true)
    internal fun onChoosenImgPath(allow: EventbusDataEvent.SharedImagePath) {
        isCameraPermissonGranted = allow.fileTypeIsImage!!
        Log.e("HATA","Camera Fragmentt Event qebul olundu")
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
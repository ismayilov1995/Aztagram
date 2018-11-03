package com.ismayilov.ismayil.aztagram.Home

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.Share.ShareNextFragment
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import com.otaliastudios.cameraview.*
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_camera.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.io.FileOutputStream

class CameraFragment : Fragment() {

    private var mCamera: CameraView? = null
    private var isCameraPermissonGranted = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        mCamera=view!!.cameraViewHome
        mCamera!!.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        mCamera!!.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)
        cameraSettings(view, mCamera)

        view.ivShootPhoto.setOnClickListener {
            mCamera!!.capturePicture()
        }

        mCamera!!.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                super.onPictureTaken(jpeg)
                val folderOfNewCreatedFiles = File(Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Aztagram/")
                var shootedPhoto = File("")
                if (folderOfNewCreatedFiles.isDirectory || folderOfNewCreatedFiles.mkdirs()) {
                    val shootedPhotoName = System.currentTimeMillis()
                    shootedPhoto = File(Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Aztagram/$shootedPhotoName.jpg")
                    val createFile = FileOutputStream(shootedPhoto)
                    createFile.write(jpeg)
                    createFile.close()
                }
                activity!!.cameraFragmentRoot.visibility = View.GONE
                activity!!.cameraFragmentFrame.visibility = View.VISIBLE
                val transaction = activity!!.supportFragmentManager.beginTransaction()
                EventBus.getDefault().postSticky(EventbusDataEvent.SharedImagePath(shootedPhoto.path, true))
                transaction.replace(R.id.cameraFragmentFrame, ShareNextFragment())
                        .addToBackStack("Fragment Next")
                        .commit()
            }
        })


        return view
    }


    private fun cameraSettings(view: View?, mCamera: CameraView?) {
        var flashIsOff = true
        var cameraFaceOnFront = true
        view!!.ivFlashStatus.setOnClickListener {
            if (flashIsOff) {
                mCamera!!.flash = Flash.ON
                view.ivFlashStatus.setImageResource(R.drawable.ic_flash_on)
                flashIsOff = false
            } else {
                mCamera!!.flash = Flash.OFF
                view.ivFlashStatus.setImageResource(R.drawable.ic_flash_of)
                flashIsOff = true
            }
        }
        view.ivSplitCamera.setOnClickListener {
            if (cameraFaceOnFront) {
                mCamera!!.facing = Facing.BACK
                view.ivSplitCamera.setImageResource(R.drawable.ic_camera_front)
                cameraFaceOnFront = false
            } else {
                mCamera!!.facing = Facing.FRONT
                view.ivSplitCamera.setImageResource(R.drawable.ic_camera_back)
                cameraFaceOnFront = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isCameraPermissonGranted){
            mCamera!!.start()
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
    internal fun onChoosenImgPath(allow: EventbusDataEvent.SendCameraRequestPermission) {
        isCameraPermissonGranted = allow.canUseCamera!!
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
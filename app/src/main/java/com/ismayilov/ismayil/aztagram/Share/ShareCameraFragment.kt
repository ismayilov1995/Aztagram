package com.ismayilov.ismayil.aztagram.Share


import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import com.otaliastudios.cameraview.*
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_camera.view.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileOutputStream

class ShareCameraFragment : Fragment() {

    lateinit var cameraView: CameraView
    var fragmentIsCreated = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_share_camera, container, false)
        cameraView = view.cameraView
        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)
        cameraSettings(view, cameraView)

        view.ivShootPhoto.setOnClickListener {
            cameraView.capturePicture()
        }

        cameraView.addCameraListener(object : CameraListener() {
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
                activity!!.shareRootContainer.visibility = View.GONE
                activity!!.shareFragmentContainer.visibility = View.VISIBLE
                val transaction = activity!!.supportFragmentManager.beginTransaction()
                EventBus.getDefault().postSticky(EventbusDataEvent.SharedImagePath(shootedPhoto.path, true))
                transaction.replace(R.id.shareFragmentContainer, ShareNextFragment())
                        .addToBackStack("Fragment Next")
                        .commit()
            }
        })

        view.imgBack.setOnClickListener {
            activity!!.onBackPressed()
        }

        return view
    }

    private fun cameraSettings(view: View?, cameraView: CameraView?) {
        var flashIsOff = true
        var cameraFaceOnFront = true
        view!!.ivFlashStatus.setOnClickListener {
            if (flashIsOff) {
                cameraView!!.flash = Flash.ON
                view.ivFlashStatus.setImageResource(R.drawable.ic_flash_on)
                flashIsOff = false
            } else {
                cameraView!!.flash = Flash.OFF
                view.ivFlashStatus.setImageResource(R.drawable.ic_flash_of)
                flashIsOff = true
            }
        }
        view!!.ivSplitCamera.setOnClickListener {
            if (cameraFaceOnFront) {
                cameraView!!.facing = Facing.BACK
                view.ivSplitCamera.setImageResource(R.drawable.ic_camera_front)
                cameraFaceOnFront = false
            } else {
                cameraView!!.facing = Facing.FRONT
                view.ivSplitCamera.setImageResource(R.drawable.ic_camera_back)
                cameraFaceOnFront = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fragmentIsCreated = true
        Log.e("HATA", "KAMERA onResume ISHE DUSHDU")
        cameraView.start()
    }

    override fun onPause() {
        super.onPause()
        Log.e("HATA", "KAMERA onPause ISHE DUSHDU")
        cameraView.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fragmentIsCreated) {
            Log.e("HATA", "KAMERA onDestroy ISHE DUSHDU")
            cameraView.destroy()
        }
    }

}

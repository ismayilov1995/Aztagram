package com.ismayilov.ismayil.aztagram.Share

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.DocumentsProsess
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import com.ismayilov.ismayil.aztagram.utils.ShareGalleryRecyclerAdapter
import com.ismayilov.ismayil.aztagram.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_gallery.*
import kotlinx.android.synthetic.main.fragment_share_gallery.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File


class ShareGalleryFragment : Fragment() {

    var choosenFilePath: String? = null
    var fileTypeIsImage: Boolean? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_share_gallery, container, false)

        val foldersPaths = ArrayList<String>()
        val foldersNames = ArrayList<String>()

        val cameraImages = getDirPath("/DCIM/Camera")
        val downloadImages = getDirPath("/Download")
        val whatsappImages = getDirPath("/WhatsApp/Media/WhatsApp Images")
        val aztagramDir = getDirPath("/DCIM/Aztagram")
        val pictures = getDirPath("/Pictures")

        if (cameraImages.isDirectory){
            foldersPaths.add(cameraImages.absolutePath)
            foldersNames.add("Kamera")
        }
        if (downloadImages.isDirectory){
            foldersPaths.add(downloadImages.absolutePath)
            foldersNames.add("Yuklenenler")
        }
        if (whatsappImages.isDirectory){
            foldersPaths.add(whatsappImages.absolutePath)
            foldersNames.add("WhatsApp")
        }
        if (aztagramDir.isDirectory){
            foldersPaths.add(aztagramDir.absolutePath)
            foldersNames.add("Aztagram")
        }
        if (pictures.isDirectory){
            foldersPaths.add(pictures.absolutePath)
            foldersNames.add(pictures.name)
        }

        val spinnerArrayAdapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, foldersNames)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        view.spnFolderName.adapter = spinnerArrayAdapter
        view.spnFolderName.setSelection(0)
        view.spnFolderName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                setupRecyclerView(DocumentsProsess.getDocumentsFromFolder(foldersPaths[p2]))
            }
        }

        view.tvIleri.setOnClickListener {
            activity!!.shareRootContainer.visibility = View.GONE
            activity!!.shareFragmentContainer.visibility = View.VISIBLE
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            EventBus.getDefault().postSticky(EventbusDataEvent.SharedImagePath(choosenFilePath, fileTypeIsImage))
            view.videoView.stopPlayback()
            transaction.replace(R.id.shareFragmentContainer, ShareNextFragment())
                    .addToBackStack("Fragment Next")
                    .commit()
        }

        view.imgBack.setOnClickListener {
            activity!!.onBackPressed()
        }


        return view
    }

    fun getDirPath(dirPath: String): File {
        return File(Environment.getExternalStorageDirectory().absolutePath + dirPath)
    }

    @Subscribe
    internal fun onChoosenImgPath(choosenImage: EventbusDataEvent.SendChoosenFilePath) {
        choosenFilePath = choosenImage.filePath!!
        showPicOrVidew(choosenFilePath!!)
    }

    private fun setupRecyclerView(documentsFromFolder: ArrayList<String>) {
        val recViewAdaptor = ShareGalleryRecyclerAdapter(documentsFromFolder, this.activity!!)
        recViewFiles.adapter = recViewAdaptor
        val layoutManager = GridLayoutManager(this.activity!!, 4)
        recViewFiles.layoutManager = layoutManager
        recViewFiles.setHasFixedSize(true)
        recViewFiles.setItemViewCacheSize(30)
        recViewFiles.isDrawingCacheEnabled = true
        recViewFiles.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_LOW
        //fragment ilk acilan kimi qoyulan shekiller
        choosenFilePath = documentsFromFolder[0]
        showPicOrVidew(choosenFilePath!!)
    }

    private fun showPicOrVidew(filePath: String) {
        if (filePath.contains(".")) {
            val fileType = filePath.substring(filePath.lastIndexOf("."))
            if (fileType != null) {
                if (fileType == ".mp4") {
                    fileTypeIsImage = false
                    videoView.visibility = View.VISIBLE
                    imagecropView.visibility = View.GONE
                    videoView.setVideoURI(Uri.parse("file://$filePath"))
                    videoView.start()
                } else {
                    fileTypeIsImage = true
                    videoView.visibility = View.GONE
                    imagecropView.visibility = View.VISIBLE
                    UniversalImageLoader.setImage(filePath, imagecropView, null, "file:/")
                }
            }
        }
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

package com.ismayilov.ismayil.aztagram.Share


import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.DocumentsProsess
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import com.ismayilov.ismayil.aztagram.utils.ShareActivityGridViewAdapter
import com.ismayilov.ismayil.aztagram.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_gallery.*
import kotlinx.android.synthetic.main.fragment_share_gallery.view.*
import org.greenrobot.eventbus.EventBus


class ShareGalleryFragment : Fragment() {

    var choosenImagePath:String? = null
    var fileTypeIsImage:Boolean? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_share_gallery, container, false)

        val foldersPaths = ArrayList<String>()
        val foldersNames = ArrayList<String>()
        val root = Environment.getExternalStorageDirectory().path
        val cameraImages = "$root/DCIM/Camera"
        val downloadImages = "$root/Download"
        val whatsappImages = "$root/WhatsApp/Media/WhatsApp Images"

        foldersPaths.add(cameraImages)
        foldersPaths.add(downloadImages)
        foldersPaths.add(whatsappImages)

        foldersNames.add("Kamera")
        foldersNames.add("Yuklenenler")
        foldersNames.add("WhatsApp")

        val spinnerArrayAdapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, foldersNames)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        view.spnFolderName.adapter = spinnerArrayAdapter
        view.spnFolderName.setSelection(0)
        view.spnFolderName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                setupGridView(DocumentsProsess.getDocumentsFromFolder(foldersPaths[p2]))
            }
        }

        view.tvIleri.setOnClickListener {
            activity!!.shareRootContainer.visibility = View.GONE
            activity!!.shareFragmentContainer.visibility = View.VISIBLE
            val transaction = activity!!.supportFragmentManager.beginTransaction()

            EventBus.getDefault().postSticky(EventbusDataEvent.SharedImagePath(choosenImagePath,fileTypeIsImage))
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

    fun setupGridView(filesInChoosenFolder: ArrayList<String>) {
        val gridAdapter = ShareActivityGridViewAdapter(activity, R.layout.single_raw_gridview, filesInChoosenFolder)
        gridImages.adapter = gridAdapter
        choosenImagePath = filesInChoosenFolder[0]
        showPicOrVidew(filesInChoosenFolder[0])

        gridImages.setOnItemClickListener { adapterView, gView, i, l ->
            choosenImagePath = filesInChoosenFolder[i]
            showPicOrVidew(filesInChoosenFolder[i])
        }
    }

    private fun showPicOrVidew(filePath: String) {
        if (filePath.contains(".")){
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

}

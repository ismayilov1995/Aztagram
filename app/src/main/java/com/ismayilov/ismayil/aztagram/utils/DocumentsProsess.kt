package com.ismayilov.ismayil.aztagram.utils

import android.os.AsyncTask
import android.os.Environment
import android.support.v4.app.Fragment
import com.iceteck.silicompressorr.SiliCompressor
import com.ismayilov.ismayil.aztagram.Profile.ProfilePhotoUploadingFragment
import com.ismayilov.ismayil.aztagram.Share.ShareNextFragment
import java.io.File
import java.util.*

class DocumentsProsess {
    companion object {
        fun getDocumentsFromFolder(folderName: String): ArrayList<String> {

            val allDocuments = ArrayList<String>()

            val file = File(folderName)

            val allFilesInFolder = file.listFiles()

            if (allFilesInFolder != null) {

                //shekilleri axirdan evvele gore duzulmesi
                if (allFilesInFolder.size > 1) {
                    Arrays.sort(allFilesInFolder) { p0, p1 ->
                        if (p0!!.lastModified() > p1!!.lastModified()) {
                            -1
                        } else 1
                    }
                }

                for (i in 0 until allFilesInFolder.size) {
                    //sadece fayllari axtaririq
                    if (allFilesInFolder[i].isFile) {
                        val pathCurrentFile = allFilesInFolder[i].absolutePath

                        if (pathCurrentFile.contains(".")){
                            val fileType = pathCurrentFile.substring(pathCurrentFile.lastIndexOf("."))
                            if (fileType == ".jpg" || fileType == ".jpeg" || fileType == ".png" || fileType == ".mp4")
                                allDocuments.add(pathCurrentFile)
                        }
                    }
                }
            }
            return allDocuments
        }

        fun compressImage(fragment: Fragment, choosenImagePath: String?) {
            ImageCompressAsyncTask(fragment).execute(choosenImagePath)
        }

        fun compressVideo(fragment: Fragment, choosenVideoPath: String?) {
            VideoCompressAsyncTask(fragment).execute(choosenVideoPath)
        }
    }

    internal class VideoCompressAsyncTask(var fragment: Fragment) : AsyncTask<String, String, String?>() {
        var compressFragment = ProfilePhotoUploadingFragment()
        override fun onPreExecute() {
            compressFragment.show(fragment.activity!!.supportFragmentManager, "CompressDialogStarted")
            compressFragment.isCancelable = false
            super.onPreExecute()
        }

        override fun doInBackground(vararg p0: String?): String? {
            val folderOfNewCreatedFiles = File(Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Aztagram/compressedVideo/")

            if (folderOfNewCreatedFiles.isDirectory || folderOfNewCreatedFiles.mkdirs()) {
                var newFilesPath = SiliCompressor.with(fragment.context).compressVideo(p0[0], folderOfNewCreatedFiles.path)
                return newFilesPath
            }
            return null
        }

        override fun onPostExecute(newFilesPath: String?) {

            if (!newFilesPath.isNullOrEmpty()) {
                (fragment as ShareNextFragment).uploadStorage(newFilesPath)
                compressFragment.dismiss()
            }

            super.onPostExecute(newFilesPath)
        }

    }

    internal class ImageCompressAsyncTask(var fragment: Fragment) : AsyncTask<String, String, String?>() {
        var compressFragment = ProfilePhotoUploadingFragment()

        override fun onPreExecute() {
            compressFragment.show(fragment.activity!!.supportFragmentManager, "CompressDialogStarted")
            compressFragment.isCancelable = false
            super.onPreExecute()
        }

        override fun doInBackground(vararg p0: String?): String? {
            val newCreatedFolder = File(Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Aztagram/compressed/")
            val newCompressedFilePath = SiliCompressor.with(fragment.context).compress(p0[0], newCreatedFolder)
            return newCompressedFilePath
        }

        override fun onPostExecute(newFilePath: String?) {
            (fragment as ShareNextFragment).uploadStorage(newFilePath)
            compressFragment.dismiss()
            super.onPostExecute(newFilePath)
        }
    }
}
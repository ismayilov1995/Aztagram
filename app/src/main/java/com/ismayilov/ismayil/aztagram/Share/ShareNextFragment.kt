package com.ismayilov.ismayil.aztagram.Share


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ismayilov.ismayil.aztagram.Home.HomeActivity
import com.ismayilov.ismayil.aztagram.Model.Posts
import com.ismayilov.ismayil.aztagram.Profile.ProfilePhotoUploadingFragment
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.DocumentsProsess
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import com.ismayilov.ismayil.aztagram.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_profile_photo_uploading.*
import kotlinx.android.synthetic.main.fragment_share_next.*
import kotlinx.android.synthetic.main.fragment_share_next.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ShareNextFragment : Fragment() {

    var choosenImageAndVideoPath: String? = null
    var fileTypeIsImage: Boolean? = null
    var photoUri: Uri? = null
    private lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    lateinit var mStorageRef: StorageReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_share_next, container, false)

        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference
        mUser = mAuth.currentUser!!
        mStorageRef = FirebaseStorage.getInstance().reference

        UniversalImageLoader.setImage(choosenImageAndVideoPath!!, view!!.ivSharedImg, null, "file://")

        view.tvShare.setOnClickListener { it ->
            //shekili sixishdirmaq
            if (fileTypeIsImage!!){
                DocumentsProsess.compressImage(this,choosenImageAndVideoPath)
            }
            //Videonu sixishdirir
            else{
                DocumentsProsess.compressVideo(this,choosenImageAndVideoPath)
            }
        }

        view.imgBack.setOnClickListener {
            activity!!.shareFragmentContainer.visibility = View.GONE
            activity!!.shareRootContainer.visibility = View.VISIBLE
        }


        return view
    }




    fun uploadStorage(newFilePath: String?) {
        val profFotoLoadingFragment = ProfilePhotoUploadingFragment()
        profFotoLoadingFragment.show(activity!!.supportFragmentManager, "profFotoLoading")
        profFotoLoadingFragment.isCancelable = false
        kullaniciProfilResminiGuncelle(profFotoLoadingFragment,mUser.uid,newFilePath)

    }

    private fun kullaniciProfilResminiGuncelle(profFotoLoadingFragment: ProfilePhotoUploadingFragment, userID: String, newFilePath: String?) {
        photoUri = Uri.parse("file://$newFilePath")
        val file = photoUri
        val mStorageRef = FirebaseStorage.getInstance().reference
        val profilePicReference = mStorageRef.child("images/users/" + userID + "/posts/" + file!!.lastPathSegment)
        val uploadTask = profilePicReference.putFile(file)
        uploadTask.addOnFailureListener {
            Toast.makeText(activity, "prof shekli yuklenende problem oldu", Toast.LENGTH_SHORT).show()
            profFotoLoadingFragment.dismiss()
        }.addOnSuccessListener { it ->
            var urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                profilePicReference.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    photoUri = null
                    val postId = mRef.child("posts/$userID").push().key
                    val uploadedPost = Posts(userID,postId,"",etSharedImageDescription.text.toString(),it.result.toString())
                    mRef.child("posts/$userID/$postId").setValue(uploadedPost)
                    mRef.child("posts/$userID/$postId/upload_date").setValue(ServerValue.TIMESTAMP)
                    if (activity != null) {
                        profFotoLoadingFragment.dismiss()
                        activity!!.startActivity(Intent(activity!!, HomeActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_NEW_TASK))
                        activity!!.finish()
                    }

                }
            }
        }.addOnProgressListener {
            var progress = 100 * it.bytesTransferred / it.totalByteCount
            profFotoLoadingFragment.textViewLoading.text = "Yuklenir..."
            profFotoLoadingFragment.tvLoading.visibility = View.VISIBLE
            profFotoLoadingFragment.tvLoading.text = progress.toString()
        }
    }

    @Subscribe(sticky = true)
    internal fun onChoosenImgPath(choosenImage: EventbusDataEvent.SharedImagePath) {
        choosenImageAndVideoPath = choosenImage!!.filePath!!
        fileTypeIsImage = choosenImage!!.fileTypeIsImage
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

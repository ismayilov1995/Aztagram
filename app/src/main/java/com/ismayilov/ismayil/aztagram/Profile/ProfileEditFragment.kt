package com.ismayilov.ismayil.aztagram.Profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ismayilov.ismayil.aztagram.Generic.ProfilePhotoUploadingFragment
import com.ismayilov.ismayil.aztagram.Model.Users
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import com.ismayilov.ismayil.aztagram.utils.RequestPermissionViewFragment
import com.ismayilov.ismayil.aztagram.utils.UniversalImageLoader
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileEditFragment : Fragment() {

    lateinit var circleImgFragment: CircleImageView
    lateinit var progressBar: ProgressBar
    lateinit var eventUser: Users
    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabaseRef: DatabaseReference
    lateinit var mStorageRef: StorageReference
    lateinit var profFotoLoadingFragment: ProfilePhotoUploadingFragment
    var profilePhotoUri: Uri? = null
    val CHOOSE_IMG = 100

    @Subscribe(sticky = true)
    internal fun onUserDetailsEvent(userDeatlis: EventbusDataEvent.BrodcastUserData) {
        eventUser = userDeatlis.user!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile_edit, container, false)
        progressBar = view.findViewById(R.id.progressBarEtProfil)
        profFotoLoadingFragment = ProfilePhotoUploadingFragment()
        setupUserDetails(view)
        mAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().reference
        mStorageRef = FirebaseStorage.getInstance().reference

        view.imgBack.setOnClickListener {
            activity?.onBackPressed()
        }

        view.imgSaveChanges.setOnClickListener {
            val userID = mAuth.currentUser!!.uid
            profFotoLoadingFragment.isCancelable = false

            if (profilePhotoUri != null) {
                profFotoLoadingFragment = ProfilePhotoUploadingFragment()
                profFotoLoadingFragment.show(activity!!.supportFragmentManager, "profFotoLoading")
                kullaniciProfilResminiGuncelle(profFotoLoadingFragment, userID)
            } else updateUserName(view, null, userID)

        }

        view.tvChangeProfilPic.setOnClickListener {
            getStorageAndCameraPermission()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_IMG && resultCode == AppCompatActivity.RESULT_OK && data!!.data != null) {
            profilePhotoUri = data.data
            cicrcleProfileImage.setImageURI(profilePhotoUri)
        }
    }

    private fun kullaniciProfilResminiGuncelle(profFotoLoadingFragment: ProfilePhotoUploadingFragment, userID: String) {
        val file = profilePhotoUri
        val mStorageRef = FirebaseStorage.getInstance().reference
        val profiliGuncellenenUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val profilePicReference = mStorageRef.child("images/users/" + profiliGuncellenenUserID + "/profile_picture/" + file!!.lastPathSegment)
        val uploadTask = profilePicReference.putFile(file)
        uploadTask.addOnFailureListener {
            Toast.makeText(activity, "prof shekli yuklenende problem oldu", Toast.LENGTH_SHORT).show()
            profFotoLoadingFragment.dismiss()
            updateUserName(view!!, false, userID)
        }.addOnSuccessListener { it ->
            var urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                profilePicReference.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    profilePhotoUri = null
                    mDatabaseRef.child("users").child(profiliGuncellenenUserID).child("users_details").child("profile_picture").setValue(it.result.toString())
                    if (activity != null) {
                        profFotoLoadingFragment.dismiss()
                        updateUserName(view!!, true, userID)
                    }
                }
            }
        }
    }

    //profPicIsChanged true dusa shekil yuklenib, false error verib null da ise hec shekil deyishmeyib
    private fun updateUserName(view: View, profPicIsChanged: Boolean?, userID: String) {

        if (eventUser.user_name != view.etUserName.text.toString()) {
            mDatabaseRef.child("users").orderByChild("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    var userNameisUsed = false
                    for (ds in p0.children) {
                        val snapUserName = ds.getValue(Users::class.java)!!.user_name
                        if (snapUserName == view.etUserName.text.toString()) {
                            userNameisUsed = true
                            saveNewDeatlist(view, profPicIsChanged, userID, false)
                            break
                        }
                    }
                    if (!userNameisUsed) {
                        mDatabaseRef.child("users").child(userID)
                                .child("user_name").setValue(view.etUserName.text.toString())
                        saveNewDeatlist(view, profPicIsChanged, userID, true)
                    }
                }
            })
        } else saveNewDeatlist(view, profPicIsChanged, userID, null)
    }

    private fun saveNewDeatlist(view: View?, profPicIsChanged: Boolean?, userID: String, userNameIsChange: Boolean?) {
        //true = deyishildi, false = istifadededir ve ya error, null = hecne olmadi
        var profileIsUpdated: Boolean? = null
        if (eventUser.full_name != view!!.etProfileName.text.toString()) {
            mDatabaseRef.child("users").child(userID).child("full_name")
                    .setValue(view.etProfileName.text.toString())
            profileIsUpdated = true
        }
        if (eventUser.users_details!!.web_sitie != view.etWebSitie.text.toString()) {
            mDatabaseRef.child("users").child(userID).child("users_details")
                    .child("web_sitie")
                    .setValue(view.etWebSitie.text.toString())
            profileIsUpdated = true
        }
        if (eventUser.users_details!!.biography != view.etBioghraphy.text.toString()) {
            mDatabaseRef.child("users").child(userID).child("users_details")
                    .child("biography")
                    .setValue(view.etBioghraphy.text.toString())
            profileIsUpdated = true
        }
        if (profPicIsChanged == null && profileIsUpdated == null && userNameIsChange == null) {
            Toast.makeText(activity, "Deyishiklik yoxdu", Toast.LENGTH_SHORT).show()
        } else if (userNameIsChange == false && (profPicIsChanged == true || profileIsUpdated == true)) {
            Toast.makeText(activity, "Melumatlar yenilendi, Istifadeci adi basqasi terefinden istifadededir", Toast.LENGTH_LONG).show()
            view.etUserName.setBackgroundResource(R.drawable.edirtext_white_background_error)
        } else if (userNameIsChange == false) {
            Toast.makeText(activity, "Istifadeci adi ISTIFADEDEDIR", Toast.LENGTH_SHORT).show()
            view.etUserName.setBackgroundResource(R.drawable.edirtext_white_background_error)
        } else {
            Toast.makeText(activity, "Melumatlar yenilendi", Toast.LENGTH_SHORT).show()
            activity!!.onBackPressed()
        }


    }

    private fun getStorageAndCameraPermission() {
        Dexter.withActivity(activity!!)
                .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.RECORD_AUDIO)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            //Icazeler verildi bura yaz
                            val intent = Intent()
                            intent.type = "image/*"
                            intent.action = Intent.ACTION_PICK
                            startActivityForResult(intent, CHOOSE_IMG)
                        }
                        if (report!!.isAnyPermissionPermanentlyDenied) {
                            val requestPermViewFragment = RequestPermissionViewFragment()
                            requestPermViewFragment.show(activity!!.supportFragmentManager, "permIsRequired")
                            requestPermViewFragment.isCancelable = false
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {

                        AlertDialog.Builder(activity!!)
                                .setTitle("Icaze lazimdir")
                                .setMessage("Proqramdan istifade ede bilesen deye icaze vermelisen. Icaze veressen ?")
                                .setPositiveButton("BELI") { p0, p1 ->
                                    token!!.continuePermissionRequest()
                                    p0.cancel()
                                }
                                .setNegativeButton("XEYIR") { p0, p1 ->
                                    token!!.cancelPermissionRequest()
                                    p0.cancel()
                                    activity!!.finish()
                                }
                                .show()
                        Log.e("HATA", "Icazelerden biri redd edildi")
                    }
                })
                .withErrorListener {
                    Log.e("HATA", "ERROR: $it")
                }
                .check()
    }

    private fun setupUserDetails(view: View?) {
        view!!.etProfileName.setText(eventUser.full_name)
        view.etUserName.setText(eventUser.user_name)
        view.etBioghraphy.setText(eventUser.users_details!!.biography)
        view.etWebSitie.setText(eventUser.users_details!!.web_sitie)
        UniversalImageLoader.setImage(eventUser.users_details!!.profile_picture!!, view.cicrcleProfileImage, progressBar, "")
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }


}

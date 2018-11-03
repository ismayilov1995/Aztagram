package com.ismayilov.ismayil.aztagram.Profile


import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth


class SignoutFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(activity!!)
                .setTitle("Hesabdan cixis")
                .setMessage("Eminsiniz ?")
                .setPositiveButton("Hesabdan Cix") { p0, p1 ->
                    FirebaseAuth.getInstance().signOut()
                }
                .setNegativeButton("Legv Et"){ p0,p1 ->
                    dismiss()
                }
                .create()
    }

}

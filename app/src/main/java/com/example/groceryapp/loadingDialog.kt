package com.example.groceryapp

import android.app.Activity
import android.app.AlertDialog

class loadingDialog (val mActivity : Activity){
    private lateinit var isdialog : AlertDialog
    fun startLoading(){

        val isdialog : AlertDialog
        //set View
        val infalter = mActivity.layoutInflater
        val dialogView = infalter?.inflate(R.layout.alert_dialog,null)
        //set dialog
        val builder = AlertDialog.Builder(mActivity)

        builder.setView(dialogView)
        isdialog = builder.create()
        isdialog.show()
    }
    fun isDismiss(){
        isdialog.dismiss()
    }


}
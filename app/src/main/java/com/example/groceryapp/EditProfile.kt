package com.example.groceryapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.groceryapp.databinding.ActivityEditProfileBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class EditProfile : AppCompatActivity() {

    private lateinit var binding : ActivityEditProfileBinding
    val REQUEST_IMAGE_CAPTURE = 2000
    private lateinit var uri: Uri
    var nampungURI : String? = null
    companion object{
        val IMAGE_REQUEST_CODE = 100
    }
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    //get Data
    var nampungUriAsli : String? = null
    var nampungNama : String? = null
    var nampungNomor : String? = ""
    var nampungAlamat : String? = ""

    //put new data
    //get Data
    var putNewNama : String? = null
    var putNewNomor : String? = null
    var putNewAlamat : String? = null
    var putNewUri : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //shimmer layout
        shimmerFrameLayout = binding.shimmerEditProfile
        shimmerFrameLayout.startShimmer()
        getData()

        binding.gantiFoto.setOnClickListener {
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            ImagePicker.Companion.with(this)
                .crop()
                .start()
//            pickImageGallery()
        }
        binding.btnSimpan.setOnClickListener {
            updateData()


        }
        binding.backActivity.setOnClickListener {
            onBackPressed()
        }




    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


                uri = data?.data!!
                binding.photoProfile.setImageURI(uri)
                uploadFirebase()

    }


    private fun uploadFirebase(){

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)

        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
        nampungURI = storageReference.toString()


        storageReference.putFile(uri).
        addOnCompleteListener {


            binding.photoProfile.setImageURI(uri)
            val customToast = LayoutInflater.from(this).inflate(R.layout.custom_toast_update_profile, null)
            val text = customToast.findViewById<TextView>(R.id.textToast)

            text.text = "Upload foto berhasil. Klik simpan!"
            Toast(this).apply {
                duration = Toast.LENGTH_SHORT
                setGravity(Gravity.BOTTOM, 0, 250)
                view = customToast
            }.show()


            if(it.isSuccessful){
                storageReference.downloadUrl.addOnCompleteListener { Task ->
                    Task.result.let { Uri ->
                        nampungURI = Uri.toString()
                    }
                }
            }


        }.addOnFailureListener{

            Toast.makeText(this, "Uplaod gagal;", Toast.LENGTH_SHORT).show()

        }





    }

    private fun getData(){
        //get token
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")
        Log.d("ini token berhasil", token.toString())
        AndroidNetworking.get("https://grocery-api.tonsu.site/members")
            .setPriority(Priority.LOW)
            .addHeaders("token", "Bearer" + " " + token)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    val res: JSONObject? = response?.getJSONObject("data")
                    try {

                        binding.edNama.setText(res?.getString("name"))
                        binding.edNomorPonsel.setText(res?.getString("phone_number"))
                        binding.edAlamat.setText(res?.getString("address"))

                        val photo = res?.getString("photo_profile")
                        Picasso.get()
                            .load(photo)
                            .into(binding.photoProfile)

                        //get data
                        nampungUriAsli = res?.getString("photo_profile")
                        nampungNama = res?.getString("name")
                        nampungNomor = res?.getString("phone_number")
                        nampungAlamat = res?.getString("address")



                    }catch (e : JSONException){
                        e.printStackTrace()
                    }
                    shimmerFrameLayout.startShimmer()
                    shimmerFrameLayout.visibility = View.GONE
                    binding.parentEditProfile.visibility = View.VISIBLE


                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        this@EditProfile,
                        "Tidak ada jaringan internet!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })

    }
    private fun updateData(){

        val putNama = binding.edNama.text.toString().trim()
        val putNomor = binding.edNomorPonsel.text.toString().trim()
        val putAlamat = binding.edAlamat.text.toString().trim()
        Log.d("nampung uri aaaaa", nampungUriAsli.toString())

        if (putNama.isEmpty()){
            val customToast = LayoutInflater.from(this).inflate(R.layout.custom_toast_profile, null)
            val text = customToast.findViewById<TextView>(R.id.textToast)

            text.text = "Field nama tidak boleh kosong"
            Toast(this).apply {
                duration = Toast.LENGTH_SHORT
                setGravity(Gravity.BOTTOM, 0, 250)
                view = customToast
            }.show()
        }else{
            putNewNama = putNama
        }
        if (putNomor.isEmpty()){
            val customToast = LayoutInflater.from(this).inflate(R.layout.custom_toast_profile, null)
            val text = customToast.findViewById<TextView>(R.id.textToast)

            text.text = "Field nomor tidak boleh kosong"
            Toast(this).apply {
                duration = Toast.LENGTH_SHORT
                setGravity(Gravity.BOTTOM, 0, 250)
                view = customToast
            }.show()


        }else{
            putNewNomor = putNomor
        }
        if(putAlamat.isEmpty()){
            val customToast = LayoutInflater.from(this).inflate(R.layout.custom_toast_profile, null)
            Toast(this).apply {
                duration = Toast.LENGTH_SHORT
                setGravity(Gravity.BOTTOM, 0, 250)
                view = customToast
            }.show()


        }else{
            putNewAlamat = putAlamat
        }
        if(nampungURI.isNullOrEmpty()){
            putNewUri = nampungUriAsli.toString()
        }else{
            putNewUri = nampungURI.toString()
        }


        //get token
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")

        Log.d("ini uri1", putNewNama.toString())
        Log.d("ini uri2", putNewNomor.toString())
        Log.d("ini uri1", putNewAlamat.toString())
        Log.d("ini uri1", putNewUri.toString())
        Log.d("Ini put New nomor", putNomor)
        val jsonObject = JSONObject()
        try {

            jsonObject.put("name", putNewNama)
            jsonObject.put("phone_number", putNewNomor)
            jsonObject.put("address", putNewAlamat)
            jsonObject.put("photo_profile", putNewUri.toString())

            Log.d("JSON OBject", jsonObject.toString())

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        AndroidNetworking.put("https://grocery-api.tonsu.site/members")
            .addJSONObjectBody(jsonObject)
            .addHeaders("token", "Bearer" + " " +token)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("ini respon email", response.toString())
                    try {
                        if (response.getString("success").equals("true")) {
                            val customToast = LayoutInflater.from(this@EditProfile).inflate(R.layout.custom_toast_update_profile, null)
                            Toast(this@EditProfile).apply {
                                duration = Toast.LENGTH_SHORT
                                setGravity(Gravity.BOTTOM, 0, 250)
                                view = customToast
                            }.show()

                        } else {
                            val customToast = LayoutInflater.from(this@EditProfile).inflate(R.layout.custom_toast_profile, null)
                            val textToast = customToast.findViewById<TextView>(R.id.textToast)
                            textToast.text = "Update profile gagal"
                            Toast(this@EditProfile).apply {
                                duration = Toast.LENGTH_SHORT
                                setGravity(Gravity.BOTTOM, 0, 250)
                                view = customToast
                            }.show()

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@EditProfile, "kesalahan", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onError(error: ANError) {
                    // handle error
                }
            })
    }


}

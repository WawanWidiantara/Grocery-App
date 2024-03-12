package com.example.groceryapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.groceryapp.databinding.ActivityDetailProductBinding
import com.example.groceryapp.databinding.ShimmerDetailProductBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DetailProduct : AppCompatActivity() {
    private lateinit var binding : ActivityDetailProductBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        dialog()
        binding.shimmerDetailProduct.startShimmer()

        getToken()


        binding.backActivity.setOnClickListener {
            onBackPressed()
        }


    }
    private fun activateProduct( token : String, getId : Int, image : String, title : String, ket : String ){
            val dialogPesanan = LayoutInflater.from(applicationContext).inflate(R.layout.dialog_activate, null)


            //inisialisai
            val imagePesanan = dialogPesanan.findViewById<ImageView>(R.id.imagePesanan)
            val titlePesanan = dialogPesanan.findViewById<TextView>(R.id.titlePesanan)
            val ketPesanan = dialogPesanan.findViewById<TextView>(R.id.keteranganPesanan)


            Picasso.get()
                .load(image)
                .into(imagePesanan)
            titlePesanan.text = title
            ketPesanan.text = ket

            Toast(applicationContext).apply {
                duration = Toast.LENGTH_SHORT
                setGravity(Gravity.BOTTOM, 0, 350)
                view = dialogPesanan
            }.show()
            val jsonObject = JSONObject()
            try {
                jsonObject.put("product_id", getId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            AndroidNetworking.post("https://grocery-api.tonsu.site/products/activate")

                .addJSONObjectBody(jsonObject)
                .addHeaders("Content-Type", "application/json")
                .setPriority(Priority.MEDIUM)
                .addHeaders("token", "Bearer" + " " + token)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        Log.d("ini respon register", response.toString())
                        try {
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(this@DetailProduct, "kesalahan", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(error: ANError) {
                        // handle error
                    }
                })


    }
    private fun getData(){

        val id = this.intent.getStringExtra("id")?.toInt()
        Log.d("ini id", id.toString())

        //get token
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")
        AndroidNetworking.get("https://grocery-api.tonsu.site/products/detail/$id")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("ini respon detail", response.toString())
                    if (response?.getString("success").equals("true")){
                        val res: JSONObject = response.getJSONObject("data")
                        try {


                            val getImage = res.getString("image")
                            val getTitle = res.getString("product_name")
                            val getKet = res.getString("unit")
                            if (getImage.isNotEmpty()){
                                Picasso.get()
                                    .load(getImage)
                                    .into(binding.imageView)
                            }else{
                                Picasso.get()
                                    .load(R.drawable.example_image)
                                    .into(binding.imageView)
                            }

                            binding.titleDetail.text = res.getString("product_name")
                            binding.hargaDetail.text = "Rp" + " " + res.getInt("original_price").toString()

                            binding.deskripsiDetail.text = res.getString("description")
                            binding.diskonDetail.apply {
                                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                                binding.diskonDetail.text = "Rp" + " " + res.getInt("member_price")
                            }
                            binding.btnActivate.setOnClickListener {
                                activateProduct(token.toString(), id!!.toInt(), getImage, getTitle, getKet )
                            }


                        }catch (e : JSONException){
                            e.printStackTrace()
                        }
                        binding.shimmerDetailProduct.stopShimmer()
                        binding.shimmerDetailProduct.visibility = View.GONE
                        binding.container.visibility = View.VISIBLE
                    }else{
                        onDestroy()
                    }



//                    Log.d("ini respon detial", response.toString())
//

                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        this@DetailProduct,
                        "Tidak ada jaringan internet!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }
    private fun getToken(){
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
                    Log.d("ini respon token detail produk", response.toString())
                    try {

                        val cek = response?.getString("success")

                        if ( cek == "true") {
                            getData()
                        }else{

                            val intent = Intent(this@DetailProduct, LoginActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this@DetailProduct, "Keluar dari APlikasi", Toast.LENGTH_SHORT).show()
                        }

                    }catch (e : JSONException){
                        e.printStackTrace()
                    }


                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        this@DetailProduct,
                        "Tidak ada jaringan internet!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }


}
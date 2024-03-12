package com.example.groceryapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.groceryapp.databinding.ActivityDetailRiwayatBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DetailRiwayat : AppCompatActivity() {
    private lateinit var binding : ActivityDetailRiwayatBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var detailRiwayatList : ArrayList<dataDetailRiwayat>
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)


        recyclerView = findViewById(R.id.recycleViewDetailRiwayat)
        detailRiwayatList = arrayListOf<dataDetailRiwayat>()

        //shimmer layout
        shimmerFrameLayout = binding.shimmerLayoutDetailRiwayat
        shimmerFrameLayout.startShimmer()
        getToken()

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getDataRecycleView(){
        //get inovice
        val invoice = this.intent.getStringExtra("invoice")
        Log.d("ini id", invoice.toString())
        //get token
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")
        AndroidNetworking.get("https://grocery-api.tonsu.site/orders/transaction/$invoice")
            .setPriority(Priority.LOW)
            .addHeaders("token", "Bearer" + " " + token)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    if (response?.getString("success").equals("true")){
                        Log.d("Ini respon fetch", response.toString())
                        val res: JSONArray? = response?.getJSONArray("products")
                        for (i in 0 until res?.length()!!) {
                            val item = res.getJSONObject(i)
                            detailRiwayatList.add(
                                dataDetailRiwayat(
                                    item.getString("image"),
                                    item.getString("product_name"),
                                    item.getInt("quantity"),
                                    item.getInt("price"),
                                    item.getInt("total"),

                                    )
                            )
                        }

                        shimmerFrameLayout.startShimmer()
                        shimmerFrameLayout.visibility = View.GONE
                        binding.recycleViewDetailRiwayat.visibility = View.VISIBLE
                        populateData()
                    }else{
                        onDestroy()
                    }

                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        this@DetailRiwayat,
                        "Tidak ada jaringan internet!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    private fun getData(){

        val invoice = this.intent.getStringExtra("invoice")
        Log.d("ini id", invoice.toString())
        //get token
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")
        AndroidNetworking.get("https://grocery-api.tonsu.site/orders/transaction/$invoice")
            .setPriority(Priority.LOW)
            .addHeaders("token", "Bearer" + " " + token)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    if (response?.getString("success").equals("true")){
                        try {

                            val cekSatus = response.getInt("status")

                            if (cekSatus == 1){
                                binding.parentCheck.setBackgroundColor(Color.parseColor("#39C7A5"))
                                binding.textCheck.text = "Selesai"
                            }else{
                                binding.parentCheck.setBackgroundColor(Color.parseColor("#F51A1A"))
                                binding.textCheck.text = "Dibatalkan"
                            }

                            binding.textInvoice.text = "INVOICE" + response.getString("invoice")
                            binding.textTanggal.text = response.getString("date")
                            binding.totalHarga.text = "TOTAL HARGA :"+ " " + response.getInt("total_payment").toString()

                        }catch (e : JSONException){
                            e.printStackTrace()
                        }
                        shimmerFrameLayout.startShimmer()
                        shimmerFrameLayout.visibility = View.GONE
                        binding.parentDetailRiwayat.visibility = View.VISIBLE
                    }else{
                        onDestroy()
                    }


                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        this@DetailRiwayat,
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
                    try {

                        val cek = response?.getString("success")

                        if ( cek == "true") {
                            getData()
                            getDataRecycleView()
                        }else{

                            val intent = Intent(this@DetailRiwayat, LoginActivity::class.java)
                            startActivity(intent)
                        }

                    }catch (e : JSONException){
                        e.printStackTrace()
                    }


                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        this@DetailRiwayat,
                        "Tidak ada jaringan internet!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }
    private fun populateData() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        val adp = this?.let { adapterDetailRiwayat(it,detailRiwayatList) }
        recyclerView.adapter = adp
    }
}
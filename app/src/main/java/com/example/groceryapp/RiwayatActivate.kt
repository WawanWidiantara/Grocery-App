package com.example.groceryapp

import android.content.Context
import android.content.Intent
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
import com.example.groceryapp.databinding.ActivityRiwayatActivateBinding
import com.facebook.shimmer.ShimmerFrameLayout
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RiwayatActivate : AppCompatActivity() {

    private lateinit var binding : ActivityRiwayatActivateBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var activateList: ArrayList<dataActivate>
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatActivateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.recycleViewRiwayatActivate)
        activateList = arrayListOf<dataActivate>()


        //shimmer layout
        shimmerFrameLayout = binding.shimmerRiwayatActivate
        shimmerFrameLayout.startShimmer()
        getToken()
        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getData() {
        //get token
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")
        AndroidNetworking.get("https://grocery-api.tonsu.site/orders/transaction")
            .setPriority(Priority.LOW)
            .addHeaders("token", "Bearer" + " " + token)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    if (response?.getString("success").equals("true")){
                        Log.d("Ini respon fetch riwayat active", response.toString())
                        val res: JSONArray? = response?.getJSONArray("data")
                        for (i in 0 until res?.length()!!) {
                            val item = res.getJSONObject(i)
                            activateList.add(
                                dataActivate(
                                    item.getInt("status"),
                                    item.getString("date"),
                                    item.getString("invoice"),
                                    item.getInt("total_bayar"),
                                )
                            )
                        }

                        shimmerFrameLayout.startShimmer()
                        shimmerFrameLayout.visibility = View.GONE
                        binding.recycleViewRiwayatActivate.visibility = View.VISIBLE

                        populateData()
                    }else{
                        onDestroy()
                    }

//                    val swipup = activity?.findViewById<SwipeRefreshLayout>(R.id.swipeUp)
//                    swipup!!.isRefreshing = false
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        this@RiwayatActivate,
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
                    Log.d("ini respon token member", response.toString())
                    try {

                        val cek = response?.getString("success")

                        if ( cek == "true") {
                            getData()
                        }else{

                            val intent = Intent(this@RiwayatActivate, LoginActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this@RiwayatActivate, "Keluar dari APlikasi", Toast.LENGTH_SHORT).show()
                        }

                    }catch (e : JSONException){
                        e.printStackTrace()
                    }


                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        this@RiwayatActivate,
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
        val adp = this?.let { adapterActivate(it,activateList) }
        recyclerView.adapter = adp
    }
}
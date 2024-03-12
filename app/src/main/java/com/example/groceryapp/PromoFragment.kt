package com.example.groceryapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.facebook.shimmer.ShimmerFrameLayout
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class PromoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var promoList : ArrayList<dataPromo>
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_promo, container, false)


        recyclerView = view.findViewById(R.id.recycleViewPromo)
        recyclerView.setHasFixedSize(true)
        val manager = LinearLayoutManager(activity)
        recyclerView.layoutManager = manager
        promoList = arrayListOf<dataPromo>()

        //shimmer layout
        shimmerFrameLayout = view.findViewById<ShimmerFrameLayout>(R.id.shimmerLayoutPromo)
        shimmerFrameLayout.startShimmer()

        getToken()


        return view
    }
    private fun getData() {
        AndroidNetworking.get("https://grocery-api.tonsu.site/products/promo")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {

                    Log.d("Ini respon fetch", response.toString())

                    if (response?.getString("success").equals("true")){
                        val res: JSONArray? = response?.getJSONArray("data")
                        for (i in 0 until res?.length()!!) {
                            val item = res.getJSONObject(i)
                            promoList.add(
                                dataPromo(
                                    item.getInt("id"),
                                    item.getString("product_name"),
                                    item.getString("unit"),
                                    item.getInt("original_price"),
                                    item.getInt("member_price"),
                                    item.getInt("discount_price"),
                                    item.getString("image"),
                                    item.getString("description"),
                                    item.getInt("featured"),
                                    item.getString("insert_date"),

                                    )
                            )
                        }
                        shimmerFrameLayout.startShimmer()
                        shimmerFrameLayout.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        populateData()
                    }else{
                        onDestroy()
                    }

//                    val swipup = activity?.findViewById<SwipeRefreshLayout>(R.id.swipeUp)
//                    swipup!!.isRefreshing = false
                }

                override fun onError(anError: ANError?) {
                    alertDialogShow()
                }

            })
    }
    private fun getToken(){
        //get token
        val sharedPreference =  activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
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

                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(activity, "Keluar dari APlikasi", Toast.LENGTH_SHORT).show()
                        }

                    }catch (e : JSONException){
                        e.printStackTrace()
                    }


                }

                override fun onError(anError: ANError?) {
                    alertDialogShow()
                }

            })
    }
    private fun populateData() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        val adp = activity?.let { adapterPromo(it,promoList) }
        recyclerView.adapter = adp
    }
    private fun alertDialogShow(){
        val loading = loadingDialog(activity as Activity)
        loading.startLoading()

    }

}
package com.example.groceryapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
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


class HomeFragment : Fragment() {

    private lateinit var reyclerView : RecyclerView
    private lateinit var homeList : ArrayList<dataHome>

    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        reyclerView = view.findViewById(R.id.recycleViewHome)
        reyclerView.setHasFixedSize(true)
        val manager = LinearLayoutManager(activity)
        reyclerView.layoutManager = manager
        homeList = arrayListOf<dataHome>()

        //shimmer layout
        shimmerFrameLayout = view.findViewById<ShimmerFrameLayout>(R.id.shimmerLayoutHome)
        shimmerFrameLayout.startShimmer()
//        dialog()
        getData()


        return view
    }

    private fun getData() {

        AndroidNetworking.get("https://grocery-api.tonsu.site/products/katalog")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Log.d("Ini respon fetch", response.toString())
                    if (response?.getString("success").equals("true")){
                        val res: JSONArray? = response?.getJSONArray("data")
                        for (i in 0 until res?.length()!!) {
                            val item = res.getJSONObject(i)
                            homeList.add(
                                dataHome(
                                    item.getInt("id"),
                                    item.getString("image"),
                                )
                            )
                        }
                        shimmerFrameLayout.startShimmer()
                        shimmerFrameLayout.visibility = View.GONE
                        reyclerView.visibility = View.VISIBLE
                        populateData()
                    }else{
                        onDestroy()

                    }


                }

                override fun onError(anError: ANError?) {
                    onDestroy()

                    alertDialogShow()
                }

            })

    }

    private fun populateData() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        reyclerView.layoutManager = layoutManager
        val adp = activity?.let { adapterHome(it,homeList) }
        reyclerView.adapter = adp
    }


    private fun alertDialogShow(){
        val loading = loadingDialog(activity as Activity)
        loading.startLoading()

    }



}
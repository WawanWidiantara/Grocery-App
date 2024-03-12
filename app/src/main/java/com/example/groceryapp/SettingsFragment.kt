package com.example.groceryapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject


class SettingsFragment : Fragment() {

    private lateinit var btnProfile : RelativeLayout
    private lateinit var btnRiwayatActive : RelativeLayout
    private lateinit var btnTentangKami : RelativeLayout
    private lateinit var btnKeluarAplikasi : RelativeLayout
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    private lateinit var imageProfile : ImageView
    private lateinit var nameProfile : TextView
    private lateinit var nomorProfile : TextView
    private lateinit var alamatProfile : TextView
    private lateinit var parentSettings : LinearLayout
    private lateinit var btnLogut: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)



        //inisialisasi button
        btnProfile = view.findViewById(R.id.ubahProfile)
        btnRiwayatActive = view.findViewById(R.id.riwayatActive)
        btnTentangKami = view.findViewById(R.id.tentangKami)
        btnKeluarAplikasi = view.findViewById(R.id.keluarAplikasi)
        imageProfile = view.findViewById(R.id.imageProfile)
        nameProfile = view.findViewById(R.id.nameProfile)
        nomorProfile = view.findViewById(R.id.nomorProfile)
        alamatProfile = view.findViewById(R.id.alamatProfile)
        parentSettings = view.findViewById(R.id.parentSettings)
        btnLogut = view.findViewById(R.id.keluarAplikasi)

        btnLogut.setOnClickListener {
            val preferences: SharedPreferences = requireActivity().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            val intent = Intent(activity, EditProfile::class.java)
            startActivity(intent)
        }
        btnTentangKami.setOnClickListener {
            val intent = Intent(activity, TentangKami::class.java)
            startActivity(intent)
        }
        btnRiwayatActive.setOnClickListener {
            val intent = Intent(activity, RiwayatActivate::class.java)
            startActivity(intent)
        }
        //shimmer layout
        shimmerFrameLayout = view.findViewById<ShimmerFrameLayout>(R.id.shimmerLayoutSettings)
        shimmerFrameLayout.startShimmer()


        getData()


        return view
    }
    private fun getData(){
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

                    Log.d("ini respond detaoill",response?.getJSONObject("data").toString())
                    val res: JSONObject? = response?.getJSONObject("data")
                    try {
                        nameProfile.text = res?.getString("name")
                        alamatProfile.text = res?.getString("address")
                        nomorProfile.text = res?.getString("phone_number")

                        if ( res?.getString("address").isNullOrEmpty()){
                            alamatProfile.visibility = View.GONE
                        }
                        if ( res?.getString("phone_number").isNullOrEmpty()){
                            nomorProfile.visibility = View.GONE
                        }



                        val photo = res?.getString("photo_profile")
                        Log.d("Ini photo pengagturan", photo.toString())
                        Picasso.get()
                            .load(photo)
                            .into(imageProfile)



                    }catch (e : JSONException){
                        e.printStackTrace()
                    }
                    shimmerFrameLayout.startShimmer()
                    shimmerFrameLayout.visibility = View.GONE
                    parentSettings.visibility = View.VISIBLE


                }

                override fun onError(anError: ANError?) {
                    alertDialogShow()
                }

            })

    }
    private fun alertDialogShow(){
        val loading = loadingDialog(activity as Activity)
        loading.startLoading()

    }

    override fun onResume() {
        super.onResume()
        getData()
    }


}
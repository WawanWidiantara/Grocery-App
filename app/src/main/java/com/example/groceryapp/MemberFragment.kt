package com.example.groceryapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class MemberFragment : Fragment() {
    private lateinit var RecyclerView : RecyclerView
    private lateinit var memberList : ArrayList<dataMember>
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    private lateinit var textDiskon : TextView
    private lateinit var btnQR : Button
    private lateinit var textQR : TextView
    private lateinit var warning : LinearLayout
    private lateinit var warningQR : CardView
    var nampungURLQR : String? = null
    var nampungInvoice : String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_member, container, false)

        RecyclerView = view.findViewById(R.id.recycleViewMember)


        memberList = arrayListOf<dataMember>()
        //shimmer layout
        shimmerFrameLayout = view.findViewById<ShimmerFrameLayout>(R.id.shimmerLayoutMember)
        shimmerFrameLayout.startShimmer()
        getToken()

        btnQR = view.findViewById(R.id.btnGenerate)
        textQR = view.findViewById(R.id.textQRCode)
        warning = view.findViewById(R.id.warning)
        warningQR = view.findViewById(R.id.cardQRCode)

//        btnQR.setOnClickListener {
//
//            getQRCode()
//
//
//        }
        getQRCode()




//        val layoutFetchMember = LayoutInflater.from(container?.context).inflate(R.layout.fetch_member, container, false)
//        textDiskon = layoutFetchMember.findViewById(R.id.diskonMember)



        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = arguments?.getString("invoice")
//        nampungInvoice =
        Log.d("nampung invoice from main activity", data.toString())
    }




    private fun generateQRCode(data : String){


        //shareURLInvoice
        val sharedPreference =  activity?.getSharedPreferences("get_invoice",Context.MODE_PRIVATE)
        val editorToken = sharedPreference?.edit()
        editorToken?.putString("invoice",data)
        editorToken?.commit()
        Log.d("Generate QR harusnya", data.toString())
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_qrcode, null)
        val alertDialog = AlertDialog.Builder(context).setView(dialogView)
        alertDialog.setView(dialogView)
        val dialog = alertDialog.create()
        val imageQR = dialogView.findViewById<ImageView>(R.id.imageQRCode)
        dialog.window?.setBackgroundDrawableResource(android.R.color.white)

        val writer = QRCodeWriter()
        try{
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 200, 200)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width){
                for (y in 0 until height){
                    bmp.setPixel(x, y, if (bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                }
            }
            imageQR.setImageBitmap(bmp)
            dialog.show()

//            if (nampungInvoice.isNullOrEmpty()){
//                dialog.show()
//            }else if(nampungInvoice.equals("1")){
//                dialog.dismiss()
//            }
        }catch (e : WriterException){
            e.printStackTrace()
        }

    }

    private fun cekProfile(){
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
                        Log.d("iall",response?.getJSONObject("data").toString())
                        if ( res?.getString("address").isNullOrEmpty() && res?.getString("phone_number").isNullOrEmpty() ){
                            Log.d("dd",response?.getJSONObject("data").toString())
                            val dialogWarning = LayoutInflater.from(context).inflate(R.layout.dialog_cek_profile, null)
                            val alertDialog = AlertDialog.Builder(context).setView(dialogWarning)
                            alertDialog.setView(dialogWarning)
                            val dialog = alertDialog.create()
                            dialog.show()
                            val btnWarning = dialogWarning.findViewById<Button>(R.id.btnWarning)

                            btnWarning.setOnClickListener {
                                postGenerate()
                                Log.d("cek profile nampung RUL btn warning", nampungURLQR.toString())
                                generateQRCode(nampungURLQR.toString())
                                dialog.dismiss()
                                val intent = Intent(context, EditProfile::class.java)
                                startActivity(intent)
                            }
                        }else{
                            postGenerate()
                            Log.d("cek profile nampung RUL btn  else", nampungURLQR.toString())
                            generateQRCode(nampungURLQR.toString())
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

    private fun getQRCode(){

        //get token
        val sharedPreference =  activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")
        AndroidNetworking.get("https://grocery-api.tonsu.site/orders")
            .setPriority(Priority.LOW)
            .addHeaders("token", "Bearer" + " " +token)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("Ini respon detailsss", response.toString())
                    try {
                        val data = response.getString("url")
                        nampungURLQR = data
                        Log.d("Get QR code from API", data)

                        Log.d("Get QR code from API nampung URLQR", nampungURLQR.toString())
                        if (response.getString("url").isNotEmpty()){
                            Log.d("Masuk konoidi", response.toString())

                            btnQR.setOnClickListener {
                                cekProfile()

                            }
                        }else{
                            btnQR.setOnClickListener {
                                cekProfile()
                            }
                            Log.d("tidak Masuk konoidi", response.toString())
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(anError: ANError?) {
                    alertDialogShow()
                }

            })
    }

    private fun postGenerate(){


        //get token
        val sharedPreference =  activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")
        AndroidNetworking.post("https://grocery-api.tonsu.site/orders")
            .setPriority(Priority.LOW)
            .addHeaders("token", "Bearer" + " " +token)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {

                    try {
                        Log.d("ini post", response.toString())

                        val urlInvoice = response.getString("url")
                        nampungURLQR = urlInvoice
                        Log.d("Generate Post url", urlInvoice.toString())
                        Log.d("Generate Post url nampung URL", nampungURLQR.toString())

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(context, "kesalahan", Toast.LENGTH_SHORT).show()
                    }
                }


                override fun onError(error: ANError) {
                    alertDialogShow()
                }
            })
    }

    private fun getData() {
        //get token
        val sharedPreference =  activity?.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")
        AndroidNetworking.get("https://grocery-api.tonsu.site/products/cart")
            .setPriority(Priority.LOW)
            .addHeaders("token", "Bearer" + " " + token)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Log.d("ini respond fetchmember",response.toString())
                    if (response?.getString("success").equals("true")){
                        val res: JSONArray? = response?.getJSONArray("data")
                        if (res!!.isNull(0)){
//                            val preferences: SharedPreferences = activity!!.getSharedPreferences("get_invoice", Context.MODE_PRIVATE)
//                            val editor = preferences.edit()
//                            editor.clear()
//                            editor.apply()
                            warning.visibility = View.VISIBLE
                            warningQR.visibility = View.GONE
                        }else{
                            warning.visibility = View.GONE
                            warningQR.visibility = View.VISIBLE
                        }
                        for (i in 0 until res?.length()!!) {
                            val item = res.getJSONObject(i)
                            memberList.add(
                                dataMember(
                                    item.getInt("id"),
                                    item.getInt("product_id"),
                                    item.getString("product_name"),
                                    item.getInt("original_price"),
                                    item.getInt("member_price"),
                                    item.getInt("discount_price"),
                                    item.getString("image"),
                                    item.getInt("quantity"),

                                    )
                            )


                        }

                        shimmerFrameLayout.startShimmer()
                        shimmerFrameLayout.visibility = View.GONE
                        RecyclerView.visibility = View.VISIBLE
                        populateData()
//                    val swipup = activity?.findViewById<SwipeRefreshLayout>(R.id.swipeUp)
//                    swipup!!.isRefreshing = false
                    }else{
                        onDestroy()
                    }


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
                    Log.d("ini respon token member", response.toString())
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

        RecyclerView.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(context, 2)
        RecyclerView.layoutManager = layoutManager
        val adp = activity?.let { adapterMember(it, memberList) }
        RecyclerView.adapter = adp
    }
    private fun alertDialogShow(){
        val loading = loadingDialog(activity as Activity)
        loading.startLoading()

    }


}
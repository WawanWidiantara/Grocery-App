package com.example.groceryapp

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.groceryapp.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var detailRiwayatList : ArrayList<dataDetailRiwayat>
    private lateinit var permissionLauncer : ActivityResultLauncher<Array<String>>
    private var isReadPermission = false

    //notification
    val list: MutableList<String> = ArrayList()
    private lateinit var notificationManager: NotificationManagerCompat
    var dataBaru : String? = null
    private var stopThread = false

    var getInvoice : String? = null
    var cekNotif : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        replaceFragment(HomeFragment())
        //notification
        notificationManager = NotificationManagerCompat.from(this)
        startThread()




        permissionLauncer = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permissions ->
            isReadPermission = permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?:isReadPermission
        }
        requestPermission()




        //bottom navigation
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){

                R.id.homeFragment -> replaceFragment(HomeFragment())
                R.id.promoFragment -> replaceFragment(PromoFragment())
                R.id.memberFragment -> replaceFragment(MemberFragment())
                R.id.settingsFragment -> replaceFragment(SettingsFragment())

                else ->{

                }
            }
            true
        }
    }


    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView,fragment)
        fragmentTransaction.commit()
    }

    private fun requestPermission(){
        isReadPermission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest : MutableList<String> = ArrayList()
        if(!isReadPermission){
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if(permissionRequest.isNotEmpty()){
            permissionLauncer.launch(permissionRequest.toTypedArray())
        }

    }
    private fun notification(title : String, tambah : Int, icon : Int){
        cekNotif = 1 - tambah
        val intent = Intent(this, RiwayatActivate::class.java)
        val resultIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT )
        
        val builder = NotificationCompat.Builder(this, BaseApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_polos)
            .setContentTitle("Notifikasi Pembayaran")
            .setContentText(title)
            .setAutoCancel(true)
            .setContentIntent(resultIntent)
            .setSmallIcon(icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .addAction(R.mipmap.ic_launcher, "Lihat riwayat pesanan", resultIntent)
            .setColor(Color.GREEN)

        val notification = builder.build()
        notificationManager.notify(1, notification)
        val preferences: SharedPreferences = getSharedPreferences("get_invoice", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    fun startThread() {
        stopThread = false

        val runnable = ExampleRunnable(1000000000)
        Thread(runnable).start()

    }

    fun stopThread(view: View?) {
        stopThread = true
    }

    internal inner class ExampleThread(var seconds: Int) : Thread() {
        override fun run() {
            for (i in 0 until seconds) {


                try {
                    sleep(10000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    internal inner class ExampleRunnable(var seconds: Int) : Runnable {
        override fun run() {
            for (i in 0 until seconds) {
                if (stopThread) return
                val shareInvoice =  getSharedPreferences("get_invoice", Context.MODE_PRIVATE)
                val invoice = shareInvoice?.getString("invoice","")
                Log.d("coba cek inovice", invoice.toString())
                sendInvoice()

                if (invoice!!.isEmpty()){

                    Log.d(TAG, "startThreadKondisi: $i")
                }else{
                    Log.d(TAG, "getData")

                    getData()
                    val splitURL = invoice.replace("https://grocery-admin.tonsu.site/pay.html?", "").split("=")
                     getInvoice = splitURL[1].replace("invoice=","")

                }
                try {
                    Thread.sleep(6000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }


    companion object {
        private const val TAG = "MainActivity"
    }
    private fun getData() {

        Log.d("INI get Invoice", getInvoice.toString())
        //get token
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")
        AndroidNetworking.get("https://grocery-api.tonsu.site/orders/transaction/$getInvoice")
            .setPriority(Priority.LOW)
            .addHeaders("token", "Bearer" + " " + token)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Log.d("Ini respon main activity notifikasi", response.toString())
                    if (response?.getString("success").equals("true")){

                        val cekStatus = response?.getInt("status")
                        Log.d("cek status main notif", cekStatus.toString())
                        val title :String
                        if(cekStatus!!.equals(1)){
                            Log.d("cekNofi", cekNotif.toString())
                            if (cekNotif.equals(0)){
                                title = "Pembayaran berhasil"
                                cekNotif = 1
                                notification(title, cekNotif, R.drawable.icon_payment_success)
                                getInvoice = null


                            }
                        }else{
                            if (cekNotif.equals(0)){
                                cekNotif = 1
                                title = "Pembayaran gagal"
                                notification(title, cekNotif, R.drawable.icon_payment_failed)
                                getInvoice = null

                            }

                        }

                    }

                }

                override fun onError(anError: ANError?) {
                }

            })

    }

    private fun sendInvoice(){
        //send data to fragment
        val cekInvoice = 1
        val bundle = Bundle()
        val fragobj = MemberFragment()
        bundle.putString("invoice", cekInvoice.toString())
        fragobj.arguments = bundle
        Log.d("ini kiriman invoice",cekInvoice.toString() )

    }



}
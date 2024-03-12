package com.example.groceryapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONException
import org.json.JSONObject

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            getToken()
        }, 3000)
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
                            val intent = Intent(this@SplashScreen, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            val intent = Intent(this@SplashScreen, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                    }catch (e : JSONException){
                        e.printStackTrace()
                    }


                }

                override fun onError(anError: ANError?) {
                    val customToast = LayoutInflater.from(this@SplashScreen).inflate(R.layout.toast_splash, null)
                    Toast(this@SplashScreen).apply {
                        duration = Toast.LENGTH_SHORT
                        setGravity(Gravity.BOTTOM, 0, 250)
                        view = customToast
                    }.show()
                    Handler().postDelayed({
                        finish()
                        System.exit(0);
                    }, 3000)

                }

            })
    }
}
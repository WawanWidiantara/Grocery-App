package com.example.groceryapp

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.groceryapp.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.textDaftar.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnLogin.setOnClickListener {
            verifikasiLogin()
        }

        getToken()



    }

    private fun verifikasiLogin(){
        val email = binding.edEmail.text.toString().trim()
        val password = binding.edPassword.text.toString().trim()

        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", email)
            jsonObject.put("password", password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        AndroidNetworking.post("https://grocery-api.tonsu.site/auth/login")
            .addJSONObjectBody(jsonObject)
            .addHeaders("Content-Type", "application/json")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        Log.d(ContentValues.TAG, response.toString())
                        if (response.getString("success").equals("true")) {
                            Log.d("ini berhasil login", response.toString())
                            Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                            //sharetoken
                            val shareToken = response.getString("token")
                            val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
                            val editorToken = sharedPreference.edit()
                            editorToken.putString("token",shareToken)
                            editorToken.commit()

                        } else {
                            Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()
                            binding.verifikasiEmailPassword.text = response.getString("message")
                            binding.warning.visibility = View.VISIBLE
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@LoginActivity, "kesalahan", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                override fun onError(error: ANError) {
                    // handle error
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
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
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
    private fun alertDialogShow(){
        val loading = loadingDialog(this)
        loading.startLoading()

    }
}
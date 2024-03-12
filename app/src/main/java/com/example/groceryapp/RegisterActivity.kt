package com.example.groceryapp

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.groceryapp.databinding.ActivityRegisterBinding
import org.json.JSONException
import org.json.JSONObject


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.textLogin.setOnClickListener{
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {

            val nama = binding.edNama.text.toString().trim()
            val email = binding.edEmail.text.toString().trim()
            val password = binding.edPassword.text.toString().trim()
            val chekbox = binding.chekbox.isChecked

            //POST API
            val jsonObject = JSONObject()
            try {
                jsonObject.put("name", nama)
                jsonObject.put("email", email)
                jsonObject.put("password", password)
                jsonObject.put( "terms", if (chekbox) 1 else 0)

            }catch ( e: JSONException){
                e.printStackTrace()
            }

            AndroidNetworking.post("https://grocery-api.tonsu.site/auth/register")
                .addJSONObjectBody(jsonObject)
                .addHeaders("Content-Type", "application/json")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        try {
                            Log.d(ContentValues.TAG, response.toString())
                            if (response.getString("success").equals("true")) {
                                Toast.makeText(this@RegisterActivity, "input berhasil", Toast.LENGTH_SHORT).show()
                                val intent = Intent(applicationContext, LoginActivity::class.java)
                                startActivity(intent)
                                finish()

                            } else {

                                binding.verifikasiEmailPassword.text = response.getString("message")
                                binding.warning.visibility = View.VISIBLE
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(this@RegisterActivity, "kesalahan", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onError(error: ANError) {
                        // handle error
                    }
                })
        }


    }
}
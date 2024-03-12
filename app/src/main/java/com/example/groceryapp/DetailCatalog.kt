package com.example.groceryapp

import android.content.Context
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.groceryapp.databinding.ActivityDetailCatalogBinding
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class DetailCatalog : AppCompatActivity() {
    private lateinit var binding : ActivityDetailCatalogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.shimmerDetailCatalog.startShimmer()


        val id = this.intent.getStringExtra("id")?.toInt()
        val image = this.intent.getStringExtra("image")

        Log.d("image katalog", image.toString())

        Picasso.get()
            .load(image)
            .into(binding.imageDetailCatalog)

        binding.shimmerDetailCatalog.stopShimmer()
        binding.shimmerDetailCatalog.visibility = View.GONE
        binding.imageDetailCatalog.visibility = View.VISIBLE



    }

}
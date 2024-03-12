package com.example.groceryapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.groceryapp.databinding.ActivityTentangKamiBinding

class TentangKami : AppCompatActivity() {
    private lateinit var binding : ActivityTentangKamiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTentangKamiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backActivity.setOnClickListener {
            onBackPressed()
        }
    }
}
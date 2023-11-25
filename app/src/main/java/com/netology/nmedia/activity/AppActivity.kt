package com.netology.nmedia.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.netology.nmedia.databinding.ActivityAppBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}

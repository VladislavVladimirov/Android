package com.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.netology.MyApp.R
import com.netology.MyApp.databinding.ActivityEditPostBinding



class EditPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            with(binding.edit) {
                requestFocus()
                setText(text)
            }
            binding.cancelButton.setOnClickListener{
                val intent = Intent()
                setResult(Activity.RESULT_CANCELED,intent)
                finish()
            }
            binding.ok.setOnClickListener {
                val intent = Intent()
                if (binding.edit.text.isNullOrBlank()) {
                    setResult(Activity.RESULT_CANCELED, intent)
                } else {
                    val content = binding.edit.text.toString()
                    intent.putExtra(Intent.EXTRA_TEXT, content)
                    setResult(Activity.RESULT_OK, intent)
                }
                finish()
            }

        }
    }
}

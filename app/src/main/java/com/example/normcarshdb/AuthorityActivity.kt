package com.example.normcarshdb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.normcarshdb.databinding.ActivityAuthorityBinding

class AuthorityActivity : AppCompatActivity() {

    private lateinit var activityAuthorityBinding: ActivityAuthorityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAuthorityBinding = DataBindingUtil.setContentView(this, R.layout.activity_authority)

        activityAuthorityBinding.authorizeButton.setOnClickListener {
            checking()
        }
    }

    private fun checking() {
        if(activityAuthorityBinding.editText.text.toString() == "admin" &&
            activityAuthorityBinding.editText2.text.toString() == "12345"){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_LONG).show()
        }
    }
}
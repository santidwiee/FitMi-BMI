package com.example.apparisan.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.apparisan.R
import com.example.apparisan.auth.AuthActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //membuat splash selama 2 detik
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
            finish()
        }, 2000)
    }
}

package ca.unb.mobiledev.campuseventlist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val splashTimeOut: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Navigate to SelectSchoolActivity after splash timeout
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SelectSchoolActivity::class.java)
            startActivity(intent)
            finish()
        }, splashTimeOut)
    }
}


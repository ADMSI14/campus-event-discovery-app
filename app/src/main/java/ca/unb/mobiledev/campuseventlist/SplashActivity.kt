package ca.unb.mobiledev.campuseventlist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.campuseventlist.utils.PreferencesManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val splashTimeOut: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Check if there's a saved school
        val prefsManager = PreferencesManager.getInstance(this)
        
        // Navigate after splash timeout
        Handler(Looper.getMainLooper()).postDelayed({
            if (prefsManager.hasSavedSchool()) {
                // Navigate directly to UpcomingEventsActivity with saved school
                val savedSchoolId = prefsManager.getSavedSchoolId() ?: ""
                val savedSchoolName = prefsManager.getSavedSchoolName() ?: ""
                
                Log.d("SplashActivity", "Found saved school: $savedSchoolName (ID: $savedSchoolId)")
                
                val intent = Intent(this, UpcomingEventsActivity::class.java)
                intent.putExtra("SELECTED_SCHOOL_NAME", savedSchoolName)
                intent.putExtra("SELECTED_SCHOOL_ID", savedSchoolId)
                startActivity(intent)
            } else {
                // No saved school - go to SelectSchoolActivity
                Log.d("SplashActivity", "No saved school found - navigating to SelectSchoolActivity")
                val intent = Intent(this, SelectSchoolActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, splashTimeOut)
    }
}


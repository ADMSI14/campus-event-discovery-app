package ca.unb.mobiledev.campuseventlist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.campuseventlist.utils.PreferencesManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val changeSchoolButton: Button = findViewById(R.id.changeSchoolButton)
        val backButton: ImageView = findViewById(R.id.backButton)
        changeSchoolButton.setOnClickListener {
            val intent = Intent(this, SelectSchoolActivity::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        updateCurrentSchoolDisplay()
    }

    private fun updateCurrentSchoolDisplay() {
        val currentSchoolText: TextView = findViewById(R.id.currentSchoolText)
        val prefsManager = PreferencesManager.getInstance(this)
        val savedName = prefsManager.getSavedSchoolName()

        if (savedName != null) {
            currentSchoolText.text = "Current school: $savedName"
        }
        else {
            val savedId = prefsManager.getSavedSchoolId()
            if (savedId != null) {
                currentSchoolText.text = "Current school (ID): $savedId"
            } else {
                currentSchoolText.text = "Current school: None selected"
            }
        }
    }
}

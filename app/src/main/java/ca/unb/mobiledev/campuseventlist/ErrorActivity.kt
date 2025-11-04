package ca.unb.mobiledev.campuseventlist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ErrorActivity : AppCompatActivity() {
    
    private lateinit var schoolNameLabel: TextView
    private lateinit var returnButton: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
        
        // Get the school name that wasn't found
        val schoolName = intent.getStringExtra("SCHOOL_NAME") ?: "Unknown School"
        
        // Initialize views
        schoolNameLabel = findViewById(R.id.schoolNameLabel)
        returnButton = findViewById(R.id.returnButton)
        
        // Display the school name in the label
        schoolNameLabel.text = schoolName
        
        // Handle return to school selection
        returnButton.setOnClickListener {
            finish() // Goes back to SelectSchoolActivity
        }
    }
}


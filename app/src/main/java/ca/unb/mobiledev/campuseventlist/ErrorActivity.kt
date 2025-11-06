package ca.unb.mobiledev.campuseventlist

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ErrorActivity : AppCompatActivity() {
    
    private lateinit var schoolNameLabel: TextView
    private lateinit var selectedSchoolText: TextView
    private lateinit var notEnrolledText: TextView
    private lateinit var returnButton: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
        
        val schoolName = intent.getStringExtra("SCHOOL_NAME") ?: "Unknown"
        
        Log.d("ErrorActivity", "School not found: $schoolName")
        
        // Initialize views
        schoolNameLabel = findViewById(R.id.schoolNameLabel)
        selectedSchoolText = findViewById(R.id.selectedSchoolText)
        notEnrolledText = findViewById(R.id.notEnrolledText)
        returnButton = findViewById(R.id.returnButton)
        
        // Display the school name in the label
        schoolNameLabel.text = schoolName
        
        // Set school error text
        selectedSchoolText.text = getString(R.string.selected_school_text)
        notEnrolledText.text = getString(R.string.not_enrolled_text)
        
        // Handle return button - goes back to SelectSchoolActivity
        returnButton.setOnClickListener {
            Log.d("ErrorActivity", "Return button clicked")
            finish() // Goes back to SelectSchoolActivity
        }
    }
    
    override fun onBackPressed() {
        // Handle system back button - same as return button
        Log.d("ErrorActivity", "Back button pressed - returning to previous screen")
        super.onBackPressed()
    }
}


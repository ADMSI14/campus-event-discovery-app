package ca.unb.mobiledev.campuseventlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ErrorActivity : AppCompatActivity() {
    
    private lateinit var itemNameLabel: TextView
    private lateinit var selectedItemText: TextView
    private lateinit var notEnrolledText: TextView
    private lateinit var returnButton: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
        
        // Determine if this is a school error or event error
        val isEventError = intent.getBooleanExtra("IS_EVENT_ERROR", false)
        val itemName = intent.getStringExtra("SCHOOL_NAME") ?: "Unknown"
        
        Log.d("ErrorActivity", "Error type - isEventError: $isEventError, Item: $itemName")
        
        // Initialize views
        itemNameLabel = findViewById(R.id.schoolNameLabel)
        selectedItemText = findViewById(R.id.selectedSchoolText)
        notEnrolledText = findViewById(R.id.notEnrolledText)
        returnButton = findViewById(R.id.returnButton)
        
        // Display the item name in the label
        itemNameLabel.text = itemName
        
        // Update text based on error type
        if (isEventError) {
            // Event not found
            selectedItemText.text = "your selected event:"
            notEnrolledText.text = "Is not available in Uni-T"
        } else {
            // School not found (default behavior)
            selectedItemText.text = getString(R.string.selected_school_text)
            notEnrolledText.text = getString(R.string.not_enrolled_text)
        }
        
        // Handle return button - goes back to previous activity
        returnButton.setOnClickListener {
            Log.d("ErrorActivity", "Return button clicked")
            finish() // Goes back to SelectSchoolActivity or UpcomingEventsActivity
        }
    }
    
    override fun onBackPressed() {
        // Handle system back button - same as return button
        Log.d("ErrorActivity", "Back button pressed - returning to previous screen")
        super.onBackPressed()
    }
}


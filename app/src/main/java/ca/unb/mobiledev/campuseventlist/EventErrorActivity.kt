package ca.unb.mobiledev.campuseventlist

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EventErrorActivity : AppCompatActivity() {
    
    private lateinit var eventNameLabel: TextView
    private lateinit var backButton: ImageView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_error)
        
        val eventName = intent.getStringExtra("EVENT_NAME") ?: "Unknown"
        
        Log.d("EventErrorActivity", "Event not found: $eventName")
        
        // Initialize views
        eventNameLabel = findViewById(R.id.eventNameLabel)
        backButton = findViewById(R.id.backButton)
        
        // Display the event name in the label
        eventNameLabel.text = eventName
        
        // Handle back button - goes back to UpcomingEventsActivity
        backButton.setOnClickListener {
            Log.d("EventErrorActivity", "Back button clicked")
            finish() // Returns to UpcomingEventsActivity
        }
    }
    
    override fun onBackPressed() {
        // Handle system back button - same as back button
        Log.d("EventErrorActivity", "Back button pressed - returning to events screen")
        super.onBackPressed()
    }
}


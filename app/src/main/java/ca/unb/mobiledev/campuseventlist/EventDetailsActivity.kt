package ca.unb.mobiledev.campuseventlist

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EventDetailsActivity : AppCompatActivity() {
    
    private lateinit var eventNameLabel: TextView
    private lateinit var eventDateLabel: TextView
    private lateinit var eventLocationLabel: TextView
    private lateinit var eventDescriptionLabel: TextView
    private lateinit var viewOnMapButton: Button
    private lateinit var saveEventButton: Button
    private lateinit var backButton: ImageView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)
        
        Log.d("EventDetails", "onCreate started")
        
        // Initialize views
        initializeViews()
    }
    
    private fun initializeViews() {
        eventNameLabel = findViewById(R.id.eventNameLabel)
        eventDateLabel = findViewById(R.id.eventDateLabel)
        eventLocationLabel = findViewById(R.id.eventLocationLabel)
        eventDescriptionLabel = findViewById(R.id.eventDescriptionLabel)
        viewOnMapButton = findViewById(R.id.viewOnMapButton)
        saveEventButton = findViewById(R.id.saveEventButton)
        backButton = findViewById(R.id.backButton)
    }
}


package ca.unb.mobiledev.campuseventlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.campuseventlist.api.RetrofitClient
import ca.unb.mobiledev.campuseventlist.models.Event
import ca.unb.mobiledev.campuseventlist.models.SingleEventResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventDetailsActivity : AppCompatActivity() {
    
    private lateinit var eventNameLabel: TextView
    private lateinit var eventDateLabel: TextView
    private lateinit var eventLocationLabel: TextView
    private lateinit var eventDescriptionLabel: TextView
    private lateinit var viewOnMapButton: Button
    private lateinit var saveEventButton: Button
    private lateinit var backButton: ImageView
    
    private var eventId: String = ""
    private var currentEvent: Event? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)
        
        // Get event ID from intent
        eventId = intent.getStringExtra("SELECTED_EVENT_ID") ?: ""
        Log.d("EventDetails", "Event ID: $eventId")
        
        // Initialize views
        initializeViews()
        
        // Setup button listeners
        setupListeners()
        
        // Fetch event details
        fetchEventDetails()
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
    
    private fun setupListeners() {
        backButton.setOnClickListener {
            Log.d("EventDetails", "Back button clicked")
            finish() // Returns to UpcomingEventsActivity
        }
        
        viewOnMapButton.setOnClickListener {
            Log.d("EventDetails", "View on Map button clicked")
            navigateToMap()
        }
        
        saveEventButton.setOnClickListener {
            Log.d("EventDetails", "Save Event button clicked")
            saveEvent()
        }
    }
    
    private fun fetchEventDetails() {
        if (eventId.isEmpty()) {
            Log.e("EventDetails", "No event ID provided")
            return
        }
        
        RetrofitClient.apiService.getEventById(eventId).enqueue(object : Callback<SingleEventResponse> {
            override fun onResponse(
                call: Call<SingleEventResponse>,
                response: Response<SingleEventResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    currentEvent = response.body()!!.data
                    displayEventDetails(currentEvent!!)
                } else {
                    Log.e("EventDetails", "Failed to fetch event: ${response.code()}")
                }
            }
            
            override fun onFailure(call: Call<SingleEventResponse>, t: Throwable) {
                Log.e("EventDetails", "API call failed", t)
            }
        })
    }
    
    private fun displayEventDetails(event: Event) {
        eventNameLabel.text = event.name
        
        // Parse and display date
        val date = parseEventDate(event)
        eventDateLabel.text = "Date: $date"
        
        // Display location
        eventLocationLabel.text = "Location: ${formatLocation(event.location)}"
        
        // Display description
        eventDescriptionLabel.text = event.description
    }
    
    private fun parseEventDate(event: Event): String {
        // Try eventDateTime first (format: "2025-11-15T11:15:18.661510-04:00")
        event.eventDateTime?.let {
            try {
                val datePart = it.split('T')[0]
                return datePart
            } catch (e: Exception) {
                Log.e("EventDetails", "Error parsing eventDateTime: $it", e)
            }
        }
        
        // Fallback to createdAt (format: "2025-11-15")
        event.createdAt?.let {
            return it
        }
        
        return "Date not available"
    }
    
    private fun formatLocation(location: String): String {
        // For now, return a simplified version
        // In a real app, you might want to parse the POINT string and show a friendly address
        return if (location.contains("POINT")) {
            "See map for location"
        } else {
            location
        }
    }
    
    private fun navigateToMap() {
        currentEvent?.let { event ->
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("SELECTED_EVENT_ID", event.id)
            intent.putExtra("SELECTED_EVENT_NAME", event.name)
            intent.putExtra("SELECTED_EVENT_DESCRIPTION", event.description)
            intent.putExtra("SELECTED_EVENT_LOCATION", event.location)
            startActivity(intent)
        }
    }
    
    private fun saveEvent() {
        // TODO: Implement event persistence in next step
        Log.d("EventDetails", "Save event functionality to be implemented")
    }
}


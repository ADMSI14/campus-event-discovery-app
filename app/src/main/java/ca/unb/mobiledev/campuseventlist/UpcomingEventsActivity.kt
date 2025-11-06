package ca.unb.mobiledev.campuseventlist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.campuseventlist.api.RetrofitClient
import ca.unb.mobiledev.campuseventlist.models.Event
import ca.unb.mobiledev.campuseventlist.models.EventResponse
import ca.unb.mobiledev.campuseventlist.models.School
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingEventsActivity : AppCompatActivity() {
    
    private lateinit var selectedSchoolLabel: TextView
    private lateinit var eventsListView: ListView
    private lateinit var searchEventEditText: EditText
    private lateinit var searchEventIcon: ImageView
    private lateinit var loadingEventsContainer: android.view.View
    private lateinit var backButton: ImageView
    private lateinit var adapter: ArrayAdapter<String>
    
    private val allEvents = mutableListOf<Event>()
    private val eventNames = mutableListOf<String>()
    private var selectedSchoolName: String = ""
    private var selectedSchoolId: String = ""
    private var isDataLoaded = false
    private var isInitialLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upcoming_events)

        Log.d("UpcomingEvents", "onCreate started")

        // Get selected school data from intent
        selectedSchoolName = intent.getStringExtra("SELECTED_SCHOOL_NAME") ?: "Unknown School"
        selectedSchoolId = intent.getStringExtra("SELECTED_SCHOOL_ID") ?: ""
        
        Log.d("UpcomingEvents", "School: $selectedSchoolName, ID: $selectedSchoolId")

        // Initialize views
        selectedSchoolLabel = findViewById(R.id.selectedSchoolLabel)
        eventsListView = findViewById(R.id.eventsListView)
        searchEventEditText = findViewById(R.id.searchEventEditText)
        searchEventIcon = findViewById(R.id.searchEventIcon)
        loadingEventsContainer = findViewById(R.id.loadingEventsContainer)
        backButton = findViewById(R.id.backButton)

        Log.d("UpcomingEvents", "Views initialized")

        // Display selected school name in label
        selectedSchoolLabel.text = selectedSchoolName

        // Setup ListView adapter
        adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1
        )
        eventsListView.adapter = adapter

        Log.d("UpcomingEvents", "Adapter set to ListView")

        // Handle event selection from list
        eventsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedEvent = adapter.getItem(position)
            Log.d("UpcomingEvents", "Event clicked: $selectedEvent")
            selectedEvent?.let { validateEventAndNavigate(it) }
        }

        // Setup search functionality
        searchEventEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })

        // Handle search action (keyboard enter)
        searchEventEditText.setOnEditorActionListener { _, _, _ ->
            performEventSearch()
            true
        }
        
        // Handle search icon click
        searchEventIcon.setOnClickListener {
            Log.d("UpcomingEvents", "Search icon clicked")
            performEventSearch()
        }
        
        // Handle back button click
        backButton.setOnClickListener {
            Log.d("UpcomingEvents", "Back button clicked")
            finish() // Return to SelectSchoolActivity
        }

        Log.d("UpcomingEvents", "Setup complete")
        
        // Show loading and disable search, then fetch events
        showLoading()
        searchEventEditText.isEnabled = false
        searchEventIcon.isEnabled = false
        fetchEvents()
    }
    
    private fun showLoading() {
        loadingEventsContainer.visibility = android.view.View.VISIBLE
        eventsListView.visibility = android.view.View.GONE
        Log.d("UpcomingEvents", "Loading indicator shown")
    }
    
    private fun hideLoading() {
        loadingEventsContainer.visibility = android.view.View.GONE
        eventsListView.visibility = android.view.View.VISIBLE
        searchEventEditText.isEnabled = true
        searchEventIcon.isEnabled = true
        Log.d("UpcomingEvents", "Loading indicator hidden")
    }
    
    // Fetch events from API for selected school
    private fun fetchEvents() {
        if (selectedSchoolId.isEmpty()) {
            Log.e("UpcomingEvents", "No school ID provided, loading fallback data")
            loadFallbackEvents()
            return
        }
        
        Log.d("UpcomingEvents", "Fetching events for school ID: $selectedSchoolId")
        
        RetrofitClient.apiService.getEventsBySchool(selectedSchoolId).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                Log.d("UpcomingEvents", "Response received: ${response.code()}")
                
                if (response.isSuccessful) {
                    response.body()?.let { eventResponse ->
                        Log.d("UpcomingEvents", "Events received: ${eventResponse.data.size}")
                        
                        allEvents.clear()
                        allEvents.addAll(eventResponse.data)
                        
                        val newEventNames = allEvents.map { it.name }
                        Log.d("UpcomingEvents", "Event names to add: $newEventNames")
                        
                        // Update adapter on UI thread
                        runOnUiThread {
                            try {
                                Log.d("UpcomingEvents", "Updating adapter with ${newEventNames.size} events")
                                
                                adapter.clear()
                                adapter.addAll(newEventNames)
                                
                                eventNames.clear()
                                eventNames.addAll(newEventNames)
                                
                                isDataLoaded = true
                                hideLoading()
                                
                                Log.d("UpcomingEvents", "Adapter updated. Count: ${adapter.count}")
                                
                                Toast.makeText(
                                    this@UpcomingEventsActivity,
                                    "Loaded ${adapter.count} event(s)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Log.e("UpcomingEvents", "Error updating adapter", e)
                                hideLoading()
                                Toast.makeText(
                                    this@UpcomingEventsActivity,
                                    "Error: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } ?: run {
                        runOnUiThread {
                            hideLoading()
                            Log.e("UpcomingEvents", "Response body is null")
                            Toast.makeText(
                                this@UpcomingEventsActivity,
                                "No data received from server",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Log.e("UpcomingEvents", "Failed response: ${response.code()}")
                        loadFallbackEvents()
                        Toast.makeText(
                            this@UpcomingEventsActivity,
                            "Using offline data (API error: ${response.code()})",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                runOnUiThread {
                    Log.e("UpcomingEvents", "API call failed", t)
                    loadFallbackEvents()
                    Toast.makeText(
                        this@UpcomingEventsActivity,
                        "Using offline data (API unavailable)",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }
    
    // Load fallback event data when API fails
    private fun loadFallbackEvents() {
        Log.d("UpcomingEvents", "Loading fallback event data")
        
        // Create test events for the selected school
        val school = School(selectedSchoolId, selectedSchoolName)
        
        val event1 = Event(
            "event-1", 
            school, 
            "UNB Residence Orientation", 
            "Welcome to UNB",
            "SRID=4326;POINT (-66.46689684501543 45.848150283597036)"
        )
        val event2 = Event(
            "event-2",
            school,
            "Halloween at The Cellar",
            "Halloween party at The Cellar",
            "SRID=4326;POINT (-0.0450959801673889 0.0147347150608942)"
        )
        
        allEvents.clear()
        allEvents.addAll(listOf(event1, event2))
        
        val newEventNames = allEvents.map { it.name }
        
        adapter.clear()
        adapter.addAll(newEventNames)
        
        eventNames.clear()
        eventNames.addAll(newEventNames)
        
        isDataLoaded = true
        hideLoading()
        
        Log.d("UpcomingEvents", "Fallback events loaded. Adapter count: ${adapter.count}")
    }
    
    // Perform event search
    private fun performEventSearch() {
        val searchedEvent = searchEventEditText.text.toString().trim()
        if (searchedEvent.isNotEmpty()) {
            Log.d("UpcomingEvents", "Event search submitted: $searchedEvent")
            validateEventAndNavigate(searchedEvent)
        } else {
            Toast.makeText(this, "Please enter an event name", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onResume() {
        super.onResume()
        Log.d("UpcomingEvents", "onResume called. isInitialLoad: $isInitialLoad, Adapter count: ${adapter.count}")
        
        // Skip onResume logic during initial load
        if (isInitialLoad) {
            Log.d("UpcomingEvents", "Initial load - skipping onResume logic")
            isInitialLoad = false
            return
        }
        
        // Clear search bar when returning from ErrorActivity
        searchEventEditText.setText("")
        Log.d("UpcomingEvents", "Search bar cleared")
        
        // Refresh adapter if data was already loaded
        if (isDataLoaded && adapter.count == 0 && allEvents.isNotEmpty()) {
            Log.d("UpcomingEvents", "Repopulating adapter with ${allEvents.size} events")
            adapter.clear()
            adapter.addAll(allEvents.map { it.name })
            hideLoading()
        } else if (isDataLoaded && adapter.count > 0) {
            Log.d("UpcomingEvents", "Data already displayed, ensuring UI is ready")
            hideLoading()
        }
    }
    
    // Validate event and navigate
    private fun validateEventAndNavigate(eventName: String) {
        Log.d("UpcomingEvents", "Validating event: $eventName")
        // Placeholder for now - will be implemented in Step 9
        Toast.makeText(this, "Event validation not implemented yet", Toast.LENGTH_SHORT).show()
    }
}


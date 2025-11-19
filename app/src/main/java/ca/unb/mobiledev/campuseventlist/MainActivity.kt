package ca.unb.mobiledev.campuseventlist

import android.content.Intent
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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ca.unb.mobiledev.campuseventlist.api.RetrofitClient
import ca.unb.mobiledev.campuseventlist.models.Event
import ca.unb.mobiledev.campuseventlist.models.EventResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

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

                        // Handle empty events list (school has no events)
                        if (eventResponse.data.isEmpty()) {
                            runOnUiThread {
                                allEvents.clear()
                                eventNames.clear()
                                adapter.clear()
                                isDataLoaded = true
                                hideLoading()
                                Toast.makeText(
                                    this@MainActivity,
                                    "No events found for this school",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            return
                        }

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
                                    this@MainActivity,
                                    "Loaded ${adapter.count} event(s)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Log.e("UpcomingEvents", "Error updating adapter", e)
                                hideLoading()
                                Toast.makeText(
                                    this@MainActivity,
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
                                this@MainActivity,
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
                            this@MainActivity,
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
                        this@MainActivity,
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
        val event1 = Event(
            "event-1",
            selectedSchoolId,
            "UNB Residence Orientation",
            "Welcome to UNB",
            "SRID=4326;POINT (-66.46689684501543 45.848150283597036)",
            "2025-11-15",
            "2025-11-15T11:15:18.661510-04:00"
        )
        val event2 = Event(
            "event-2",
            selectedSchoolId,
            "Halloween at The Cellar",
            "Halloween party at The Cellar",
            "SRID=4326;POINT (-0.0450959801673889 0.0147347150608942)",
            "2025-11-15",
            "2025-11-15T11:15:18.661510-04:00"
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

        // Clear search bar and filter when returning from EventErrorActivity
        searchEventEditText.setText("")
        adapter.filter.filter("") // Clear any active filter
        Log.d("UpcomingEvents", "Search bar and filter cleared")

        // Ensure loading indicator is hidden
        hideLoading()
    }

    // Validate event and navigate to MainActivity or ErrorActivity
    private fun validateEventAndNavigate(eventName: String) {
        Log.d("UpcomingEvents", "Validating event: $eventName")

        // Check if we have loaded events data
        if (!isDataLoaded) {
            Toast.makeText(
                this,
                "Please wait for events to load",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // If events list is empty, any search should go to EventErrorActivity
        if (allEvents.isEmpty()) {
            Log.d("UpcomingEvents", "No events available - Navigating to EventErrorActivity")
            val intent = Intent(this, EventErrorActivity::class.java)
            intent.putExtra("EVENT_NAME", eventName)
            startActivity(intent)
            return
        }

        Log.d("UpcomingEvents", "Available events: ${allEvents.map { it.name }}")

        // Find the selected event object
        val selectedEvent = allEvents.find {
            it.name.equals(eventName, ignoreCase = true)
        }

        if (selectedEvent != null) {
            // Event exists - navigate to MainActivity
            Log.d("UpcomingEvents", "Event found! ID: ${selectedEvent.id}, Name: ${selectedEvent.name}")
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("SELECTED_SCHOOL_NAME", selectedSchoolName)
            intent.putExtra("SELECTED_SCHOOL_ID", selectedSchoolId)
            intent.putExtra("SELECTED_EVENT_ID", selectedEvent.id)
            intent.putExtra("SELECTED_EVENT_NAME", selectedEvent.name)
            intent.putExtra("SELECTED_EVENT_DESCRIPTION", selectedEvent.description)
            intent.putExtra("SELECTED_EVENT_LOCATION", selectedEvent.location)
            startActivity(intent)
            finish()
        } else {
            // Event doesn't exist - navigate to EventErrorActivity
            Log.d("UpcomingEvents", "Event not found! Navigating to EventErrorActivity")
            val intent = Intent(this, EventErrorActivity::class.java)
            intent.putExtra("EVENT_NAME", eventName)
            startActivity(intent)
            // Don't finish - keep UpcomingEventsActivity in back stack
        }
    }

    override fun onBackPressed() {
        // Handle back button press
        Log.d("UpcomingEvents", "Back button pressed - returning to SelectSchoolActivity")
        super.onBackPressed()
    }
}


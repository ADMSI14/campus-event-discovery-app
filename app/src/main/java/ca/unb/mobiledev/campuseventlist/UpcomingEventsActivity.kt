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
import ca.unb.mobiledev.campuseventlist.models.Event

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
    
    // Validate event and navigate
    private fun validateEventAndNavigate(eventName: String) {
        Log.d("UpcomingEvents", "Validating event: $eventName")
        // Placeholder for now - will be implemented in Step 9
        Toast.makeText(this, "Event validation not implemented yet", Toast.LENGTH_SHORT).show()
    }
}


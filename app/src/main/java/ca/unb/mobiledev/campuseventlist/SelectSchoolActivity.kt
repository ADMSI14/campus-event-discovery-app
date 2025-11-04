package ca.unb.mobiledev.campuseventlist

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.campuseventlist.api.RetrofitClient
import ca.unb.mobiledev.campuseventlist.models.School
import ca.unb.mobiledev.campuseventlist.models.SchoolResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectSchoolActivity : AppCompatActivity() {
    
    private lateinit var schoolListView: ListView
    private lateinit var searchEditText: EditText
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var loadingContainer: android.view.View
    private lateinit var searchIcon: android.widget.ImageView
    
    private val allSchools = mutableListOf<School>()
    private val schoolNames = mutableListOf<String>()
    private var isDataLoaded = false
    private var isInitialLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_school)

        Log.d("SelectSchool", "onCreate started")

        // Initialize views
        schoolListView = findViewById(R.id.schoolListView)
        searchEditText = findViewById(R.id.searchEditText)
        loadingContainer = findViewById(R.id.loadingContainer)
        searchIcon = findViewById(R.id.searchIcon)

        Log.d("SelectSchool", "Views initialized")

        // Setup ListView adapter WITHOUT backing list
        adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1
        )
        schoolListView.adapter = adapter

        Log.d("SelectSchool", "Adapter set to ListView (no backing list)")
        Log.d("SelectSchool", "ListView visibility: ${schoolListView.visibility}")
        Log.d("SelectSchool", "ListView height: ${schoolListView.height}")
        Log.d("SelectSchool", "ListView parent: ${schoolListView.parent}")

        // Handle school selection from list
        schoolListView.setOnItemClickListener { _, _, position, _ ->
            val selectedSchool = adapter.getItem(position)
            Log.d("SelectSchool", "Item clicked: $selectedSchool")
            selectedSchool?.let { validateAndNavigate(it) }
        }

        // Setup search functionality
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })

        // Handle search action (when user presses enter/search on keyboard)
        searchEditText.setOnEditorActionListener { _, _, _ ->
            performSearch()
            true
        }
        
        // Handle search icon click
        searchIcon.setOnClickListener {
            Log.d("SelectSchool", "Search icon clicked")
            performSearch()
        }
        
        // Show loading, disable search, and fetch schools
        showLoading()
        searchEditText.isEnabled = false
        searchIcon.isEnabled = false
        fetchSchools()
    }
    
    // Perform search validation and navigation
    private fun performSearch() {
        val searchedSchool = searchEditText.text.toString().trim()
        if (searchedSchool.isNotEmpty()) {
            Log.d("SelectSchool", "Search submitted: $searchedSchool")
            validateAndNavigate(searchedSchool)
        } else {
            Toast.makeText(this, "Please enter a school name", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showLoading() {
        loadingContainer.visibility = android.view.View.VISIBLE
        schoolListView.visibility = android.view.View.GONE
        Log.d("SelectSchool", "Loading indicator shown")
    }
    
    private fun hideLoading() {
        loadingContainer.visibility = android.view.View.GONE
        schoolListView.visibility = android.view.View.VISIBLE
        searchEditText.isEnabled = true
        searchIcon.isEnabled = true
        Log.d("SelectSchool", "Loading indicator hidden")
    }

    override fun onResume() {
        super.onResume()
        Log.d("SelectSchool", "onResume called. isInitialLoad: $isInitialLoad, isDataLoaded: $isDataLoaded, Adapter count: ${adapter.count}")
        
        // Skip onResume logic during initial load (first time activity is created)
        if (isInitialLoad) {
            Log.d("SelectSchool", "Initial load - skipping onResume logic")
            isInitialLoad = false
            return
        }
        
        // Clear the search bar when returning from ErrorActivity
        searchEditText.setText("")
        Log.d("SelectSchool", "Search bar cleared")
        
        // Refresh if data was already loaded (coming back from ErrorActivity)
        if (isDataLoaded && adapter.count == 0 && allSchools.isNotEmpty()) {
            Log.d("SelectSchool", "Repopulating adapter with ${allSchools.size} schools")
            adapter.clear()
            adapter.addAll(allSchools.map { it.name })
            hideLoading()
        } else if (isDataLoaded && adapter.count > 0) {
            Log.d("SelectSchool", "Data already displayed, ensuring UI is ready")
            hideLoading()
        }
    }

    // Fetch schools from API
    private fun fetchSchools() {
        Log.d("SelectSchool", "Fetching schools from API...")
        
        RetrofitClient.apiService.getSchools().enqueue(object : Callback<SchoolResponse> {
            override fun onResponse(call: Call<SchoolResponse>, response: Response<SchoolResponse>) {
                Log.d("SelectSchool", "Response received: ${response.code()}")
                
                if (response.isSuccessful) {
                    response.body()?.let { schoolResponse ->
                        Log.d("SelectSchool", "Schools received: ${schoolResponse.data.size}")
                        
                        allSchools.clear()
                        allSchools.addAll(schoolResponse.data)
                        
                        val newSchoolNames = allSchools.map { it.name }
                        Log.d("SelectSchool", "School names to add: $newSchoolNames")
                        
                        // Update adapter on UI thread
                        runOnUiThread {
                            try {
                                Log.d("SelectSchool", "Updating adapter with ${newSchoolNames.size} schools")
                                
                                // Clear adapter first
                                adapter.clear()
                                
                                // Add all schools to adapter
                                adapter.addAll(newSchoolNames)
                                
                                // Update the backing list for search validation
                                schoolNames.clear()
                                schoolNames.addAll(newSchoolNames)
                                
                                isDataLoaded = true
                                
                                // Hide loading and show list
                                hideLoading()
                                
                                Log.d("SelectSchool", "Adapter updated successfully!")
                                Log.d("SelectSchool", "Adapter count: ${adapter.count}")
                                
                                Toast.makeText(
                                    this@SelectSchoolActivity,
                                    "Loaded ${adapter.count} school(s)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Log.e("SelectSchool", "Error updating adapter", e)
                                hideLoading()
                                Toast.makeText(
                                    this@SelectSchoolActivity,
                                    "Error: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } ?: run {
                        runOnUiThread {
                            hideLoading()
                            Log.e("SelectSchool", "Response body is null")
                            Toast.makeText(
                                this@SelectSchoolActivity,
                                "No data received from server",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        hideLoading()
                        Log.e("SelectSchool", "Failed response: ${response.code()} - ${response.message()}")
                        Toast.makeText(
                            this@SelectSchoolActivity,
                            "Failed to load schools: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<SchoolResponse>, t: Throwable) {
                runOnUiThread {
                    hideLoading()
                    Log.e("SelectSchool", "API call failed", t)
                    Toast.makeText(
                        this@SelectSchoolActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    // Check if school exists in API data and navigate accordingly
    private fun validateAndNavigate(schoolName: String) {
        Log.d("SelectSchool", "Validating school: $schoolName")
        Log.d("SelectSchool", "Available schools: ${allSchools.map { it.name }}")
        
        val schoolExists = allSchools.any { 
            it.name.equals(schoolName, ignoreCase = true) 
        }

        if (schoolExists) {
            // School exists - navigate to MainActivity
            Log.d("SelectSchool", "School found! Navigating to MainActivity")
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("SELECTED_SCHOOL", schoolName)
            startActivity(intent)
            finish()
        } else {
            // School doesn't exist - navigate to ErrorActivity
            Log.d("SelectSchool", "School not found! Navigating to ErrorActivity")
            val intent = Intent(this, ErrorActivity::class.java)
            intent.putExtra("SCHOOL_NAME", schoolName)
            startActivity(intent)
            // Don't finish - keep SelectSchoolActivity in back stack
        }
    }
}

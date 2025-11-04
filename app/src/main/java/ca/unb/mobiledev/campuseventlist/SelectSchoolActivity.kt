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
    
    private val allSchools = mutableListOf<School>()
    private val schoolNames = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_school)

        // Initialize views
        schoolListView = findViewById(R.id.schoolListView)
        searchEditText = findViewById(R.id.searchEditText)

        // Setup ListView adapter
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            schoolNames
        )
        schoolListView.adapter = adapter

        // Handle school selection from list
        schoolListView.setOnItemClickListener { _, _, position, _ ->
            val selectedSchool = adapter.getItem(position)
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
            val searchedSchool = searchEditText.text.toString().trim()
            if (searchedSchool.isNotEmpty()) {
                validateAndNavigate(searchedSchool)
            }
            true
        }

        // Fetch schools from API
        fetchSchools()
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
                        
                        schoolNames.clear()
                        schoolNames.addAll(allSchools.map { it.name })
                        
                        Log.d("SelectSchool", "School names: $schoolNames")
                        
                        adapter.notifyDataSetChanged()
                        
                        Toast.makeText(
                            this@SelectSchoolActivity,
                            "Loaded ${schoolNames.size} schools",
                            Toast.LENGTH_SHORT
                        ).show()
                    } ?: run {
                        Log.e("SelectSchool", "Response body is null")
                        Toast.makeText(
                            this@SelectSchoolActivity,
                            "No data received from server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("SelectSchool", "Failed response: ${response.code()} - ${response.message()}")
                    Toast.makeText(
                        this@SelectSchoolActivity,
                        "Failed to load schools: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<SchoolResponse>, t: Throwable) {
                Log.e("SelectSchool", "API call failed", t)
                Toast.makeText(
                    this@SelectSchoolActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    // Check if school exists in API data and navigate accordingly
    private fun validateAndNavigate(schoolName: String) {
        val schoolExists = allSchools.any { 
            it.name.equals(schoolName, ignoreCase = true) 
        }

        if (schoolExists) {
            // School exists - navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("SELECTED_SCHOOL", schoolName)
            startActivity(intent)
            finish()
        } else {
            // School doesn't exist - navigate to ErrorActivity
            val intent = Intent(this, ErrorActivity::class.java)
            intent.putExtra("SCHOOL_NAME", schoolName)
            startActivity(intent)
        }
    }
}

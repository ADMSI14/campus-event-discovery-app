package ca.unb.mobiledev.campuseventlist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ca.unb.mobiledev.campuseventlist.api.RetrofitClient
import ca.unb.mobiledev.campuseventlist.models.EventResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private var eventLocation: LatLng? = null

    private lateinit var eventId: String

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get Event ID from Intent
        eventId = intent.getStringExtra("SELECTED_EVENT_ID") ?: ""
        Log.d(TAG, "Event ID: $eventId")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermissions()
    }

    // Permissions
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                lastLocation
            } else {
                showToast("Location permission denied")
            }
        }

    private fun checkLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                lastLocation
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                requestLocationPermissions()
            }
            else -> {
                requestLocationPermissions()
            }
        }
    }

    private val isLocationEnabled: Boolean
        get() {
            val lm = getSystemService(LOCATION_SERVICE) as LocationManager
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

    private fun requestLocationPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Get last location
    private val lastLocation: Unit
        @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
        get() {
            if (isLocationEnabled) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            currentLocation = it
                            fetchEventCoordinates()
                        } ?: run {
                            showToast("Unable to fetch location")
                        }
                    }
            } else {
                showToast("Please turn location services on")
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }

    // ------------------------
    // FETCH EVENT BY ID
    // ------------------------
    private fun fetchEventCoordinates() {
        if (eventId.isEmpty()) {
            showToast("No event ID provided")
            return
        }

        RetrofitClient.apiService.getEventsById(eventId)
            .enqueue(object : Callback<EventResponse> {
                override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val events = response.body()!!.data
                        if (events.isNotEmpty()) {
                            val event = events[0]
                            eventLocation = parsePointString(event.location)
                        }
                        openMap()
                    } else {
                        showToast("Failed to fetch event data")
                        openMap()
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    showToast("API failure: ${t.message}")
                    openMap()
                }
            })
    }

    private fun parsePointString(point: String?): LatLng? {
        if (point.isNullOrEmpty()) return null
        return try {
            val cleaned = point.substringAfter("POINT").replace("(", "").replace(")", "").trim()
            val parts = cleaned.split(" ")
            if (parts.size != 2) return null
            val lon = parts[0].toDouble()
            val lat = parts[1].toDouble()
            LatLng(lat, lon)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse POINT string: '$point'", e)
            null
        }
    }

    // ------------------------
    // MAP
    // ------------------------
    private fun openMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.main) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        val boundsBuilder = LatLngBounds.Builder()

        val userLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        map.addMarker(MarkerOptions().position(userLatLng).title("You are here"))
        boundsBuilder.include(userLatLng)

        eventLocation?.let {
            map.addMarker(MarkerOptions().position(it).title("Event Location"))
            boundsBuilder.include(it)
        }

        try {
            val bounds = boundsBuilder.build()
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))
        } catch (_: Exception) {}
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MapActivity"
    }
}

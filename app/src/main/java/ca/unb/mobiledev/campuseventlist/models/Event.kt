package ca.unb.mobiledev.campuseventlist.models

import com.google.gson.annotations.SerializedName

// Data class for a single event
data class Event(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("school")
    val school: String,  // School ID as string
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("location")
    val location: String,
    
    @SerializedName("created_at")
    val createdAt: String? = null,  // Event creation date in format "2025-11-15"
    
    @SerializedName("event_date_time")
    val eventDateTime: String? = null  // Event date and time in format "2025-11-15T11:15:18.661510-04:00"
)


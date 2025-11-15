package ca.unb.mobiledev.campuseventlist.models

import com.google.gson.annotations.SerializedName

// Data class for a single event
data class Event(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("school")
    val school: School,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("location")
    val location: String,
    
    @SerializedName("date")
    val date: String? = null  // Event date in format "2025-10-12"
)


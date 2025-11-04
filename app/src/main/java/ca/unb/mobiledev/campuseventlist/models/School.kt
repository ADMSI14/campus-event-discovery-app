package ca.unb.mobiledev.campuseventlist.models

import com.google.gson.annotations.SerializedName

/**
 * Data class representing a single school
 * Matches the school object structure from the API
 */
data class School(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String
)


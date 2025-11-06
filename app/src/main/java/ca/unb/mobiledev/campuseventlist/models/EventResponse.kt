package ca.unb.mobiledev.campuseventlist.models

import com.google.gson.annotations.SerializedName

// Wrapper class for the API response
data class EventResponse(
    @SerializedName("data")
    val data: List<Event>
)


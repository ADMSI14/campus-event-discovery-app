package ca.unb.mobiledev.campuseventlist.models

import com.google.gson.annotations.SerializedName

// Wrapper class for single event API response
// API returns: {"data": {...}} where data is a single Event object
data class SingleEventResponse(
    @SerializedName("data")
    val data: Event
)


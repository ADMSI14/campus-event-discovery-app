package ca.unb.mobiledev.campuseventlist.models

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the API response wrapper
 * The API returns: {"data": [...]}
 */
data class SchoolResponse(
    @SerializedName("data")
    val data: List<School>
)


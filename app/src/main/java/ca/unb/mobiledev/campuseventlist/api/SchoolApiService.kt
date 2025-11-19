package ca.unb.mobiledev.campuseventlist.api

import ca.unb.mobiledev.campuseventlist.models.EventResponse
import ca.unb.mobiledev.campuseventlist.models.SchoolResponse
import ca.unb.mobiledev.campuseventlist.models.SingleEventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

// API service to fetch schools and events from the backend
interface SchoolApiService {
    
    // GET request to retrieve list of schools
    @GET("v1/blog/schools/")
    fun getSchools(): Call<SchoolResponse>
    
    // GET request to retrieve events for a specific school
    @GET("v1/blog/events/school/{schoolId}")
    fun getEventsBySchool(@Path("schoolId") schoolId: String): Call<EventResponse>

    // GET request to retrieve single event details (returns SingleEventResponse)
    // API endpoint: /v1/blog/events/{eventId} returns {"data": {...}} format
    @GET("v1/blog/events/{eventId}")
    fun getEventById(@Path("eventId") eventId: String): Call<SingleEventResponse>
}


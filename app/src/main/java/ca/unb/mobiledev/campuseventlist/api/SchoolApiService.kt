package ca.unb.mobiledev.campuseventlist.api

import ca.unb.mobiledev.campuseventlist.models.EventResponse
import ca.unb.mobiledev.campuseventlist.models.SchoolResponse
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

    // GET request to retrieve event details
    @GET("v1/blog/events/{eventId}")
    fun getEventsById(@Path("eventId") eventId: String): Call<EventResponse>
}


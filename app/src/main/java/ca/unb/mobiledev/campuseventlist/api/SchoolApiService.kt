package ca.unb.mobiledev.campuseventlist.api

import ca.unb.mobiledev.campuseventlist.models.SchoolResponse
import retrofit2.Call
import retrofit2.http.GET

// API service to fetch schools from the backend
interface SchoolApiService {
    
    // GET request to retrieve list of schools
    @GET("v1/blog/schools/")
    fun getSchools(): Call<SchoolResponse>
}


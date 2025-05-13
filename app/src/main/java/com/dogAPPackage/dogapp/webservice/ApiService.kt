package com.dogAPPackage.dogapp.webservice
import com.dogAPPackage.dogapp.model.AppointmentModelResponse
import com.dogAPPackage.dogapp.utils.Constants.END_POINT
import retrofit2.http.GET

interface ApiService {
    @GET(END_POINT)
    suspend fun getProducts(): MutableList<AppointmentModelResponse>
}
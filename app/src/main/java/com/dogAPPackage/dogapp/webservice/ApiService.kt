package com.dogAPPackage.dogapp.webservice
import com.dogAPPackage.dogapp.model.AppointmentModelResponse
import com.dogAPPackage.dogapp.model.DogBreedsResponse
import com.dogAPPackage.dogapp.utils.Constants.END_POINT
import com.dogAPPackage.dogapp.model.DogImageResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET(END_POINT)
    suspend fun getProducts(): MutableList<AppointmentModelResponse>

    @GET("breeds/list/all")
    suspend fun getAllBreeds(): DogBreedsResponse

    @GET("breed/{breed}/images/random")
    suspend fun getRandomImageByBreed(@Path("breed") breed: String): DogImageResponse
}
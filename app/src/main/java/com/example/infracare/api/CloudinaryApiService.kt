package com.example.infracare.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CloudinaryApiService {
    @Multipart
    @POST("v1_1/{cloud_name}/upload")
    fun uploadImage(
        @Path("cloud_name") cloudName: String,
        @Query("upload_preset") uploadPreset: String,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>
}


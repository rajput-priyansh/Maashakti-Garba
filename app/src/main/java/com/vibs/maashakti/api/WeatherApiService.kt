package com.vibs.maashakti.api

import androidx.lifecycle.LiveData
import com.vibs.maashakti.api.models.ResponseCreateCard
import com.vibs.maashakti.api.models.ResponsePlayerPass
import com.vibs.maashakti.api.models.ResponseVerifyUser
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.http.*

/**
 * REST API access points
 */
interface WeatherApiService {

    @Multipart
    @POST("user_register_manually")
    fun postCreateCardData(@PartMap params: Map<String, @JvmSuppressWildcards RequestBody>, @Part file: MultipartBody.Part): LiveData<ApiResponse<ResponseCreateCard>>?


    @Multipart
    @POST("user_verify_online")
    fun postVerifyCardData(@PartMap params: Map<String, @JvmSuppressWildcards RequestBody>): LiveData<ApiResponse<ResponseVerifyUser>>?

    @Multipart
    @POST("delete_pass_details")
    fun postRemoveCardData(@PartMap params: Map<String, @JvmSuppressWildcards RequestBody>): LiveData<ApiResponse<ResponseCreateCard>>?

    @GET("list_passes")
    fun getPassList(): LiveData<ApiResponse<ResponsePlayerPass>>?

}

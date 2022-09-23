package com.vibs.maashakti

import androidx.lifecycle.LiveData
import com.vibs.maashakti.api.*
import com.vibs.maashakti.api.models.ResponseCreateCard
import com.vibs.maashakti.api.models.ResponsePlayerPass
import com.vibs.maashakti.api.models.ResponseVerifyUser
import kotlinx.coroutines.CoroutineScope
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CardRepository (val coroutineScope: CoroutineScope) {

    private val restApiService: WeatherApiService? = RetrofitApiService(
        WeatherApiService::class.java
    ).getInterface()

    fun makePostCreateCardData(map: Map<String, RequestBody>, image : MultipartBody.Part): LiveData<Resource<ResponseCreateCard>> {
        return object : NetworkBoundResource<ResponseCreateCard>(coroutineScope) {
            override fun createCall(): LiveData<ApiResponse<ResponseCreateCard>>? {
                return restApiService?.postCreateCardData(map, image)
            }
        }.asLiveData()
    }

    fun makeVerifyCardData(map: Map<String, RequestBody>): LiveData<Resource<ResponseVerifyUser>> {
        return object : NetworkBoundResource<ResponseVerifyUser>(coroutineScope) {
            override fun createCall(): LiveData<ApiResponse<ResponseVerifyUser>>? {
                return restApiService?.postVerifyCardData(map)
            }
        }.asLiveData()
    }

    fun postRemoveCardData(map: Map<String, RequestBody>): LiveData<Resource<ResponseCreateCard>> {
        return object : NetworkBoundResource<ResponseCreateCard>(coroutineScope) {
            override fun createCall(): LiveData<ApiResponse<ResponseCreateCard>>? {
                return restApiService?.postRemoveCardData(map)
            }
        }.asLiveData()
    }

    fun getPassList(): LiveData<Resource<ResponsePlayerPass>> {
        return object : NetworkBoundResource<ResponsePlayerPass>(coroutineScope) {
            override fun createCall(): LiveData<ApiResponse<ResponsePlayerPass>>? {
                return restApiService?.getPassList()
            }
        }.asLiveData()
    }
}
package com.vibs.maashakti

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibs.maashakti.api.Resource
import com.vibs.maashakti.api.models.ResponseCreateCard
import com.vibs.maashakti.api.models.ResponsePlayerPass
import com.vibs.maashakti.api.models.ResponseVerifyUser
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CardViewModel : ViewModel() {
    private val authRepository = CardRepository(viewModelScope)

    private val _cardResponse = MutableLiveData<ResponseCreateCard?>()
    private val _cardVerifyResponse = MutableLiveData<ResponseVerifyUser?>()
    private val _cardList = MutableLiveData<ResponsePlayerPass?>()


    val cardResponse : LiveData<ResponseCreateCard?> = _cardResponse
    val cardVerifyResponse : LiveData<ResponseVerifyUser?> = _cardVerifyResponse
    val cardList : LiveData<ResponsePlayerPass?> = _cardList

    fun setCardResponseData(data: ResponseCreateCard?) {
        _cardResponse.value = data
    }

    fun setCardVerifyResponseData(data: ResponseVerifyUser?) {
        _cardVerifyResponse.value = data
    }

    fun setCardListResponse(data: ResponsePlayerPass?) {
        _cardList.value = data
    }

    fun postCreateCardData(map : Map<String, RequestBody>, image : MultipartBody.Part): LiveData<Resource<ResponseCreateCard>> =
        authRepository.makePostCreateCardData(map, image)

    fun makeVerifyCardData(map : Map<String, RequestBody>): LiveData<Resource<ResponseVerifyUser>> =
        authRepository.makeVerifyCardData(map)

    fun postRemoveCardData(map : Map<String, RequestBody>): LiveData<Resource<ResponseCreateCard>> =
        authRepository.postRemoveCardData(map)

    fun getPassList(): LiveData<Resource<ResponsePlayerPass>> =
        authRepository.getPassList()

}
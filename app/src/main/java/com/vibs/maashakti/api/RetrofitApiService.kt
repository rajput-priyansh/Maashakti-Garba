package com.vibs.maashakti.api

/**
 * A Retrofit API interface
 * @param restService the actual interface which as the endpoints
</BnbRestService> */
class RetrofitApiService<BnbRestService>(private val restService: Class<BnbRestService>) {

    fun getInterface(): BnbRestService? {
        return RetrofitClient.getInstance()?.create(restService)
    }
}

package com.vibs.maashakti.api.models

data class Error(
    val errorCode: Int,
    val errorUserMessage: String,
    val errorMessage: String,
    val httpErrorCode: Int
    )
package com.example.dicodingstory.ui.auth.repository

import com.example.dicodingstory.ui.auth.data.SignInBody
import com.example.dicodingstory.ui.auth.data.SignUpBody
import com.example.dicodingstory.utils.ApiConsumer
import com.example.dicodingstory.utils.RequestStatus
import com.example.dicodingstory.utils.SimplifiedMessage
import kotlinx.coroutines.flow.flow


class AuthRepository (private val consumer: ApiConsumer){


    fun registerUser(body: SignUpBody)= flow {
        emit(RequestStatus.Waiting)
        val response = consumer.registerUser(body)
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()!!))
        } else {
            emit(RequestStatus.Error(SimplifiedMessage.get(response.errorBody()!!.byteStream().reader().readText())))
        }
    }

    fun loginUser(body: SignInBody) = flow {
        emit(RequestStatus.Waiting)
        val response = consumer.loginUser(body)
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()!!))
        } else {
            emit(RequestStatus.Error(SimplifiedMessage.get(response.errorBody()!!.byteStream().reader().readText())))
        }
    }
}
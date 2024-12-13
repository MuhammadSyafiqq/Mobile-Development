package com.example.dicodingstory.ui.auth.data


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName ("_id")
    val id :String,
    val email : String,
    val name : String,
    val token : String,
    val userId : String)


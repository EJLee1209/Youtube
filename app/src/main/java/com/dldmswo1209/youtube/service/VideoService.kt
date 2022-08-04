package com.dldmswo1209.youtube.service

import com.dldmswo1209.youtube.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {
    @GET("/v3/9dda1018-377f-4974-bf51-bcb513a4e851")
    fun listVideos(): Call<VideoDto>

}
package com.dldmswo1209.youtube

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.dldmswo1209.youtube.adapter.VideoAdapter
import com.dldmswo1209.youtube.databinding.ActivityMainBinding
import com.dldmswo1209.youtube.dto.VideoDto
import com.dldmswo1209.youtube.service.VideoService
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var videoAdapter : VideoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PlayerFragment())
            .commit()

        videoAdapter = VideoAdapter(callback = { url, title ->
            supportFragmentManager.fragments.find { it is PlayerFragment }?.let {
                (it as PlayerFragment).play(url, title)
            }
        })
        getVideoList()
        binding.mainRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getVideoList(){
        val retrofit = Retrofit.Builder() // Retrofit 인스턴스 생성
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create()) // GsonConverter : Json 타입의 응답결과를 객체로 매핑해주는 Converter
            .build()

        retrofit.create(VideoService::class.java).also {
            it.listVideos() // listVideos() 메소드 호출
                .enqueue(object: Callback<VideoDto>{ // enqueue 로 통신 실행
                    override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                        if(!response.isSuccessful){
                            // 통신 실패
                            Log.d("testt", "response fail")
                            return
                        }
                        response.body()?.let {
                            videoAdapter.submitList(it.videos)
                        }

                    }

                    override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                        // 통신 실패
                    }
                })
        }

    }
}
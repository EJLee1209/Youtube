package com.dldmswo1209.youtube

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dldmswo1209.youtube.adapter.VideoAdapter
import com.dldmswo1209.youtube.databinding.FragmentPlayerBinding
import com.dldmswo1209.youtube.dto.VideoDto
import com.dldmswo1209.youtube.service.VideoService
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs
import kotlin.random.Random

class PlayerFragment: Fragment(R.layout.fragment_player) {
    private lateinit var binding : FragmentPlayerBinding
    private lateinit var videoAdapter : VideoAdapter
    private var player: SimpleExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPlayerBinding.bind(view)

        initMotionLayoutEvent()
        initRecyclerView()
        initPlayer()
        initControlButton()
        getVideoList()

    }
    private fun initMotionLayoutEvent(){
        binding.playerMotionLayout.setTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {}

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                (activity as MainActivity).also { mainActivity ->
                    mainActivity.findViewById<MotionLayout>(R.id.mainMotionLayout).progress = abs(progress)
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {}

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {}
        })
    }
    private fun initRecyclerView(){
        videoAdapter = VideoAdapter(callback = { url, title->
            play(url, title)
        })
        binding.fragmentRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    private fun initPlayer(){
        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }
        binding.playerView.player = player
        player?.addListener(object: Player.EventListener{
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if(isPlaying){
                    binding.bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_pause_24)

                } else{
                    binding.bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }
        })
    }
    private fun initControlButton(){
        binding.bottomPlayerControlButton.setOnClickListener{
            val player = this.player ?: return@setOnClickListener
            if(player.isPlaying){
                player.pause()
            }else{
                player.play()
            }
        }
    }
    private fun getVideoList(){
        val retrofit = Retrofit.Builder() // Retrofit 인스턴스 생성
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create()) // GsonConverter : Json 타입의 응답결과를 객체로 매핑해주는 Converter
            .build()

        retrofit.create(VideoService::class.java).also {
            it.listVideos() // listVideos() 메소드 호출
                .enqueue(object: Callback<VideoDto> { // enqueue 로 통신 실행
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
    fun play(url: String, title: String){
        context?.let {
            val dataSourceFactory = DefaultDataSourceFactory(it)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
            player?.apply {
                setMediaSource(mediaSource)
                prepare()
                play()
            }
        }

        binding.playerMotionLayout.transitionToEnd()
        binding.bottomTitleTextView.text = title
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }
    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
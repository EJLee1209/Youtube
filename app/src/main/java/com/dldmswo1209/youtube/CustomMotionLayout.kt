package com.dldmswo1209.youtube

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout

class CustomMotionLayout(context: Context, attributeSet: AttributeSet? = null): MotionLayout(context, attributeSet) {
    private var motionTouchStarted = false // motion 을 동작하기 위한 영역이 터치가 되었는지 확인하는 flag 변수
    private val mainContainerView by lazy {
        findViewById<View>(R.id.mainContainerLayout)
    }
    private val hitRect = Rect()

    init {
        setTransitionListener(object: TransitionListener{
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
            ) {}

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                // 모션이 끝나면 motionTouchStarted 를 초기화
                motionTouchStarted = false
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }
        })
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.actionMasked){
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL->{
                motionTouchStarted = false
                return super.onTouchEvent(event)
            }
        }
        if(!motionTouchStarted){
            mainContainerView.getHitRect(hitRect) // getHitRect() 호출시 hitRect 에 mainContainerView 의 hitRect 값을 넣어 줌
            motionTouchStarted = hitRect.contains(event.x.toInt(), event.y.toInt()) // hitRect 영역 안에 터치가 되었는지 확인
        }
        return super.onTouchEvent(event) && motionTouchStarted // motionTouchStarted 가 true 인 경우에만 모션이 동작하도록 함
    }

    private val gestureListener by lazy{
        object : GestureDetector.SimpleOnGestureListener(){
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                mainContainerView.getHitRect(hitRect)
                return hitRect.contains(e1.x.toInt(), e1.y.toInt())
            }
        }
    }

    private val gestureDetector by lazy{
        GestureDetector(context, gestureListener)
    }
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)

    }
}
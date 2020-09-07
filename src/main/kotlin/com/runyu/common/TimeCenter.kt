package com.runyu.common

import java.time.LocalDateTime
import java.util.*

object TimeCenter{
    private var bStart=false
    private var oldYear=0
    private var oldMonth=0
    private var oldWeek=0
    private var oldDay=-1
    private var oldHour=-1
    private var oldMinute=0
    private var oldQuarter=-1

    private var mMinuteHandler:((old:Int,new:Int)->Unit)?=null
    private var mQuarterHandler:((old:Int,newHour:Int,new:Int)->Unit)?=null
    private var mHourHandler:((old:Int,new:Int)->Unit)?=null
    private var mDayHandler:((old:Int,new:Int)->Unit)?=null
    private var mWeekHandler:((old:Int,new:Int)->Unit)?=null
    private var mMonthHandler:((old:Int,new:Int)->Unit)?=null
    private var mYearHandler:((old:Int,new:Int)->Unit)?=null
    private var mStartHandler:(()->Unit)?=null

    private val timer = Timer()
    private val timerHandler:()->Unit={

        if(bStart) {

            var now = LocalDateTime.now()

            if(now.minute!= oldMinute){
                mMinuteHandler?.invoke(oldMinute,now.minute)
                oldMinute= now.minute
            }

            var quarter=now.minute/15
            if(quarter!= oldQuarter){
                mQuarterHandler?.invoke(oldQuarter,now.hour,quarter)
                oldQuarter= quarter
            }

            if (now.hour != oldHour) {
                if(-1!= oldHour)
                    mHourHandler?.invoke(oldHour, now.hour)
                oldHour = now.hour
            }

            if (now.dayOfMonth != oldDay) {
                if(-1!=oldDay)
                    mDayHandler?.invoke(oldDay, now.dayOfMonth)
                oldDay = now.dayOfMonth
            }

            var week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
            if (week != oldWeek) {
                mWeekHandler?.invoke(oldWeek, week)
                oldWeek = week
            }

            if (now.monthValue != oldMonth) {
                mMonthHandler?.invoke(oldMonth, now.monthValue)
                oldMonth = now.monthValue
            }

            if (now.year != oldYear) {
                mYearHandler?.invoke(oldYear, now.year)
                oldYear = now.year
            }
        }
    }


    init {
        timer.schedule(object : TimerTask() {
            override fun run() {
                System.out.println("timerCenter is living at" + this.scheduledExecutionTime())
                timerHandler.invoke()
            }
        }, 5000, 10000)
    }



    fun minuteChange(block:(old:Int,new:Int)->Unit):TimeCenter{
        mMinuteHandler=block
        return this
    }

    fun quarterChange(block:(old:Int,newHour:Int,new:Int)->Unit):TimeCenter{
        mQuarterHandler=block
        return this
    }

    fun hourChange(block:(old:Int,new:Int)->Unit):TimeCenter{
        mHourHandler=block
        return this
    }

    fun dayChange(block:(old:Int,new:Int)->Unit):TimeCenter{
        mDayHandler=block
        return this
    }

    fun weekChange(block:(old:Int,new:Int)->Unit):TimeCenter{
        mWeekHandler=block
        return this
    }

    fun monthChange(block:(old:Int,new:Int)->Unit):TimeCenter{
        mMonthHandler=block
        return this
    }

    fun yearChange(block:(old:Int,new:Int)->Unit):TimeCenter{
        mYearHandler=block
        return this
    }

    fun onStart(block:()->Unit):TimeCenter{
        mStartHandler=block
        return this
    }

    fun start(){
        bStart=true
        mStartHandler?.invoke()
    }

    fun stop(){
        bStart=false
    }
}
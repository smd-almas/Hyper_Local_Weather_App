package com.example.weatherapp.util

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.core.animation.doOnRepeat
import com.example.weatherapp.R

object WeatherAnimationUtil {

    fun animateWeatherIcon(imageView: ImageView, weatherCondition: String) {
        when {
            weatherCondition.contains("rain", ignoreCase = true) -> {
                animateRain(imageView)
            }
            weatherCondition.contains("snow", ignoreCase = true) -> {
                animateSnow(imageView)
            }
            weatherCondition.contains("cloud", ignoreCase = true) -> {
                animateClouds(imageView)
            }
            weatherCondition.contains("clear", ignoreCase = true) -> {
                animateSun(imageView)
            }
            weatherCondition.contains("thunder", ignoreCase = true) -> {
                animateThunder(imageView)
            }
            weatherCondition.contains("fog", ignoreCase = true) || weatherCondition.contains("mist", ignoreCase = true) -> {
                animateFog(imageView)
            }
            weatherCondition.contains("wind", ignoreCase = true) -> {
                animateWind(imageView)
            }
            else -> {
                // Default animation or static icon
                imageView.setImageResource(R.drawable.ic_weather_cloudy)
            }
        }
    }

    private fun animateRain(imageView: ImageView) {
        imageView.setImageResource(R.drawable.ic_weather_rainy)
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1500
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            imageView.translationY = value * 5f
        }
        animator.doOnRepeat {
            imageView.translationY = 0f
        }
        animator.start()
    }

    private fun animateSnow(imageView: ImageView) {
        imageView.setImageResource(R.drawable.ic_weather_snowy)
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 2000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            imageView.rotation = value * 360f
        }
        animator.start()
    }

    private fun animateClouds(imageView: ImageView) {
        imageView.setImageResource(R.drawable.ic_weather_cloudy)
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 3000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            imageView.translationX = (value * 10f) - 5f
        }
        animator.start()
    }

    private fun animateSun(imageView: ImageView) {
        imageView.setImageResource(R.drawable.ic_weather_sunny)
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 6000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            imageView.rotation = value * 360f
        }
        animator.start()
    }

    private fun animateThunder(imageView: ImageView) {
        imageView.setImageResource(R.drawable.ic_weather_thunder)
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 2000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            if (value > 0.8f || (value > 0.4f && value < 0.6f)) {
                imageView.alpha = 1.0f
            } else {
                imageView.alpha = 0.7f
            }
        }
        animator.start()
    }

    private fun animateFog(imageView: ImageView) {
        imageView.setImageResource(R.drawable.ic_weather_foggy)
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 3000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            imageView.alpha = 0.7f + (value * 0.3f)
        }
        animator.start()
    }

    private fun animateWind(imageView: ImageView) {
        imageView.setImageResource(R.drawable.ic_weather_windy)
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 2000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            imageView.translationX = (value * 10f) - 5f
        }
        animator.start()
    }
}
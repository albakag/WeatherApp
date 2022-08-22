package com.alexalban.weatherapp.adapters

data class WeatherModel(
        val city: String,
        val currentTemp: String,
        val maxTemp: String,
        val minTemp: String,
        val condition: String,
        val imageUrl: String,
        val hours: String,
        val currentTime: String
)
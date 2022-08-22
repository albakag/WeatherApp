package com.alexalban.weatherapp

data class DataItem(
    val city: String,
    val time: String,
    val imageUrl: String,
    val condition: String,
    val currentTemp: String,
    val maxTemp: String,
    val minTemp: String,
    val feelsLike: String,
    val windDirection: String,
    val wind_mph: String,
    val hours: String,
    val humidity: String
)

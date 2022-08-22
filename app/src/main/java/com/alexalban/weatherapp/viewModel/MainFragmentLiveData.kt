package com.alexalban.weatherapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexalban.weatherapp.adapters.WeatherModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainFragmentLiveData: ViewModel() {

    val currentWeatherModel = MutableLiveData<WeatherModel>()
    val currentViewModelList = MutableLiveData<List<WeatherModel>>()
//    private val _currentViewModel = MutableLiveData<WeatherModel>()
//    val currentViewModel: LiveData<WeatherModel> = _currentViewModel
//
//    private val _currentViewModelList = MutableLiveData<List<WeatherModel>>()
//    val currentViewModelList: LiveData<List<WeatherModel>> = _currentViewModelList
//
//    fun updateCurrentViewModel(weatherModel: WeatherModel){
//        _currentViewModel.value = weatherModel
//    }
//
//    fun updateCurrentViewModelList(list: List<WeatherModel>){
//        _currentViewModelList.value = list
//    }

}
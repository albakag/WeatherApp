package com.alexalban.weatherapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexalban.weatherapp.adapters.WeatherModel

class MainViewModel: ViewModel() {

//    val currentViewModel = MutableLiveData<WeatherModel>()
//    val currentViewModelList = MutableLiveData<List<WeatherModel>>()
    private val _currentViewModel = MutableLiveData<WeatherModel>()
    val currentViewModel: LiveData<WeatherModel> = _currentViewModel
    fun updateCurrentViewModel(weatherModel: WeatherModel){
        _currentViewModel.value = weatherModel
    }

    private val _currentViewModelList = MutableLiveData<List<WeatherModel>>()
    val currentViewModelList: LiveData<List<WeatherModel>> = _currentViewModelList
    fun updateCurrentViewModelList(list: List<WeatherModel>){
        _currentViewModelList.value = list
    }
}
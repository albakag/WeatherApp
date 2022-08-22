package com.alexalban.weatherapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexalban.weatherapp.R
import com.alexalban.weatherapp.adapters.WeatherModel
import com.alexalban.weatherapp.adapters.WeatherModelAdapter
import com.alexalban.weatherapp.databinding.FragmentHoursBinding
import com.alexalban.weatherapp.viewModel.MainFragmentLiveData
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

class HoursFragment : Fragment() {
    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherModelAdapter
    private val model: MainFragmentLiveData by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHoursBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rcViewInit()
        updateHoursListByLiveData()
    }

    private fun updateHoursListByLiveData() {
        model.currentWeatherModel.observe(viewLifecycleOwner) {
            Log.d("MyLog", "Hours: ${it.hours}")
//            adapter.submitList(getHoursList(it))
        }
    }

    private fun rcViewInit() = with(binding){
        rcViewHours.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherModelAdapter()
        rcViewHours.adapter = adapter
    }

    private fun getHoursList(itemWeather: WeatherModel): List<WeatherModel>{
        val arrayList = JSONArray(itemWeather.hours)
        val list = ArrayList<WeatherModel>()

        Log.d("MyLog", "Into getHoursList")
        for (i in 0 until arrayList.length()){
            val itemHours = WeatherModel(
                "",
                currentTemp = (arrayList[i] as JSONObject).getString("temp_c"),
                "",
                "",
                condition = (arrayList[i] as JSONObject).getJSONObject("condition").getString("text"),
                imageUrl = (arrayList[i] as JSONObject).getJSONObject("condition").getString("icon"),
                "",
                ""
            )
            list.add(itemHours)
        }
        return list
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}
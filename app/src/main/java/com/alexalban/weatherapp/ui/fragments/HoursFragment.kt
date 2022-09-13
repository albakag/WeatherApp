package com.alexalban.weatherapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexalban.weatherapp.adapters.WeatherModel
import com.alexalban.weatherapp.adapters.WeatherModelAdapter
import com.alexalban.weatherapp.databinding.FragmentHoursBinding
import com.alexalban.weatherapp.viewModel.MainViewModel
import org.json.JSONArray
import org.json.JSONObject

class HoursFragment : Fragment() {
    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherModelAdapter
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.currentViewModel.observe(viewLifecycleOwner){
            adapter.submitList(getHoursList(it))
        }
        rcViewInit()
    }

    private fun rcViewInit() = with(binding){
        adapter = WeatherModelAdapter(null)
        rcViewHours.layoutManager = LinearLayoutManager(requireActivity())
        rcViewHours.adapter = adapter
    }

    private fun getHoursList(itemWeather: WeatherModel): List<WeatherModel>{
        val hoursArray = JSONArray(itemWeather.hours)
        val list = ArrayList<WeatherModel>()
        for (i in 0 until hoursArray.length()){

            val item = WeatherModel(
                city = itemWeather.city,
                currentTemp = (hoursArray[i] as JSONObject).getString("temp_c").toFloat().toInt().toString()+"°C",
                "",
                "",
                condition = (hoursArray[i] as JSONObject).getJSONObject("condition").getString("text"),
                imageUrl = (hoursArray[i] as JSONObject).getJSONObject("condition").getString("icon"),
                "",
                currentTime = (hoursArray[i] as JSONObject).getString("time")
            )
            list.add(item)
        }
        return list
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}
package com.alexalban.weatherapp.ui.fragments

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.alexalban.weatherapp.R
import com.alexalban.weatherapp.adapters.ViewPagerAdapter
import com.alexalban.weatherapp.adapters.WeatherModel
import com.alexalban.weatherapp.databinding.FragmentMainBinding
import com.alexalban.weatherapp.utils.Constants
import com.alexalban.weatherapp.viewModel.MainFragmentLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

class MainFragment : Fragment() {

    private val fragmentList = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )

    private lateinit var binding: FragmentMainBinding
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private val model: MainFragmentLiveData by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        init()
        requestWeatherData(Constants.MAIN_CITY)
        updateCurrentWeather()
    }

    private fun init() = with(binding){
        val adapter = ViewPagerAdapter(activity as FragmentActivity, fragmentList)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager){
            tab, pos -> tab.text = resources.getStringArray(R.array.string_fragments_name)[pos]
        }.attach()
    }

    private fun updateCurrentWeather() = with(binding){
        model.currentWeatherModel.observe(viewLifecycleOwner){
            val minMuxTemp = "${it.maxTemp}'C/${it.minTemp}'C"
            tvDateTime.text = it.currentTime
            tvCondition.text = it.condition
            tvCurrentTemp.text = it.currentTemp
            tvPlaceInfo.text = it.city
            tvMaxMin.text = minMuxTemp
            Picasso.get().load("Http:" + it.imageUrl).into(ivPictureCard)
        }
    }

    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            Snackbar.make(requireView(), "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission(){
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestWeatherData(city: String){
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                Constants.KEY_API +
                "&q=" +
                city +
                "&days=" +
                "3" +
                "&aqi=no&alerts=no"

        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                result -> parseWeatherData(result)
            },
            {
                error -> Log.d("MyLog", "Error: $error")
            }
        )
        queue.add(request)
    }

    private fun parseWeatherData(result: String){
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        model.currentViewModelList.value = list
        parseCurrentTime(mainObject, list[0])
    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel>{
        val list = ArrayList<WeatherModel>()
        val daysArrayList = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        val cityName = mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArrayList.length()){
            val day = (daysArrayList[i] as JSONObject)
            val item = WeatherModel(
                cityName,
                mainObject.getJSONObject("current").getString("temp_c"),
                day.getJSONObject("day").getString("maxtemp_c"),
                day.getJSONObject("day").getString("mintemp_c"),
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                day.getJSONArray("hour").toString(),
                mainObject.getJSONObject("location").getString("localtime")
            )
            list.add(item)
        }
        return list
    }

    private fun parseCurrentTime(mainObject: JSONObject, weatherItem: WeatherModel){
        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("temp_c"),
            weatherItem.maxTemp,
            weatherItem.minTemp,
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            weatherItem.hours,
            weatherItem.currentTime
        )
        model.currentWeatherModel.value = item

//        Log.d("MyLog", "Max temperature: ${item.maxTemp}")
//        Log.d("MyLog", "Min temperature: ${item.minTemp}")
//        Log.d("MyLog", "Hours: ${item.hours}")
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
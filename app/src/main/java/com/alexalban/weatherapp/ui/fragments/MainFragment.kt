package com.alexalban.weatherapp.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.alexalban.weatherapp.adapters.ViewPagerAdapter
import com.alexalban.weatherapp.adapters.WeatherModel
import com.alexalban.weatherapp.databinding.FragmentMainBinding
import com.alexalban.weatherapp.ui.dialogs.LocationDialog
import com.alexalban.weatherapp.utils.Constants
import com.alexalban.weatherapp.viewModel.MainViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

class MainFragment : Fragment() {

    private lateinit var ftClient: FusedLocationProviderClient
    private val fragmentList = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )

    private lateinit var sharedPrefs: SharedPreferences
    private val tList = listOf(
        "Hours",
        "Days"
    )

    private lateinit var binding: FragmentMainBinding
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefs = requireContext().getSharedPreferences(Constants.SHARED, Context.MODE_PRIVATE)
        checkPermission()
        init()
        updateCurrentWeather()
        refreshByButton()
        searchByCityName()
//        checkLocationEnabled()
    }

//    override fun onClick(cityName: String) {
//        Log.d("MyLog", cityName)
//    }

    private fun searchByCityName(){
        binding.btnSearch.setOnClickListener {
            LocationDialog.getSearchByCityDialog(requireContext(), object : LocationDialog.Listener {
                override fun onClick(cityName: String?) {
                    if (cityName != null) {
                        requestWeatherData(city = cityName)
                    }
                }
            })
        }
    }

    private fun refreshByButton(){
        binding.btnSync.setOnClickListener {
            sharedPrefs.edit().putString(Constants.SHARED, Constants.SHARED_CITY).apply()
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
            getLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        checkLocationEnabled()
    }

    private fun checkLocationEnabled(){
        if (isLocationEnabled()){
            getLocation()
        } else {
            Snackbar.make(binding.root, "Location disabled!", Snackbar.LENGTH_SHORT).show()
            LocationDialog.getAlertDialog(requireContext(), object : LocationDialog.Listener{
                override fun onClick(cityName: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locMan = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun init() = with(binding){
        ftClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = ViewPagerAdapter(activity as FragmentActivity, fragmentList)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager){
                tab, pos -> tab.text = tList[pos]
        }.attach()
    }

    private fun getLocation(){
        if (sharedPrefs.getString(Constants.SHARED, Constants.SHARED_CITY) == Constants.SHARED_CITY){
            val ct = CancellationTokenSource()
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            ftClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
                .addOnCompleteListener {
                    requestWeatherData("${it.result.latitude},${it.result.longitude}")
                }
        } else {
            requestWeatherData(sharedPrefs.getString(Constants.SHARED, Constants.SHARED_CITY).toString())
        }
    }

    private fun updateCurrentWeather() = with(binding){
        model.currentViewModel.observe(viewLifecycleOwner){
            val minMuxTemp = "${it.maxTemp}°C/${it.minTemp}°C"
            tvDateTime.text = it.currentTime
            tvPlaceInfo.text = it.city
            tvCurrentTemp.text = it.currentTemp.ifEmpty { minMuxTemp }
            tvCondition.text = it.condition
            tvMaxMin.text = if (it.currentTemp.isEmpty()) "" else minMuxTemp
            Picasso.get().load("https:" + it.imageUrl).into(ivPictureCard)
        }
    }

    private fun requestWeatherData(city: String){
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                Constants.KEY_API +
                "&q=" +
                city +
                "&days=" +
                "7" +
                "&aqi=no&alerts=no"

        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                response -> getDaysList(response)
            },
            {
                error -> Log.d("MyLog", "Error: $error")
            }
        )
        queue.add(request)
    }

    private fun getDaysList(response: String){
        val mainObject = JSONObject(response)
        val listOfDays = parseDays(mainObject)
        parseCurrentDay(mainObject, listOfDays[0])
    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel>{
        val list = ArrayList<WeatherModel>()
        val daysArrayList = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        val cityName = mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArrayList.length()){
            val day = daysArrayList[i] as JSONObject
            val item = WeatherModel(
                cityName,
//                mainObject.getJSONObject("current").getString("temp_c"),
                "",
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                day.getJSONArray("hour").toString(),
                day.getString("date")
            )
            list.add(item)
        }
        model.updateCurrentViewModelList(list)
        return list
    }

    private fun parseCurrentDay(mainObject: JSONObject, weatherItem: WeatherModel){
        val item = WeatherModel(
            city = mainObject.getJSONObject("location").getString("name"),
            currentTemp = mainObject.getJSONObject("current").getString("temp_c").toFloat().toInt().toString(),
            maxTemp = weatherItem.maxTemp,
            minTemp = weatherItem.minTemp,
            condition = mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            imageUrl = mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            hours = weatherItem.hours,
            currentTime = mainObject.getJSONObject("current").getString("last_updated")
        )
        model.updateCurrentViewModel(item)
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

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
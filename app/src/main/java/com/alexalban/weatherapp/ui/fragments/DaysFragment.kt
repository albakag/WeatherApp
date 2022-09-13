package com.alexalban.weatherapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexalban.weatherapp.adapters.WeatherModel
import com.alexalban.weatherapp.adapters.WeatherModelAdapter
import com.alexalban.weatherapp.databinding.FragmentDaysBinding
import com.alexalban.weatherapp.viewModel.MainViewModel

class DaysFragment : Fragment(), WeatherModelAdapter.Listener {

    private lateinit var adapter: WeatherModelAdapter
    private lateinit var binding: FragmentDaysBinding
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recViewInit()
        model.currentViewModelList.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }

    private fun recViewInit(){
        adapter = WeatherModelAdapter(listener = this)
        binding.rcViewDays.layoutManager = LinearLayoutManager(requireActivity())
        binding.rcViewDays.adapter = adapter
    }

    companion object {

       @JvmStatic
        fun newInstance() = DaysFragment()
    }

    override fun onCLick(item: WeatherModel) {
        model.updateCurrentViewModel(item)
    }
}
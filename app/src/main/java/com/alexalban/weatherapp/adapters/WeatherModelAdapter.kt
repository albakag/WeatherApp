package com.alexalban.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexalban.weatherapp.R
import com.alexalban.weatherapp.databinding.ListItemBinding

class WeatherModelAdapter: ListAdapter<WeatherModel, WeatherModelAdapter.Holder>(Comparator()) {

    class Holder(view: View): RecyclerView.ViewHolder(view){
        private val binding = ListItemBinding.bind(view)

        fun bind(item: WeatherModel) = with(binding){
            tvItemCondition.text = item.condition
            tvTemp.text = item.currentTemp
            tvItemDate.text = item.currentTime
        }
    }

    class Comparator(): DiffUtil.ItemCallback<WeatherModel>(){
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.bind(getItem(position))
    }


}
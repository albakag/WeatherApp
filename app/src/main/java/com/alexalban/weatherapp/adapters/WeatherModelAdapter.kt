package com.alexalban.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexalban.weatherapp.R
import com.alexalban.weatherapp.databinding.ListItemBinding
import com.squareup.picasso.Picasso

class WeatherModelAdapter(private val listener: Listener?): ListAdapter<WeatherModel, WeatherModelAdapter.Holder>(Comparator()) {

    class Holder(view: View, listener: Listener?): RecyclerView.ViewHolder(view){
        private val binding = ListItemBinding.bind(view)

        private var itemTemp : WeatherModel? = null
        init {
            itemView.setOnClickListener {
                itemTemp?.let { it1 -> listener?.onCLick(item = it1) }
            }
        }

        fun bind(item: WeatherModel) = with(binding){
            itemTemp = item
            tvItemCondition.text = item.condition
            tvTemp.text = item.currentTemp.ifEmpty { "${item.maxTemp}°C / ${item.minTemp}°C" }
            tvItemDate.text = item.currentTime
            Picasso.get().load("https:" + item.imageUrl).into(imageView)
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
        return Holder(view = view, listener = listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.bind(getItem(position))
    }

    interface Listener {
        fun onCLick(item: WeatherModel)
    }

}
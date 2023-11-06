package com.giraffe.weatherforecasapplication.features.home.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.giraffe.weatherforecasapplication.databinding.DailyItemBinding
import com.giraffe.weatherforecasapplication.databinding.HourlyItemBinding
import com.giraffe.weatherforecasapplication.model.Daily
import com.giraffe.weatherforecasapplication.model.Hourly
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HourlyAdapter(private val list: MutableList<Hourly>) : Adapter<HourlyAdapter.HourlyVH>() {

    class HourlyVH(private val binding: HourlyItemBinding) : ViewHolder(binding.root) {
        fun bind(item: Hourly) {
            binding.tvTime.text = unixTimeToReadableDate(item.dt.toLong())
            Glide.with(binding.root.context)
                .load("https://openweathermap.org/img/wn/${item.weather[0].icon}.png")
                .into(binding.ivWeather)
            binding.tvTemp.text= item.temp.toInt().toString().plus("°")
        }

        private fun unixTimeToReadableDate(unixTime: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getDefault() // Set your desired time zone
            val date = Date(unixTime * 1000)
            return dateFormat.format(date)
        }
    }


    fun updateList(list: List<Hourly>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyVH {
        val binding = HourlyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyVH(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: HourlyVH, position: Int) = holder.bind(list[position])
}
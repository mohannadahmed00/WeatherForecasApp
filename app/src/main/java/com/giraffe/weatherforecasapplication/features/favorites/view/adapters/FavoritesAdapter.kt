package com.giraffe.weatherforecasapplication.features.favorites.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.giraffe.weatherforecasapplication.databinding.FavoriteItemBinding
import com.giraffe.weatherforecasapplication.model.ForecastModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FavoritesAdapter(private val list: MutableList<ForecastModel>,private val onDeleteClick: OnDeleteClick,private val onSelectClick: OnSelectClick) : Adapter<FavoritesAdapter.FavoriteVH>() {

    inner class FavoriteVH(private val binding: FavoriteItemBinding) : ViewHolder(binding.root) {
        fun bind(item: ForecastModel) {
            binding.tvZone.text = item.timezone
            Glide.with(binding.root.context)
                .load("https://openweathermap.org/img/wn/${item.current.weather[0].icon}.png")
                .into(binding.ivWeather)
            binding.tvDes.text = item.current.weather[0].description
            binding.ivDelete.setOnClickListener { onDeleteClick.onDeleteClick(item) }
            binding.root.setOnClickListener{
                onSelectClick.onSelectClick(item)
            }
        }

        private fun unixTimeToReadableDate(unixTime: Long): String {
            val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getDefault() // Set your desired time zone
            val date = Date(unixTime * 1000)
            return dateFormat.format(date)
        }
    }


    fun updateList(list: List<ForecastModel>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteVH {
        val binding = FavoriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteVH(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: FavoriteVH, position: Int) = holder.bind(list[position])

    interface OnDeleteClick{
        fun onDeleteClick(forecast: ForecastModel)
    }

    interface OnSelectClick{
        fun onSelectClick(forecast: ForecastModel)
    }
}
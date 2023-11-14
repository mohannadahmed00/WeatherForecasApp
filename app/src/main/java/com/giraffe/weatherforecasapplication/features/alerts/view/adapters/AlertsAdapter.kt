package com.giraffe.weatherforecasapplication.features.alerts.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.giraffe.weatherforecasapplication.R
import com.giraffe.weatherforecasapplication.databinding.AlertItemBinding
import com.giraffe.weatherforecasapplication.model.alert.AlertItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AlertsAdapter(val list: MutableList<AlertItem>) : Adapter<AlertsAdapter.AlertsVH>() {

    inner class AlertsVH(private val binding: AlertItemBinding) : ViewHolder(binding.root) {
        fun bind(item: AlertItem) {
            binding.tvLocation.text = item.locationName
            if (item.endDateTime==null) {
                binding.tvTime.text = binding.root.context.getString(R.string.at, localToReadable(item.startDateTime))
            }else{
                binding.tvTime.text = binding.root.context.getString(
                    R.string.from_to,
                    localToReadable(item.startDateTime),
                    localToReadable(item.startDateTime)
                )
            }
        }

        private fun localToReadable(localDateTime: LocalDateTime): String {
            val formatter = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm", Locale.US)
            return localDateTime.format(formatter)
        }
    }


    fun updateList(list: List<AlertItem>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertsVH {
        val binding = AlertItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlertsVH(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: AlertsVH, position: Int) = holder.bind(list[position])

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }
}
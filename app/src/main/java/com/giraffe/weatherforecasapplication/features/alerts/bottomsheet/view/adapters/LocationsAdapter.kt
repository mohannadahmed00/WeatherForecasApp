package com.giraffe.weatherforecasapplication.features.alerts.bottomsheet.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.giraffe.weatherforecasapplication.R
import com.giraffe.weatherforecasapplication.databinding.LocationItemBinding
import com.giraffe.weatherforecasapplication.features.alerts.bottomsheet.view.BottomSheet
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.utils.getAddress

class LocationsAdapter(
    private val list: MutableList<BottomSheet.LocationItem>,
    private val onLocationClick: OnLocationClick
) :
    Adapter<LocationsAdapter.LocationsVH>() {

    private var selectedItemPosition: Int = -1

    inner class LocationsVH(val binding: LocationItemBinding) : ViewHolder(binding.root)


    fun updateList(list: List<BottomSheet.LocationItem>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsVH {
        val binding = LocationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationsVH(binding)
    }

    override fun onBindViewHolder(holder: LocationsVH, position: Int) {
        val item = list[position]
        val context = holder.binding.root.context
        val textView = holder.binding.tvLocation
        val location = getAddress(context, item.forecast.lat, item.forecast.lon,item.forecast.timezone)
        textView.text = location


        if (position == selectedItemPosition) {
            //holder.binding.root.setCardBackgroundColor(context.getColor(R.color.red))
            holder.binding.tvLocation.setTextColor(context.getColor(R.color.red))
        } else {
            //holder.binding.root.setCardBackgroundColor(context.getColor(R.color.white))
            holder.binding.tvLocation.setTextColor(context.getColor(R.color.gray))
        }

        holder.binding.root.setOnClickListener {
            if (selectedItemPosition != position) {
                val previousSelectedItemPosition = selectedItemPosition
                selectedItemPosition = position
                notifyItemChanged(previousSelectedItemPosition)
                notifyItemChanged(selectedItemPosition)
                onLocationClick.onLocationClick(item.forecast)
            }
        }
    }

    override fun getItemCount() = list.size



    interface OnLocationClick {
        fun onLocationClick(forecast: ForecastModel)
    }
}
package com.giraffe.weatherforecasapplication.features.alerts.bottomsheet.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.giraffe.weatherforecasapplication.R
import com.giraffe.weatherforecasapplication.databinding.LocationItemBinding
import com.giraffe.weatherforecasapplication.features.alerts.bottomsheet.view.BottomSheet
import com.giraffe.weatherforecasapplication.model.forecast.ForecastModel
import com.giraffe.weatherforecasapplication.utils.Constants
import com.giraffe.weatherforecasapplication.utils.getAddress

class TypesAdapter(
    private val list: MutableList<String>,
    private val onTypeClick: OnTypeClick
) :
    Adapter<TypesAdapter.TypesVH>() {

    private var selectedItemPosition: Int = -1

    inner class TypesVH(val binding: LocationItemBinding) : ViewHolder(binding.root)


    fun updateList(list: List<String>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypesVH {
        val binding = LocationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TypesVH(binding)
    }

    override fun onBindViewHolder(holder: TypesVH, position: Int) {
        val item = list[position]
        val context = holder.binding.root.context
        val textView = holder.binding.tvLocation
        textView.text = item


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
                onTypeClick.onTypeClick(item)
            }
        }
    }

    override fun getItemCount() = list.size



    interface OnTypeClick {
        fun onTypeClick(alertType:String)
    }
}
package com.dicoding.dicoevent.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dicoding.dicoevent.data.response.ListEventsItem
import com.dicoding.dicoevent.databinding.ItemHorizontalEventBinding

class HomeListUpcomingAdapter : ListAdapter<ListEventsItem, HomeListUpcomingAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding = ItemHorizontalEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val event = getItem(position)
        holder.bind(event)
    }

    class MyViewHolder(val binding: ItemHorizontalEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            Glide.with(itemView.context)
                .load(event.imageLogo)
                .transform(CenterCrop(), RoundedCorners(16))
                .into(binding.imgItemPhoto)

            binding.btnRegister.setOnClickListener {
                Toast.makeText(itemView.context, "Registered for ${event.name}", Toast.LENGTH_SHORT).show()
            }

            itemView.setOnClickListener {
                Toast.makeText(itemView.context, event.name, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
package com.dicoding.dicoevent.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dicoding.dicoevent.R
import com.dicoding.dicoevent.data.response.ListEventsItem
import com.dicoding.dicoevent.databinding.ItemRowEventBinding
import com.dicoding.dicoevent.utils.formatDateForDisplay

class EventVerticalAdapter(
    private val onItemClick: (Int) -> Unit,
) : ListAdapter<ListEventsItem, EventVerticalAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding = ItemRowEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val event = getItem(position)
        holder.bind(event, onItemClick)
    }

    class MyViewHolder(val binding: ItemRowEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem, onItemClick: (Int) -> Unit) {
            binding.tvItemTitle.text = event.name
            binding.tvItemDate.text = event.beginTime?.formatDateForDisplay()
            binding.tvItemPlace.text = event.cityName

            Glide.with(itemView.context)
                .load(event.imageLogo)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.baseline_broken_image_24)
                .transform(CenterCrop(), RoundedCorners(16))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imgItemPhoto)

            itemView.setOnClickListener {
                onItemClick(event.id ?: 0)
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
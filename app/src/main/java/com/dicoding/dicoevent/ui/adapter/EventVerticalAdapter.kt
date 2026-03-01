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
import com.dicoding.dicoevent.data.local.entity.EventEntity
import com.dicoding.dicoevent.databinding.ItemRowEventBinding
import com.dicoding.dicoevent.utils.formatDateForDisplay

class EventVerticalAdapter(
    private val onItemClick: (Int) -> Unit,
) : ListAdapter<EventEntity, EventVerticalAdapter.MyViewHolder>(DIFF_CALLBACK) {
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
        fun bind(event: EventEntity, onItemClick: (Int) -> Unit) {
            with(binding) {
                tvItemTitle.text = event.name
                tvItemDate.text = event.beginTime?.formatDateForDisplay()
                tvItemPlace.text = event.cityName
            }

            Glide.with(itemView.context)
                .load(event.imageLogo)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.baseline_broken_image_24)
                .transform(CenterCrop(), RoundedCorners(16))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imgItemPhoto)

            itemView.setOnClickListener {
                onItemClick(event.id)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(
                oldItem: EventEntity,
                newItem: EventEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: EventEntity,
                newItem: EventEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
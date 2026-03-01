package com.dicoding.dicoevent.ui.home

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
import com.dicoding.dicoevent.databinding.ItemHorizontalEventBinding

class HomeListUpcomingAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onRegisterClick: (String) -> Unit
) : ListAdapter<EventEntity, HomeListUpcomingAdapter.MyViewHolder>(DIFF_CALLBACK) {
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
        holder.bind(event, onItemClick, onRegisterClick)
    }

    class MyViewHolder(val binding: ItemHorizontalEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventEntity, onItemClick: (Int) -> Unit, onRegisterClick: (String) -> Unit) {
            Glide.with(itemView.context)
                .load(event.imageLogo)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.baseline_broken_image_24)
                .transform(CenterCrop(), RoundedCorners(16))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imgItemPhoto)

            binding.btnRegister.setOnClickListener {
                onRegisterClick(event.link ?: "")
            }

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
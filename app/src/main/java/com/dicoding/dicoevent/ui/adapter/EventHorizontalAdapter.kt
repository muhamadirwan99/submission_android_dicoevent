package com.dicoding.dicoevent.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dicoding.dicoevent.R
import com.dicoding.dicoevent.data.remote.response.ListEventsItem
import com.dicoding.dicoevent.databinding.ItemCardEventBinding
import com.dicoding.dicoevent.utils.formatDateForDisplay

class EventHorizontalAdapter(
    private val onItemClick: (Int) -> Unit,
) : ListAdapter<ListEventsItem, EventHorizontalAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding = ItemCardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val event = getItem(position)
        holder.bind(event, onItemClick)
    }

    class MyViewHolder(val binding: ItemCardEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem, onItemClick: (Int) -> Unit) {
           with(binding){
               val quota = event.quota ?: 0
               val registrants = event.registrants ?: 0
               val percentage = if (quota > 0) {
                   (registrants.toDouble() / quota) * 100
               } else {
                   0.0
               }

               tvTitle.text = event.name
               tvDate.text = event.beginTime?.formatDateForDisplay()
               tvLocation.text = event.cityName
               tvOrganizerName.text = event.ownerName
               tvCategory.text = event.category

               tvProgressText.text = itemView.context.getString(R.string.registrants_format, registrants, quota)
               tvProgressPercentage.text = itemView.context.getString(R.string.progress_percentage, percentage)

               progressBar.progress = percentage.toInt().coerceIn(0, 100)

               Glide.with(itemView.context)
                   .load(event.imageLogo)
                   .placeholder(R.drawable.placeholder_image)
                   .error(R.drawable.baseline_broken_image_24)
                   .transform(CenterCrop())
                   .transition(DrawableTransitionOptions.withCrossFade())
                   .into(binding.imgItemPhoto)
           }

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
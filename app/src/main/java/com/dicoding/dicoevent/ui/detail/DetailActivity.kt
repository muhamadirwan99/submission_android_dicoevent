package com.dicoding.dicoevent.ui.detail

import com.dicoding.dicoevent.R
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.dicoding.dicoevent.data.Result
import com.dicoding.dicoevent.data.local.entity.EventEntity
import com.dicoding.dicoevent.databinding.ActivityDetailBinding
import com.dicoding.dicoevent.ui.ViewModelFactory
import com.dicoding.dicoevent.utils.DateUtils
import com.dicoding.dicoevent.utils.openUrl
import com.google.android.material.color.MaterialColors
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs by navArgs()
    private val detailViewModel: DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        observeViewModel(args.eventId)
    }

    private fun observeViewModel(eventId: Int) {
        detailViewModel.getDetailEvent(eventId).observe(this) { result ->
            with(binding) {
                shimmerLoading.shimmerViewDetail.isVisible = result is Result.Loading
                layoutContent.root.isVisible = result is Result.Success
                layoutError.layoutError.isVisible = result is Result.Error
                actionRegister.isVisible = result is Result.Success
                actionFavorite.isVisible = result is Result.Success

                when (result) {
                    is Result.Success -> {
                        actionRegister.setOnClickListener {
                            openUrl(result.data.link)
                        }
                        actionFavorite.setOnClickListener {
                            val newState = !result.data.isFavorite
                            detailViewModel.setFavoriteEvent(eventId, newState)
                        }

                        if (result.data.activeStatus == 0) {
                            binding.actionRegister.isEnabled = false
                            binding.actionRegister.text = getString( R.string.event_completed)

                            val disabledBgColor = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorSurfaceVariant)
                            val disabledTextColor = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnSurfaceVariant)

                            binding.actionRegister.backgroundTintList = ColorStateList.valueOf(disabledBgColor)
                            binding.actionRegister.setTextColor(disabledTextColor)

                        } else {
                            binding.actionRegister.isEnabled = true
                            binding.actionRegister.text =getString( R.string.register_now)

                            val activeBgColor = MaterialColors.getColor(binding.root, androidx.appcompat.R.attr.colorPrimary)
                            val activeTextColor = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnPrimary)

                            binding.actionRegister.backgroundTintList = ColorStateList.valueOf(activeBgColor)
                            binding.actionRegister.setTextColor(activeTextColor)
                        }

                        if (result.data.isFavorite) {
                            binding.actionFavorite.setImageResource(R.drawable.baseline_favorite_24)
                        } else {
                            binding.actionFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
                        }

                        bindDataDetail(result.data)
                    }

                    is Result.Error -> {
                        layoutError.tvErrorMessage.text = result.error
                    }

                    else -> {}
                }
            }
        }
    }


    private fun bindDataDetail(eventDetail: EventEntity) {
        Glide.with(this)
            .load(eventDetail.mediaCover)
            .error(R.drawable.baseline_broken_image_24)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    with(binding) {
                        shimmerViewContainer.stopShimmer()
                        shimmerViewContainer.visibility = View.GONE

                        imageEvent.visibility = View.VISIBLE
                    }

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE

                    binding.imageEvent.visibility = View.VISIBLE
                    return false
                }
            })
            .into(binding.imageEvent)

        with(binding.layoutContent) {
            val time = DateUtils.formatEventDate(eventDetail.beginTime, eventDetail.endTime)
            val quota = eventDetail.quota ?: 0
            val registrants = eventDetail.registrants ?: 0
            val remainingQuota = (quota - registrants).coerceAtLeast(0)

            val percentage = if (quota > 0) {
                (registrants.toDouble() / quota) * 100
            } else {
                0.0
            }

            val htmlDescription = eventDetail.description ?: ""

            tvCategory.text = eventDetail.category
            tvEventName.text = eventDetail.name
            tvOwnerName.text = eventDetail.ownerName
            tvLocation.text = eventDetail.cityName
            tvSummary.text = eventDetail.summary
            tvProgressLabel.text = getString(R.string.remaining_quota, remainingQuota)

            tvTimeMain.text = time.timeMain
            tvTimeSub.text = time.timeSub

            tvRegistrants.text = getString(R.string.registrants_format, registrants, quota)
            tvProgressPercentage.text = getString(R.string.progress_percentage, percentage)

            progressBar.progress = percentage.toInt().coerceIn(0, 100)

            tvDescription.setHtml(
                htmlDescription,
                HtmlHttpImageGetter(tvDescription)
            )
        }
    }
}
package com.dicoding.dicoevent.ui.detail

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
import com.dicoding.dicoevent.R
import com.dicoding.dicoevent.data.response.EventDetail
import com.dicoding.dicoevent.databinding.ActivityDetailBinding
import com.dicoding.dicoevent.utils.DateUtils
import com.dicoding.dicoevent.utils.UiState
import com.dicoding.dicoevent.utils.openUrl
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs by navArgs()
    private val detailViewModel by viewModels<DetailViewModel>()

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

        if (savedInstanceState == null) {
            detailViewModel.getDetailEvent(args.eventId)
        }

        observeViewModel()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeViewModel() {
        detailViewModel.eventDetailState.observe(this) { state ->
            with(binding) {
                shimmerLoading.shimmerViewDetail.isVisible = state is UiState.Loading
                layoutContent.root.isVisible = state is UiState.Success
                layoutError.layoutError.isVisible = state is UiState.Error
                actionRegister.isVisible = state is UiState.Success

                when (state) {
                    is UiState.Success -> {
                        actionRegister.setOnClickListener {
                            openUrl(state.data.link)
                        }

                        bindDataDetail(state.data)
                    }

                    is UiState.Error -> {
                        layoutError.tvErrorMessage.text = state.errorMessage
                        layoutError.btnRetry.setOnClickListener {
                            detailViewModel.getDetailEvent(args.eventId)
                        }
                    }

                    else -> {}
                }
            }
        }
    }


    private fun bindDataDetail(eventDetail: EventDetail) {
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
                (remainingQuota.toDouble() / quota) * 100
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
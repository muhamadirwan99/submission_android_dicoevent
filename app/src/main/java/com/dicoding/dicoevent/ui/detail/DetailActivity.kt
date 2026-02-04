package com.dicoding.dicoevent.ui.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.dicoding.dicoevent.R
import com.dicoding.dicoevent.data.response.EventDetail
import com.dicoding.dicoevent.databinding.ActivityDetailBinding
import com.dicoding.dicoevent.utils.DateUtils
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

        detailViewModel.eventDetail.observe(this) { eventDetail ->
            bindDataDetail(eventDetail)

            binding.actionRegister.setOnClickListener {
                openUrl(eventDetail.link)
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun bindDataDetail(eventDetail: EventDetail) {
        binding.apply {
            val time = DateUtils.formatEventDate(eventDetail.beginTime, eventDetail.endTime)
            val quota = eventDetail.quota ?: 0
            val registrants = eventDetail.registrants ?: 0

            val percentage = if (quota > 0) {
                (registrants.toDouble() / quota) * 100
            } else {
                0.0
            }

            val htmlDescription = eventDetail.description ?: ""

            Glide.with(root.context)
                .load(eventDetail.mediaCover)
                .error(R.drawable.baseline_broken_image_24)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        shimmerViewContainer.stopShimmer()
                        shimmerViewContainer.visibility = View.GONE

                        imageEvent.visibility = View.VISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        shimmerViewContainer.stopShimmer()
                        shimmerViewContainer.visibility = View.GONE

                        imageEvent.visibility = View.VISIBLE
                        return false
                    }
                })
                .into(binding.imageEvent)

            tvCategory.text = eventDetail.category
            tvEventName.text = eventDetail.name
            tvOwnerName.text = eventDetail.ownerName
            tvLocation.text = eventDetail.cityName

            tvTimeMain.text = time.timeMain
            tvTimeSub.text = time.timeSub

            tvRegistrants.text = getString(R.string.registrants_format, registrants, quota)
            tvProgressPercentage.text = getString(R.string.progress_percentage, percentage)

            progressBar.progress = percentage.toInt().coerceIn(0, 100)

            tvSummary.text = eventDetail.summary
//            tvDescription.text = HtmlCompat.fromHtml(
//                htmlDescription,
//                HtmlCompat.FROM_HTML_MODE_LEGACY
//            )
            tvDescription.setHtml(
                htmlDescription,
                HtmlHttpImageGetter(tvDescription)
            )
        }
    }
}
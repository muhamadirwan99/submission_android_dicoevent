package com.dicoding.dicoevent.ui.upcoming

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicoevent.R
import com.dicoding.dicoevent.databinding.FragmentUpcomingBinding
import com.dicoding.dicoevent.ui.adapter.EventHorizontalAdapter
import com.dicoding.dicoevent.ui.adapter.EventVerticalAdapter
import com.dicoding.dicoevent.ui.home.HomeFragmentDirections
import com.dicoding.dicoevent.ui.home.HomeListUpcomingAdapter
import com.dicoding.dicoevent.ui.home.HomeViewModel
import com.dicoding.dicoevent.utils.DisplayUtils
import com.dicoding.dicoevent.utils.UiState
import com.dicoding.dicoevent.utils.openUrl
import com.google.android.material.search.SearchView
import kotlin.getValue


class UpcomingFragment : Fragment() {

    private lateinit var binding: FragmentUpcomingBinding
    private val viewModel by viewModels<UpcomingViewModel>()
    private lateinit var upcomingAdapter: EventHorizontalAdapter
    private lateinit var searchAdapter: EventVerticalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = binding.searchBar.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = systemBars.top + DisplayUtils.convertDpToPixel(requireContext())
            binding.searchBar.layoutParams = layoutParams

            insets
        }
        setupRecyclerViews()
        observeViewModel()
        setupSearch()

        val layoutFinishedManager = LinearLayoutManager(requireContext())
        binding.rvUpcomingEvents.layoutManager = layoutFinishedManager
    }

    private fun setupRecyclerViews() {
        val navigateToDetail: (Int) -> Unit = { eventId ->
            val action = UpcomingFragmentDirections.actionNavigationUpcomingToDetailActivity(eventId)
            findNavController().navigate(action)
        }

        upcomingAdapter = EventHorizontalAdapter(
            onItemClick = navigateToDetail,

            )

        searchAdapter = EventVerticalAdapter(
            onItemClick = navigateToDetail

        )

        with(binding) {
            rvUpcomingEvents.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = upcomingAdapter
            }

            rvSearchResults.adapter = searchAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.upcomingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    with(binding) {
                        rvUpcomingEvents.visibility = View.GONE
                        layoutError.layoutError.visibility = View.GONE
                        shimmerUpcomingEvents.shimmerViewContainer.visibility = View.VISIBLE
                    }
                }

                is UiState.Success -> {
                    with(binding) {
                        rvUpcomingEvents.visibility = View.VISIBLE
                        layoutError.layoutError.visibility = View.GONE
                        shimmerUpcomingEvents.shimmerViewContainer.visibility = View.GONE
                    }

                    upcomingAdapter.submitList(state.data)
                }

                is UiState.Error -> {
                    with(binding) {
                        rvUpcomingEvents.visibility = View.GONE
                        shimmerUpcomingEvents.shimmerViewContainer.visibility = View.GONE
                        layoutError.layoutError.visibility = View.VISIBLE
                        layoutError.tvErrorMessage.text = state.errorMessage
                        layoutError.btnRetry.setOnClickListener {
                            viewModel.getListUpcomingEvents()
                        }
                    }
                }
            }
        }

        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    with(binding) {
                        rvSearchResults.visibility = View.GONE
                        shimmerSearch.shimmerViewContainer.visibility = View.VISIBLE
                        tvEmptySearch.visibility = View.GONE
                    }
                }

                is UiState.Success -> {
                    with(binding) {
                        if (state.data.isEmpty()) {
                            rvSearchResults.visibility = View.GONE
                            shimmerSearch.shimmerViewContainer.visibility = View.GONE
                            tvEmptySearch.visibility = View.VISIBLE
                        }

                        rvSearchResults.visibility = View.VISIBLE
                        shimmerSearch.shimmerViewContainer.visibility = View.GONE
                        tvEmptySearch.visibility = View.GONE

                        searchAdapter.submitList(state.data)
                    }
                }

                is UiState.Error -> {
                    with(binding) {
                        rvSearchResults.visibility = View.GONE
                        shimmerSearch.shimmerViewContainer.visibility = View.GONE
                        tvEmptySearch.visibility = View.VISIBLE
                    }
                }

            }
        }
    }

    private fun setupSearch() {
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { _, _, _ ->
                    searchBar.setText(searchView.text)
                    viewModel.searchEvents(searchView.text.toString())

                    false
                }

            searchView.addTransitionListener { _, _, newState ->
                if (newState == SearchView.TransitionState.HIDDEN) {
                    searchAdapter.submitList(emptyList())
                    tvEmptySearch.visibility = View.GONE
                    rvSearchResults.visibility = View.VISIBLE
                    searchBar.setText("")
                }
            }
        }
    }
}
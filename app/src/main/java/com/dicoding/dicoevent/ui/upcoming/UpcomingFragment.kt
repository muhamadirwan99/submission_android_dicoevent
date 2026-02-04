package com.dicoding.dicoevent.ui.upcoming

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.dicoding.dicoevent.utils.textChangesAsFlow
import com.google.android.material.search.SearchView
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
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
        // === 1. OBSERVE UPCOMING STATE ===
        viewModel.upcomingState.observe(viewLifecycleOwner) { state ->
            with(binding) {
                shimmerUpcomingEvents.shimmerViewContainer.isVisible = state is UiState.Loading
                rvUpcomingEvents.isVisible = state is UiState.Success
                layoutError.root.isVisible = state is UiState.Error

                when (state) {
                    is UiState.Success -> {
                        upcomingAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        layoutError.tvErrorMessage.text = state.errorMessage
                        layoutError.btnRetry.setOnClickListener {
                            viewModel.getListUpcomingEvents()
                        }
                    }
                    else -> {}
                }
            }
        }

        // === 2. OBSERVE SEARCH STATE ===
        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            with(binding) {
                shimmerSearch.shimmerViewContainer.isVisible = state is UiState.Loading

                when (state) {
                    is UiState.Loading -> {
                        rvSearchResults.isVisible = false
                        tvEmptySearch.isVisible = false
                    }

                    is UiState.Success -> {
                        val isDataEmpty = state.data.isEmpty()
                        rvSearchResults.isVisible = !isDataEmpty
                        tvEmptySearch.isVisible = isDataEmpty
                        searchAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        rvSearchResults.isVisible = false
                        tvEmptySearch.isVisible = true
                    }
                }
            }
        }
    }

    private fun setupSearch() {
        binding.searchView.setupWithSearchBar(binding.searchBar)

        lifecycleScope.launch {
            binding.searchView.editText.textChangesAsFlow()
                .debounce(500)
                .distinctUntilChanged()
                .filter { it.isNotEmpty() }
                .collect { query ->
                    Log.d("Search", "Mencari: $query")
                    viewModel.searchEvents(query)
                }
        }

        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                binding.searchBar.setText("")
                viewModel.clearSearch()
            }
        }
    }
}
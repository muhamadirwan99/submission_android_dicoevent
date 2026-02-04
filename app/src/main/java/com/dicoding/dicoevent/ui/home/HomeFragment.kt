package com.dicoding.dicoevent.ui.home

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
import com.dicoding.dicoevent.databinding.FragmentHomeBinding
import com.dicoding.dicoevent.ui.adapter.EventVerticalAdapter
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

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var finishedAdapter: EventVerticalAdapter
    private lateinit var upcomingAdapter: HomeListUpcomingAdapter
    private lateinit var searchAdapter: EventVerticalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
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
        binding.rvFinishedEvents.layoutManager = layoutFinishedManager

        val layoutUpcomingManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvUpcomingEvents.layoutManager = layoutUpcomingManager
    }

    private fun setupRecyclerViews() {
        finishedAdapter = EventVerticalAdapter(
            onItemClick = ::navigateToDetailFinished
        )
        upcomingAdapter = HomeListUpcomingAdapter(
            onItemClick = ::navigateToDetail,
            onRegisterClick = { link ->
                requireContext().openUrl(link)
            }
        )
        searchAdapter = EventVerticalAdapter(
            onItemClick = ::navigateToDetail
        )

        with(binding) {
            rvFinishedEvents.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = finishedAdapter
            }

            rvUpcomingEvents.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = upcomingAdapter
            }

            rvSearchResults.adapter = searchAdapter
        }
    }

    private fun navigateToDetail(eventId: Int) {
        val action = HomeFragmentDirections.actionNavigationHomeToDetailActivity(eventId)
        findNavController().navigate(action)
    }

    private fun navigateToDetailFinished(eventId: Int) {
        val action = HomeFragmentDirections.actionNavigationHomeToDetailActivity(eventId, false)
        findNavController().navigate(action)
    }

    private fun observeViewModel() {
        // === 1. UPCOMING SECTION ===
        homeViewModel.upcomingState.observe(viewLifecycleOwner) { state ->
            with(binding) {
                shimmerUpcoming.shimmerViewUpcoming.isVisible = state is UiState.Loading
                rvUpcomingEvents.isVisible = state is UiState.Success
                layoutErrorUpcoming.root.isVisible = state is UiState.Error

                when (state) {
                    is UiState.Success -> {
                        upcomingAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        layoutErrorUpcoming.tvErrorMessage.text = state.errorMessage
                        layoutErrorUpcoming.btnRetry.setOnClickListener {
                            homeViewModel.getListUpcomingEvents()
                        }
                    }

                    else -> {}
                }
            }
        }

        // === 2. FINISHED SECTION ===
        homeViewModel.finishedState.observe(viewLifecycleOwner) { state ->
            with(binding) {
                shimmerFinished.shimmerViewFinished.isVisible = state is UiState.Loading
                rvFinishedEvents.isVisible = state is UiState.Success
                layoutErrorFinished.root.isVisible = state is UiState.Error

                when (state) {
                    is UiState.Success -> {
                        finishedAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        layoutErrorFinished.tvErrorMessage.text = state.errorMessage
                        layoutErrorFinished.btnRetry.setOnClickListener {
                            homeViewModel.getListFinishedEvents()
                        }
                    }

                    else -> {}
                }
            }
        }

        // === 3. SEARCH SECTION ===
        homeViewModel.searchState.observe(viewLifecycleOwner) { state ->
            with(binding) {
                shimmerSearch.shimmerViewFinished.isVisible = state is UiState.Loading

                if (state is UiState.Error) {
                    rvSearchResults.isVisible = false
                    tvEmptySearch.isVisible = true
                }

                if (state is UiState.Success) {
                    val isDataEmpty = state.data.isEmpty()
                    rvSearchResults.isVisible = !isDataEmpty
                    tvEmptySearch.isVisible = isDataEmpty

                    searchAdapter.submitList(state.data)
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupSearch() {
        binding.searchView.setupWithSearchBar(binding.searchBar)

        lifecycleScope.launch {
            binding.searchView.editText.textChangesAsFlow()
                .debounce(500)
                .distinctUntilChanged()
                .filter { it.isNotEmpty() }
                .collect { query ->
                    Log.d("Search", "Mencari: $query")
                    homeViewModel.searchEvents(query)
                }
        }

        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                binding.searchBar.setText("")
                homeViewModel.clearSearch()
            }
        }
    }
}
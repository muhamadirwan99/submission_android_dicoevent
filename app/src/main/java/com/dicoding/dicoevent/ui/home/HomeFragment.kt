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
import com.dicoding.dicoevent.data.Result
import com.dicoding.dicoevent.databinding.FragmentHomeBinding
import com.dicoding.dicoevent.ui.ViewModelFactory
import com.dicoding.dicoevent.ui.adapter.EventVerticalAdapter
import com.dicoding.dicoevent.utils.DisplayUtils
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
    private val homeViewModel : HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
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
        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                with(binding) {
                    shimmerUpcoming.shimmerViewUpcoming.isVisible = result is Result.Loading
                    rvUpcomingEvents.isVisible = result is Result.Success
                    layoutErrorUpcoming.root.isVisible = result is Result.Error

                    when (result) {
                        is Result.Success -> {
                            upcomingAdapter.submitList(result.data)
                        }

                        is Result.Error -> {
                            layoutErrorUpcoming.tvErrorMessage.text = result.error
                        }

                        else -> {}
                    }
                }
            }
        }

        // === 2. FINISHED SECTION ===
        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { result ->
            with(binding) {
                shimmerFinished.shimmerViewFinished.isVisible = result is Result.Loading
                rvFinishedEvents.isVisible = result is Result.Success
                layoutErrorFinished.root.isVisible = result is Result.Error

                when (result) {
                    is Result.Success -> {
                        finishedAdapter.submitList(result.data)
                    }

                    is Result.Error -> {
                        layoutErrorFinished.tvErrorMessage.text = result.error
                    }

                    else -> {}
                }
            }
        }

        // === 3. SEARCH SECTION ===
        homeViewModel.searchState.observe(viewLifecycleOwner) { result ->
            with(binding) {
                shimmerSearch.shimmerViewFinished.isVisible = result is Result.Loading

                if (result is Result.Error) {
                    rvSearchResults.isVisible = false
                    tvEmptySearch.isVisible = true
                }

                if (result is Result.Success) {
                    val isDataEmpty = result.data.isEmpty()
                    rvSearchResults.isVisible = !isDataEmpty
                    tvEmptySearch.isVisible = isDataEmpty

                    searchAdapter.submitList(result.data)
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
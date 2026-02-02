package com.dicoding.dicoevent.ui.home

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicoevent.databinding.FragmentHomeBinding
import com.dicoding.dicoevent.ui.adapter.EventVerticalAdapter
import com.google.android.material.search.SearchView

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var finishedAdapter: EventVerticalAdapter
    private lateinit var upcomingAdapter: HomeListUpcomingAdapter
    private lateinit var searchAdapter: EventVerticalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = binding.searchBar.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = systemBars.top + convertDpToPixel(requireContext())
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

    private fun convertDpToPixel(context: Context): Int {
        return (16f * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    private fun setupRecyclerViews() {
        finishedAdapter = EventVerticalAdapter()
        upcomingAdapter = HomeListUpcomingAdapter()
        searchAdapter = EventVerticalAdapter()

        binding.rvFinishedEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = finishedAdapter
        }

        binding.rvUpcomingEvents.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
        }

        binding.rvSearchResults.adapter = searchAdapter
    }

    private fun observeViewModel() {
        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            finishedAdapter.submitList(events)
        }

        homeViewModel.isLoadingFinished.observe(viewLifecycleOwner) { isLoading ->
            showLoadingFinished(isLoading)
        }

        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            upcomingAdapter.submitList(events)
        }

        homeViewModel.isLoadingUpcoming.observe(viewLifecycleOwner) { isLoading ->
            showLoadingUpcoming(isLoading)
        }

        homeViewModel.searchEvents.observe(viewLifecycleOwner) { events ->
            searchAdapter.submitList(events)

            binding.apply {
                if (events.isEmpty()) {
                    tvEmptySearch.visibility = View.VISIBLE
                    rvSearchResults.visibility = View.GONE
                } else {
                    tvEmptySearch.visibility = View.GONE
                    rvSearchResults.visibility = View.VISIBLE
                }
            }
        }

        homeViewModel.isLoadingSearch.observe(viewLifecycleOwner) { isLoading ->
            showLoadingSearch(isLoading)
        }

        homeViewModel.snackbarText.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->

                com.google.android.material.snackbar.Snackbar.make(
                    binding.root,
                    message,
                    com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setupSearch() {
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { textView, actionId, event ->
                    searchBar.setText(searchView.text)
                    homeViewModel.searchEvents(searchView.text.toString())

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

    private fun toggleLoading(
        isLoading: Boolean,
        shimmerContainer: com.facebook.shimmer.ShimmerFrameLayout,
        recyclerView: androidx.recyclerview.widget.RecyclerView
    ) {
        if (isLoading) {
            shimmerContainer.visibility = View.VISIBLE
            shimmerContainer.startShimmer()
            recyclerView.visibility = View.GONE
        } else {
            shimmerContainer.stopShimmer()
            shimmerContainer.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun showLoadingFinished(isLoading: Boolean) {
        val shimmerView = binding.shimmerFinished.root
        toggleLoading(isLoading, shimmerView, binding.rvFinishedEvents)
    }

    private fun showLoadingUpcoming(isLoading: Boolean) {
        val shimmerView = binding.shimmerUpcoming.root
        toggleLoading(isLoading, shimmerView, binding.rvUpcomingEvents)
    }

    private fun showLoadingSearch(isLoading: Boolean) {
        val shimmerView = binding.shimmerSearch.root
        toggleLoading(isLoading, shimmerView, binding.rvSearchResults)
    }
}
package com.dicoding.dicoevent.ui.finished

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
import com.dicoding.dicoevent.databinding.FragmentFinishedBinding
import com.dicoding.dicoevent.ui.adapter.EventHorizontalAdapter
import com.dicoding.dicoevent.ui.adapter.EventVerticalAdapter
import com.dicoding.dicoevent.utils.DisplayUtils
import com.dicoding.dicoevent.utils.UiState
import com.dicoding.dicoevent.utils.textChangesAsFlow
import com.google.android.material.search.SearchView
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlin.getValue


class FinishedFragment : Fragment() {

    private lateinit var binding: FragmentFinishedBinding
    private val viewModel by viewModels<FinishedViewModel>()
    private lateinit var finishedAdapter: EventHorizontalAdapter
    private lateinit var searchAdapter: EventVerticalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinishedBinding.inflate(inflater, container, false)
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
    }

    private fun setupRecyclerViews() {
        finishedAdapter = EventHorizontalAdapter(
            onItemClick = ::navigateToDetail
        )

        searchAdapter = EventVerticalAdapter(
            onItemClick = ::navigateToDetail
        )

        with(binding) {
            rvFinishedEvents.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = finishedAdapter
            }

            rvSearchResults.adapter = searchAdapter
        }
    }

    private fun navigateToDetail(eventId: Int) {
        val action = FinishedFragmentDirections.actionNavigationFinishedToDetailActivity(eventId)
        findNavController().navigate(action)
    }

    private fun observeViewModel() {
        // === 1. OBSERVE UPCOMING STATE ===
        viewModel.finishedState.observe(viewLifecycleOwner) { state ->
            with(binding) {
                shimmerFinishedEvents.shimmerViewContainer.isVisible = state is UiState.Loading
                rvFinishedEvents.isVisible = state is UiState.Success
                layoutError.root.isVisible = state is UiState.Error

                when (state) {
                    is UiState.Success -> {
                        finishedAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        layoutError.tvErrorMessage.text = state.errorMessage
                        layoutError.btnRetry.setOnClickListener {
                            viewModel.getListFinishedEvents()
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
            binding.searchView.editText.textChangesAsFlow().debounce(500).distinctUntilChanged()
                .filter { it.isNotEmpty() }.collect { query ->
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
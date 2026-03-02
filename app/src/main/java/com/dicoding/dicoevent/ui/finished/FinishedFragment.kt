package com.dicoding.dicoevent.ui.finished

import com.dicoding.dicoevent.data.Result
import android.os.Bundle
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
import com.dicoding.dicoevent.ui.ViewModelFactory
import com.dicoding.dicoevent.ui.adapter.EventHorizontalAdapter
import com.dicoding.dicoevent.ui.adapter.EventVerticalAdapter
import com.dicoding.dicoevent.utils.DisplayUtils
import com.dicoding.dicoevent.utils.textChangesAsFlow
import com.google.android.material.search.SearchView
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlin.getValue

class FinishedFragment : Fragment() {

    private lateinit var binding: FragmentFinishedBinding
    private val viewModel : FinishedViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
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
        with(binding) {
            rvFinishedEvents.layoutManager = layoutFinishedManager
            layoutError.btnRetry.setOnClickListener {
                viewModel.refresh()
            }
        }
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
        viewModel.finishedEvents.observe(viewLifecycleOwner) { result ->
            with(binding) {
                shimmerFinishedEvents.shimmerViewEvent.isVisible = result is Result.Loading
                rvFinishedEvents.isVisible = result is Result.Success
                layoutError.root.isVisible = result is Result.Error

                when (result) {
                    is Result.Success -> {
                        finishedAdapter.submitList(result.data)
                    }

                    is Result.Error -> {
                        layoutError.tvErrorMessage.text = result.error
                    }

                    else -> {}
                }
            }
        }

        // === 2. OBSERVE SEARCH STATE ===
        viewModel.searchState.observe(viewLifecycleOwner) { result ->
            with(binding) {
                shimmerSearch.shimmerViewFinished.isVisible = result is Result.Loading

                when (result) {
                    is Result.Loading -> {
                        rvSearchResults.isVisible = false
                        tvEmptySearch.isVisible = false
                    }

                    is Result.Success -> {
                        val isDataEmpty = result.data.isEmpty()
                        rvSearchResults.isVisible = !isDataEmpty
                        tvEmptySearch.isVisible = isDataEmpty
                        searchAdapter.submitList(result.data)
                    }

                    is Result.Error -> {
                        rvSearchResults.isVisible = false
                        tvEmptySearch.isVisible = true
                    }
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupSearch() {
        binding.searchView.setupWithSearchBar(binding.searchBar)

        lifecycleScope.launch {
            binding.searchView.editText.textChangesAsFlow().debounce(500).distinctUntilChanged()
                .filter { it.isNotEmpty() }.collect { query ->
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
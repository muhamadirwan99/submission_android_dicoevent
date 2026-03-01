package com.dicoding.dicoevent.ui.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicoevent.databinding.FragmentFavoriteBinding
import com.dicoding.dicoevent.ui.ViewModelFactory
import com.dicoding.dicoevent.ui.adapter.EventVerticalAdapter
import com.dicoding.dicoevent.utils.DisplayUtils
import kotlin.getValue


class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var adapter: EventVerticalAdapter

    private val viewModel: FavoriteViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.rvFavoriteEvents.setPadding(
                0,
                systemBars.top + DisplayUtils.convertDpToPixel(requireContext()),
                0,
                systemBars.bottom
            )
            insets
        }

        setupRecyclerViews()
        observeViewModel()

        val layoutFinishedManager = LinearLayoutManager(requireContext())
        binding.rvFavoriteEvents.layoutManager = layoutFinishedManager
    }

    private fun setupRecyclerViews() {
        adapter = EventVerticalAdapter(
            onItemClick = ::navigateToDetail
        )

        with(binding) {
            rvFavoriteEvents.adapter = adapter
        }
    }

    private fun navigateToDetail(eventId: Int) {
        val action = FavoriteFragmentDirections.actionFavoriteFragmentToDetailActivity(eventId)
        findNavController().navigate(action)
    }

    private fun observeViewModel() {
        viewModel.favoriteEvents.observe(viewLifecycleOwner) { result ->
            if (!result.isNullOrEmpty()) {
                binding.layoutNoData.isVisible = false
                adapter.submitList(result)
            } else {
                binding.layoutNoData.isVisible = true
                adapter.submitList(emptyList())
            }
        }
    }
}
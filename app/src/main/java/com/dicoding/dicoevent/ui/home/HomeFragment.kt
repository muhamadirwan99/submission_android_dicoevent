package com.dicoding.dicoevent.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicoevent.data.response.ListEventsItem
import com.dicoding.dicoevent.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            setUpFinishedEventData(events)
        }

        homeViewModel.isLoadingFinished.observe(viewLifecycleOwner) { isLoading ->
            showLoadingFinished(isLoading)
        }

        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            setUpComingEventData(events)
        }

        homeViewModel.isLoadingUpcoming.observe(viewLifecycleOwner) { isLoading ->
            showLoadingUpcoming(isLoading)
        }

        val layoutFinishedManager = LinearLayoutManager(requireContext())
        binding.rvFinishedEvents.layoutManager = layoutFinishedManager

        val layoutUpcomingManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvUpcomingEvents.layoutManager = layoutUpcomingManager
    }

    private fun setUpFinishedEventData(eventsData: List<ListEventsItem>) {
        val adapter = HomeListFinishedAdapter()

        adapter.submitList(eventsData)
        binding.rvFinishedEvents.adapter = adapter
    }

    private fun setUpComingEventData(eventsData: List<ListEventsItem>) {
        val adapter = HomeListUpcomingAdapter()

        adapter.submitList(eventsData)
        binding.rvUpcomingEvents.adapter = adapter
    }

    private fun showLoadingFinished(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerFinished.root.visibility = View.VISIBLE
            binding.shimmerFinished.root.startShimmer()

            binding.rvFinishedEvents.visibility = View.GONE
        } else {
            binding.shimmerFinished.root.stopShimmer()
            binding.shimmerFinished.root.visibility = View.GONE

            binding.rvFinishedEvents.visibility = View.VISIBLE
        }
    }

    private fun showLoadingUpcoming(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerUpcoming.root.visibility = View.VISIBLE
            binding.shimmerUpcoming.root.startShimmer()

            binding.rvUpcomingEvents.visibility = View.GONE
        } else {
            binding.shimmerUpcoming.root.stopShimmer()
            binding.shimmerUpcoming.root.visibility = View.GONE

            binding.rvUpcomingEvents.visibility = View.VISIBLE
        }
    }
}
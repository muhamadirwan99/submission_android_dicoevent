package com.dicoding.dicoevent.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicoevent.R
import com.dicoding.dicoevent.data.response.ListEventsItem
import com.dicoding.dicoevent.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel>()

    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.events.observe(viewLifecycleOwner) { events ->
            setEventData(events)
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvFinishedEvents.layoutManager = layoutManager

        binding.tvDicodingEvent

    }

    private fun setEventData(eventsData: List<ListEventsItem>) {
        val adapter = HomeListFinishedAdapter()
        // 1. Inisialisasi sebagai ArrayList (Wadah yang bisa diedit)
        val listData = ArrayList<ListEventsItem>()

        // 2. Masukkan semua data dari parameter ke dalam listData
        listData.addAll(eventsData)
        listData.addAll(eventsData)

        adapter.submitList(listData)
        binding.rvFinishedEvents.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading){
            binding.layoutShimmer.root.visibility = View.VISIBLE
            binding.layoutShimmer.root.startShimmer()

            binding.rvFinishedEvents.visibility = View.GONE
        } else {
            binding.layoutShimmer.root.stopShimmer()
            binding.layoutShimmer.root.visibility = View.GONE

            binding.rvFinishedEvents.visibility = View.VISIBLE
        }
    }
}
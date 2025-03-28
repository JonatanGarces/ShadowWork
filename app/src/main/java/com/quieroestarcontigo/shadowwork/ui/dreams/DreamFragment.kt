package com.quieroestarcontigo.shadowwork.ui.dreams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.quieroestarcontigo.shadowwork.R
import com.quieroestarcontigo.shadowwork.databinding.FragmentDreamBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class DreamFragment : Fragment() {

    private var _binding: FragmentDreamBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DreamViewModel by viewModels()
    private val eventViewModel: DreamEventViewModel by activityViewModels()
    private lateinit var adapter: DreamAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding  = FragmentDreamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = DreamAdapter()
        binding.dreamRecyclerView.adapter = adapter

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.dreams.collect { dreamList ->
                        adapter.submitList(dreamList)
                    }
                }

                launch {
                    viewModel.isLoading.collect { loading ->
                        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.unsyncedCount.collect { count ->
                        val badge = bottomNav.getOrCreateBadge(R.id.navigation_dreams)
                        if (count > 0) {
                            badge.isVisible = true
                            badge.number = count
                        } else {
                            badge.clearNumber()
                            badge.isVisible = false
                        }
                    }
                }

                launch {
                    eventViewModel.dreamSaved.collect {
                    }
                }
            }
        }

        // Sync button click
        binding.syncButton.setOnClickListener {
            viewModel.syncDreams()
        }

        // Add new dream
        binding.fabAddDream.setOnClickListener {
            findNavController().navigate(R.id.action_dreamFragment_to_addDreamFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding  = null
    }
}

package com.example.dicodingstory.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingstory.R
import com.example.dicodingstory.databinding.FragmentHomeBinding
import com.example.dicodingstory.injection.Injection
import com.example.dicodingstory.ui.addstory.AddStoryActivity
import com.example.dicodingstory.ui.auth.SignInActivity
import com.example.dicodingstory.ui.profile.ProfileActivity
import com.example.dicodingstory.utils.UserPreferences
import com.example.dicodingstory.utils.dataStore
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var adapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = Injection.provideRepository(requireContext())
        val factory = HomeFragmentViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeFragmentViewModel::class.java)

        setupRecyclerView()

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observing listStory to update RecyclerView
        viewModel.listStory.observe(viewLifecycleOwner) { listStory ->
            if (listStory != null && listStory.isNotEmpty()) {
                Log.d("HomeFragment", "Updating adapter with ${listStory.size} stories.")
                adapter.setList(listStory)
            } else {
                Log.d("HomeFragment", "Story list is empty or null.")
                Toast.makeText(requireContext(), "No stories available. Please Login First", Toast.LENGTH_SHORT).show()            }
        }

        // Fetch stories using token from UserPreferences
        val userPreferences = UserPreferences.getInstance(requireContext().dataStore)
        viewLifecycleOwner.lifecycleScope.launch {
            userPreferences.getUser().collect { userData ->
                val token = userData.token
                if (token.isNotEmpty()) {
                    Log.d("HomeFragment", "Fetching stories with token: $token")
                    viewModel.getStories(token)
                    setProfileButton(true)
                } else {
                    setProfileButton(false)
                }
            }
        }

        binding.uploadButton.setOnClickListener {
            val intent = Intent(requireContext(), AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setProfileButton(isLoggedIn: Boolean) {
        binding.accountButton.setOnClickListener {
            val intent = if (isLoggedIn) {
                Intent(requireContext(), ProfileActivity::class.java)
            } else {
                Intent(requireContext(), SignInActivity::class.java)
            }
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        adapter = StoryAdapter()
        binding.rvStories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStories.adapter = adapter

        val slideInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_left)
        binding.rvStories.startAnimation(slideInAnimation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

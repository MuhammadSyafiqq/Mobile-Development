package com.example.dicodingstory.ui.detailstory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dicodingstory.data.ListStoryItem
import com.example.dicodingstory.databinding.ActivityDetailStoryBinding

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    companion object {
        const val EXTRA_STORY = "extra_story"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the story item from intent
        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)

        // Populate the views with story details
        story?.let {
            binding.tvDetailName.text = it.name
            binding.tvDetailDeskripsi.text = it.description

            Glide.with(this)
                .load(it.photoUrl)
                .into(binding.ivItemPhoto)
        }

        // Optional: Add back button functionality
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
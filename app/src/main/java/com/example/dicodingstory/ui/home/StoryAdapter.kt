package com.example.dicodingstory.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingstory.R
import com.example.dicodingstory.data.ListStoryItem
import com.example.dicodingstory.ui.detailstory.DetailStoryActivity

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    private val storyList = mutableListOf<ListStoryItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(storyList[position])
    }

    override fun getItemCount(): Int = storyList.size

    fun setList(newList: List<ListStoryItem>) {
        storyList.clear()
        storyList.addAll(newList)
        notifyDataSetChanged()
    }

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivStoryImage: ImageView = itemView.findViewById(R.id.iv_item_photo)
        private val tvStoryName: TextView = itemView.findViewById(R.id.tv_event_name)
        private val tvStoryDescription: TextView = itemView.findViewById(R.id.tv_story_deskripsi)

        fun bind(story: ListStoryItem) {
            tvStoryName.text = story.name
            tvStoryDescription.text = story.description
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(ivStoryImage)

            // Add click listener to the entire item view
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_STORY, story)
                context.startActivity(intent)
            }
        }
    }
}
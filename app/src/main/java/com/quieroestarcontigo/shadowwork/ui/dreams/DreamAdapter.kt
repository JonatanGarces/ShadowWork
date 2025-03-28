package com.quieroestarcontigo.shadowwork.ui.dreams

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quieroestarcontigo.shadowwork.data.model.Dream
import com.quieroestarcontigo.shadowwork.databinding.DreamItemBinding

class DreamAdapter : ListAdapter<Dream, DreamAdapter.DreamViewHolder>(DreamDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DreamViewHolder {
        val binding = DreamItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DreamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DreamViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    class DreamViewHolder(private val binding: DreamItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dream: Dream) {
            binding.titleTextView.text = dream.tags
            binding.descriptionTextView.text = dream.content
            binding.timestampTextView.text = dream.created_at
            binding.syncStatusIcon.isVisible = !dream.synced

        }
    }

    class DreamDiffCallback : DiffUtil.ItemCallback<Dream>() {
        override fun areItemsTheSame(oldItem: Dream, newItem: Dream): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Dream, newItem: Dream): Boolean = oldItem == newItem
    }
}

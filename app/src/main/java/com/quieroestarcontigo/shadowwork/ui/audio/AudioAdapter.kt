package com.quieroestarcontigo.shadowwork.ui.audio

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quieroestarcontigo.shadowwork.data.model.AudioRecord
import com.quieroestarcontigo.shadowwork.databinding.ItemAudioBinding

class AudioAdapter(
    private val onTranscribeClicked: (AudioRecord) -> Unit,
    private val onTextChanged: (AudioRecord, String) -> Unit,
) : ListAdapter<AudioRecord, AudioAdapter.AudioViewHolder>(DiffCallback) {

    inner class AudioViewHolder(private val binding: ItemAudioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(record: AudioRecord) {
            binding.tvFilename.text = record.filePath.substringAfterLast("/")
            binding.etTranscription.setText(record.transcription)

            binding.btnTranscribe.setOnClickListener {
                onTranscribeClicked(record)
            }

            binding.etTranscription.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    onTextChanged(record, s.toString())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val binding = ItemAudioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AudioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<AudioRecord>() {
        override fun areItemsTheSame(oldItem: AudioRecord, newItem: AudioRecord): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AudioRecord, newItem: AudioRecord): Boolean =
            oldItem == newItem
    }
}

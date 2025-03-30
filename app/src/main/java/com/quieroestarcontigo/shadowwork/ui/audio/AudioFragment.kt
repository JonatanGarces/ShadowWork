package com.quieroestarcontigo.shadowwork.ui.audio

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.quieroestarcontigo.shadowwork.databinding.FragmentAudioBinding
import com.quieroestarcontigo.shadowwork.data.model.AudioRecord
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AudioFragment : Fragment() {

    private var _binding: FragmentAudioBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AudioViewModel by viewModels()

    private var isRecording = false

    private lateinit var adapter: AudioAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAudioBinding.inflate(inflater, container, false)

        adapter = AudioAdapter(
            onTranscribeClicked = { record -> viewModel.transcribeAudio(record) },
            onTextChanged = { record, newText -> viewModel.updateTranscription(record.id, newText) }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AudioFragment.adapter
        }

        binding.fabRecord.setOnClickListener {
            if (!hasMicPermission()) {
                requestPermissions()
                return@setOnClickListener
            }

            if (!isRecording) {
                val path = viewModel.startRecording()
                Toast.makeText(requireContext(), "🎙 Grabando...", Toast.LENGTH_SHORT).show()
                isRecording = true
                binding.fabRecord.text = "⏹ Detener"
            } else {
                val record = viewModel.stopRecording()
                if (record != null) {
                    lifecycleScope.launch {
                        viewModel.saveRecord(record)
                    }
                    Toast.makeText(requireContext(), "✅ Audio guardado", Toast.LENGTH_SHORT).show()
                }
                isRecording = false
                binding.fabRecord.text = "🎙 Grabar"
            }
        }

        viewModel.audioRecords.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
    }

    private fun hasMicPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            1001
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

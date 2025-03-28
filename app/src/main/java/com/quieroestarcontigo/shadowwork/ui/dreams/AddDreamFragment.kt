package com.quieroestarcontigo.shadowwork.ui.dreams

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.quieroestarcontigo.shadowwork.databinding.FragmentAddDreamBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class AddDreamFragment : Fragment() {

    private var _binding: FragmentAddDreamBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DreamViewModel by viewModels()
    private val eventViewModel: DreamEventViewModel by activityViewModels()

    // Optionally inject your DreamViewModel here if saving to DB
    // private val viewModel: DreamViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddDreamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.saveButton.setOnClickListener {
            val content = binding.dreamEditText.text.toString().trim()
            val selectedTags = mutableListOf<String>()

            for (i in 0 until binding.tagChipGroup.childCount) {
                val chip = binding.tagChipGroup.getChildAt(i) as? Chip
                if (chip?.isChecked == true) {
                    selectedTags.add(chip.text.toString())
                }
            }

            if (content.isNotEmpty()) {
                viewModel.addDream(content, selectedTags)
                eventViewModel.notifyDreamSaved()

                Toast.makeText(requireContext(), "Dream saved 💤", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Write something first 😴", Toast.LENGTH_SHORT).show()
            }
        }

        binding.micButton.setOnClickListener {
            startSpeechToText()
        }
// After save:

    }
    private val REQUEST_CODE_SPEECH_INPUT = 100

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora...")

        speechRecognizerLauncher.launch(intent)
    }

    private val speechRecognizerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val resultList = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = resultList?.get(0)
            binding.dreamEditText.append("$spokenText ")
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

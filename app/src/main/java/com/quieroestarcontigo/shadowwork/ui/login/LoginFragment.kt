package com.quieroestarcontigo.shadowwork.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.quieroestarcontigo.shadowwork.MainActivity
import com.quieroestarcontigo.shadowwork.ui.auth.AuthViewModel
import com.quieroestarcontigo.shadowwork.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    // ✅ ViewModel injected via Hilt
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // ✅ Observe session
        viewModel.session.observe(viewLifecycleOwner) { session ->
            if (session != null) {
                Toast.makeText(requireContext(), "Welcome ${session.email}", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                requireActivity().finish()
            }
        }

        // ✅ Observe login state
        viewModel.isAuthenticated.observe(viewLifecycleOwner) { success ->
            if (!success) {
                Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ Login button
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            viewModel.login(email, password)
        }

        // ✅ Sign up button
        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            viewModel.signup(email, password)
        }
    }
}

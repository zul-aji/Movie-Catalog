package com.example.labproject3.ui.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.labproject3.HomeActivity
import com.example.labproject3.R
import com.example.labproject3.data.TokenPreferences
import com.example.labproject3.databinding.FragmentSigninBinding
import com.example.labproject3.data.network.APIRequest
import com.example.labproject3.data.network.Resource
import com.example.labproject3.data.repository.UserRepo
import com.example.labproject3.data.request.LoginRequest
import com.example.labproject3.ui.base.BaseFragment
import com.example.labproject3.ui.startNewActivity
import com.example.labproject3.ui.visible

class SigninFragment : BaseFragment<UserViewModel, FragmentSigninBinding, UserRepo>() {

    private lateinit var tokenPreferences: TokenPreferences
    private var isAllFieldsChecked = false

    override fun getViewModel() = UserViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSigninBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = UserRepo(dataSource.buildAPI(requireContext(), APIRequest::class.java))


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tokenPreferences = TokenPreferences(requireContext())
        binding.progressCircle.visible(false)

        watchFields()

        viewModel.loginResponse.observe(viewLifecycleOwner) {
            binding.progressCircle.visible(false)
            when (it) {
                is Resource.Success -> {
                    tokenPreferences.setToken(it.value.token)
                    requireActivity().startNewActivity(HomeActivity::class.java)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.login_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        if (it.isNetworkError){ "Network Error" }
                        else { it.errorMessage.toString() },
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.unknown_err),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.signinButton.setOnClickListener {
            val username = binding.signinUsername.text.toString()
            val password = binding.signinPass.text.toString()
            val user = LoginRequest(username, password)

            isAllFieldsChecked = checkAllFields()
            if (isAllFieldsChecked) {
                binding.progressCircle.visible(true)
                viewModel.login(user)
            }
        }

        binding.toSignupButton.setOnClickListener{
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fl_login_fragment, SignupFragment())
                commit()
            }
        }
    }

    private fun watchFields() {
        binding.signinUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonBackground()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.signinPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonBackground()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateButtonBackground() {
        val filled = binding.signinUsername.text.isNotEmpty() && binding.signinPass!!.text.isNotEmpty()
        if (filled) {
            binding.signinButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.gray_faded
                )
            )
            binding.signinButton.background =
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.input_bg_pressed
                )
        }
        else {
            binding.signinButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.accent
                )
            )
            binding.signinButton.background =
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.input_bg
                )
        }
    }

    private fun checkAllFields(): Boolean {
        if (binding.signinUsername.length() == 0) {
            Toast.makeText(requireContext(), getString(R.string.uname_mt), Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.signinPass.length() == 0) {
            Toast.makeText(requireContext(), getString(R.string.pass_mt), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
package com.example.labproject3.ui.user

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.labproject3.HomeActivity
import com.example.labproject3.R
import com.example.labproject3.data.TokenPreferences
import com.example.labproject3.data.network.APIRequest
import com.example.labproject3.data.network.Resource
import com.example.labproject3.data.repository.UserRepo
import com.example.labproject3.data.request.RegisterRequest
import com.example.labproject3.databinding.FragmentSignupBinding
import com.example.labproject3.ui.base.BaseFragment
import com.example.labproject3.ui.startNewActivity
import com.example.labproject3.ui.visible
import java.text.SimpleDateFormat
import java.util.*

class SignupFragment : BaseFragment<UserViewModel, FragmentSignupBinding, UserRepo>() {

    private lateinit var tokenPreferences: TokenPreferences
    private var dateSaved: String = "test"
    private var dateState: Boolean = false
    private var genderInt: Int = 3
    private var isAllFieldsChecked = false

    override fun getViewModel() = UserViewModel::class.java
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSignupBinding.inflate(inflater, container, false)
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
                        getString(R.string.register_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        if (it.isNetworkError){ "Network Error" }
                        else { "Fail: ${it.errorMessage.toString()}" },
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.unknown_err),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.signupButton.setOnClickListener {
            val username = binding.signupUsername.text.toString()
            val email = binding.signupEmail.text.toString()
            val name = binding.signupName.text.toString()
            val password = binding.signupPass.text.toString()
            val birthday = dateSaved
            val gender = genderInt
            val user = RegisterRequest(username, email, name, password, birthday, gender)

            isAllFieldsChecked = checkAllFields()
            if (isAllFieldsChecked){
                binding.progressCircle.visible(true)
                viewModel.register(user)
            }
        }

        binding.alrAccButton.setOnClickListener{
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fl_login_fragment, SigninFragment())
                commit()
            }
        }
    }

    private fun watchFields() {
        binding.signupUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonBackground()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.signupEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonBackground()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.signupName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonBackground()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.signupPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonBackground()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.signupRepeatPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonBackground()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.signupBirthday.setOnClickListener {
            showDateDialog()
            dateState = true
            updateButtonBackground()
        }
        binding.maleButton.setOnClickListener {
            genderInt = 0
            binding.maleButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_male_pressed)
            binding.femaleButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_female)
            updateButtonBackground()
        }
        binding.femaleButton.setOnClickListener {
            genderInt = 1
            binding.femaleButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_female_pressed)
            binding.maleButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_male)
            updateButtonBackground()
        }
    }

    private fun showDateDialog() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val dateFormatShown = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val dateFormatSaved = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                dateFormatSaved.timeZone = TimeZone.getTimeZone("UTC")
                dateSaved = dateFormatSaved.format(selectedDate.time)
                binding.signupBirthday.text = dateFormatShown.format(selectedDate.time)
                binding.signupBirthday.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun updateButtonBackground() {
        val filled = (binding.signupUsername.text.isNotEmpty()
                && binding.signupEmail.text.isNotEmpty()
                && binding.signupPass.text.isNotEmpty()
                && binding.signupName.text.isNotEmpty()
                && binding.signupRepeatPass.text.isNotEmpty())
                && dateState
                && (genderInt == 0 || genderInt == 1)
        if (filled) {
            binding.signupButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.gray_faded
                )
            )
            binding.signupButton.background =
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.input_bg_pressed
                )
        }
        else {
            binding.signupButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.accent
                )
            )
            binding.signupButton.background =
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.input_bg
                )
        }
    }

    private fun checkAllFields(): Boolean {
        if (binding.signupUsername.length() == 0) {
            Toast.makeText(requireContext(), getString(R.string.uname_mt), Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.signupEmail.length() == 0) {
            Toast.makeText(requireContext(), getString(R.string.email_mt), Toast.LENGTH_SHORT).show()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.signupEmail.text).matches()) {
            Toast.makeText(requireContext(), getString(R.string.email_invalid), Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.signupName.length() == 0) {
            Toast.makeText(requireContext(), getString(R.string.name_mt), Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.signupPass.length() == 0) {
            Toast.makeText(requireContext(), getString(R.string.pass_mt), Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.signupPass.length() < 6) {
            Toast.makeText(requireContext(), getString(R.string.pass_6), Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.signupRepeatPass.length() == 0) {
            Toast.makeText(requireContext(), getString(R.string.rep_pass_mt), Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.signupPass.text.toString() != binding.signupRepeatPass.text.toString()) {
            Toast.makeText(requireContext(), getString(R.string.pass_unmatched), Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.signupBirthday.text == getString(R.string.birthday)) {
            Toast.makeText(requireContext(), getString(R.string.birthday_mt), Toast.LENGTH_SHORT).show()
            return false
        } else if (genderInt == 3) {
            Toast.makeText(requireContext(), getString(R.string.gender_mt), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
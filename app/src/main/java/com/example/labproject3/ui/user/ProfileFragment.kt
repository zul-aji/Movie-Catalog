package com.example.labproject3.ui.user

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.labproject3.LoginActivity
import com.example.labproject3.R
import com.example.labproject3.data.TokenPreferences
import com.example.labproject3.data.network.APIRequest
import com.example.labproject3.data.network.Resource
import com.example.labproject3.data.repository.UserRepo
import com.example.labproject3.data.request.UserUpdateRequest
import com.example.labproject3.databinding.FragmentProfileBinding
import com.example.labproject3.ui.base.BaseFragment
import com.example.labproject3.ui.startNewActivity
import com.example.labproject3.ui.visible
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : BaseFragment<UserViewModel, FragmentProfileBinding, UserRepo>() {

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var idSaved: String
    private lateinit var dateSaved: String
    private var genderInt: Int = 3
    private var isAllFieldsChecked = false

    override fun getViewModel() = UserViewModel::class.java
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentProfileBinding.inflate(inflater, container, false)
    override fun getFragmentRepository() = UserRepo(dataSource.buildAPI(requireContext(), APIRequest::class.java))

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tokenPreferences = TokenPreferences(requireContext())
        binding.progressCircle.visible(false)

        val token = "Bearer " + tokenPreferences.getToken()
        viewModel.getUser(token)

        viewModel.getUserResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.progressCircle.visible(false)
                    idSaved = it.value.id
                    dateSaved = it.value.birthDate
                    val cutDate = dateSaved.substring(0, 9)
                    val splitDate = cutDate.split("-")
                    val dateShown = if (splitDate[2].length < 2 ) {
                        "0" + splitDate[2] + "." + splitDate[1] + "." + splitDate[0]
                    } else {
                        splitDate[2] + "." + splitDate[1] + "." + splitDate[0]
                    }
                    Glide.with(requireContext())
                        .load(it.value.avatarLink)
                        .error(R.drawable.ic_profile)
                        .into(binding.profilePic)
                    binding.profileName.text = it.value.nickName
                    binding.inputEmail.setText(it.value.email)
                    binding.inputAvlink.setText(it.value.avatarLink)
                    binding.inputName.setText(it.value.name)
                    binding.inputBirthday.text = dateShown
                    if (it.value.gender == 0) {
                        genderInt = 0
                        binding.maleButton.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.button_male_pressed)
                        binding.femaleButton.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.button_female)
                    } else {
                        genderInt = 1
                        binding.femaleButton.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.button_female_pressed)
                        binding.maleButton.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.button_male)
                    }
                }
                is Resource.Failure -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        requireContext(),
                        if (it.isNetworkError){ "Network Error" }
                        else { "Fail: ${it.errorMessage.toString()}" },
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Resource.Loading -> {
                    binding.progressCircle.visible(true)
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

        viewModel.logoutResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.progressCircle.visible(false)
                    tokenPreferences.removeToken()
                    requireActivity().startNewActivity(LoginActivity::class.java)
                    Toast.makeText(
                        requireContext(),
                        it.value.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Failure -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        requireContext(),
                        if (it.isNetworkError){ "Network Error" }
                        else { "Fail: ${it.errorMessage.toString()}" },
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Resource.Loading -> {
                    binding.progressCircle.visible(true)
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

        viewModel.upUserResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.progressCircle.visible(false)
                    Log.e("update", it.toString() )
                    Toast.makeText(
                        requireContext(),
                        it.value.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Failure -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        requireContext(),
                        if (it.isNetworkError){ "Profile Updated!" }
                        else { "Fail: ${it.errorMessage.toString()}" },
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Resource.Loading -> {
                    binding.progressCircle.visible(true)
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

        binding.inputBirthday.setOnClickListener {
            showDateDialog()
        }

        binding.maleButton.setOnClickListener {
            genderInt = 0
            binding.maleButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_male_pressed)
            binding.femaleButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_female)
        }
        binding.femaleButton.setOnClickListener {
            genderInt = 1
            binding.femaleButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_female_pressed)
            binding.maleButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.button_male)
        }

        binding.saveButton.setOnClickListener {
            val id = idSaved
            val nickName = binding.profileName.text.toString()
            val email = binding.inputEmail.text.toString()
            val avatarLink = binding.inputAvlink.text.toString().ifEmpty { " " }
            val name = binding.inputName.text.toString()
            val birthDate = dateSaved
            val gender = genderInt
            val user = UserUpdateRequest(id, nickName, email, avatarLink, name, birthDate, gender)

            Log.e("userDetails", user.toString() )

            isAllFieldsChecked = checkAllFields()
            if (isAllFieldsChecked){
                binding.progressCircle.visible(true)
                viewModel.updateUser(token, user)
            }
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
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
                binding.inputBirthday.text = dateFormatShown.format(selectedDate.time)
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun checkAllFields(): Boolean {
        if (binding.inputEmail.length() == 0) {
            Toast.makeText(requireContext(), getString(R.string.email_mt), Toast.LENGTH_SHORT).show()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text).matches()) {
            Toast.makeText(requireContext(), getString(R.string.email_invalid), Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.inputName.length() == 0) {
            Toast.makeText(requireContext(), getString(R.string.name_mt), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
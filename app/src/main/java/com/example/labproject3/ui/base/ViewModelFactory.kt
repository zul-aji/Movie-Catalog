package com.example.labproject3.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.labproject3.data.repository.UserRepo
import com.example.labproject3.data.repository.BaseRepo
import com.example.labproject3.data.repository.MoviesRepo
import com.example.labproject3.ui.details.MovieDetailsViewModel
import com.example.labproject3.ui.home.HomeViewModel
import com.example.labproject3.ui.user.UserViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repo: BaseRepo
    ) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(UserViewModel::class.java) -> UserViewModel(repo as UserRepo) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repo as MoviesRepo) as T
            modelClass.isAssignableFrom(MovieDetailsViewModel::class.java) -> MovieDetailsViewModel(repo as MoviesRepo) as T
            else -> throw IllegalArgumentException("ViewModelClass not found")
        }
    }
}